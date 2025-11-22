package net.psunset.translatorpp.translation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.event.ItemTooltipCallbacks;
import net.psunset.translatorpp.event.ScreenCallbacks;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.tool.ClientUtl;
import net.psunset.translatorpp.tool.TooltipUtl;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class TranslationKit {

    static final TranslationKit INSTANCE = new TranslationKit();

    public static final String SEPARATOR = "<@>";
    public static final String SUCCESS = "<O>";
    public static final String PROCESSING = "<?>";
    public static final String ERROR = "<X>";

    private static final AtomicInteger taskCounter = new AtomicInteger(0);
    private static final ExecutorService translationExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread translationThread = new Thread(r, "Translation-Worker-" + taskCounter.incrementAndGet());
        translationThread.setDaemon(true); // Allow JVM to exit even if this thread is running
        return translationThread;
    });

    public static TranslationKit getInstance() {
        return INSTANCE;
    }

    // TODO: Make cache size configurable
    private static final int MAX_CACHE_SIZE = 100;

    /**
     * A cache with LRU eviction policy to store recent translations.
     */
    private final Map<String, String> translationCache = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            }
    );

    /**
     * The text currently being hovered over, null if none.
     */
    @Nullable
    private String hoveredText = null;

    /**
     * The text that is being translated, null if not translating yet.
     */
    @Nullable
    private String translatedText = null;

    /**
     * The result of the translation, null if not translated yet.
     */
    @Nullable
    private volatile String translatedResult = null;

    /**
     * Whether a translation is in progress or completed.
     */
    private volatile boolean translated = false;

    /**
     * Used when a screen open, key.isDown() won't work.
     * When there is no screen open, set to null.
     */
    private boolean translateKeyDown = false;

    private CompletableFuture<Void> translationFuture = null;

    private TranslationKit() {
    }

    public @Nullable String getHoveredText() {
        return hoveredText;
    }

    public void setHoveredText(@Nullable ItemStack stack, Minecraft client) {
        if (stack == null) {
            this.hoveredText = null;
            return;
        }
        this.hoveredText = TooltipUtl.getCombinedTooltipText(stack, client);
    }

    public void setHoveredText(List<? extends FormattedText> tooltip) {
        if (tooltip.isEmpty()) {
            this.hoveredText = null;
            return;
        }
        this.hoveredText = TooltipUtl.getCombinedTooltipText(tooltip);
    }

    public void setHoveredText(@Nullable String text) {
        this.hoveredText = text;
    }

    public @Nullable String getTranslatedText() {
        return translatedText;
    }

    public @Nullable String getTranslatedResult() {
        return translatedResult;
    }

    public boolean isTranslated() {
        return translated;
    }

    public boolean isKeyDown() {
        return translateKeyDown;
    }

    private void setKeyDown(boolean isKeyDown) {
        translateKeyDown = isKeyDown;
    }

    /**
     * Start translating the currently hovered text.
     */
    public void start(Minecraft client) {
        if (hoveredText == null || hoveredText.equals(translatedText)) {
            // Already translating or translated this exact stack instance
            return;
        }

        // Cancel any previous ongoing translation
        this.stop();

        translatedText = hoveredText;

        // Check cache first
        String cachedResult = translationCache.get(translatedText);
        if (cachedResult != null) {
            TranslatorPP.LOGGER.debug("Cache hit for: {}", translatedText);
            translatedResult = I18n.get("misc.translatorpp.translation", cachedResult) + SUCCESS;
            translated = true;
            translationFuture = CompletableFuture.completedFuture(null); // Create a completed future
            return; // Skip API call
        }

        translatedResult = I18n.get("misc.translatorpp.translation.processing") + PROCESSING; // Initial placeholder
        translated = true; // Set translated flag immediately

        translationFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return TPPConfig.getInstance().getTranslationTool().tool.translate(
                                translatedText,
                                TPPConfig.getInstance().getSourceLanguage(),
                                TPPConfig.getInstance().getTargetLanguage()
                        );
                    } catch (Exception e) {
                        if (e instanceof RuntimeException re) {
                            throw re;
                        } else {
                            throw new RuntimeException(e);
                        }
                    }
                }, translationExecutor)
                .thenAcceptAsync(it -> {
                    // Update the result and cache it
                    translatedResult = I18n.get("misc.translatorpp.translation", it) + SUCCESS;
                    translationCache.put(translatedText, it); // Add to cache
                }, translationExecutor)
                .exceptionally(err -> {
                    TranslatorPP.LOGGER.error("Translation failed for: {}. Cause: {}", translatedText, err.getCause());
                    translatedResult = I18n.get("misc.translatorpp.translation.failed") + ERROR;
                    this.sendErrorToClient(client, err.getCause());
                    return null; // Indicate exception was handled
                });
    }

    private void sendErrorToClient(Minecraft client, Throwable err) {
        if (err instanceof OpenAIClientTool.ServiceException openaiErr) {
            String transKey = "misc.translatorpp.translation.failed.chat.openai." + openaiErr.statusCode;
            if (openaiErr.statusCode == 401 && openaiErr.getMessage().contains("organization")) {
                transKey += "_org";
            } else if (openaiErr.statusCode == 429 && openaiErr.getMessage().contains("limit reached")) {
                transKey += "_limit";
            } else if (openaiErr.statusCode == 503 && openaiErr.getMessage().contains("overloaded")) {
                transKey += "_over";
            }
            ClientUtl.message(client, Component.translatable(transKey).withStyle(ChatFormatting.RED));
            return;
        }
        ClientUtl.message(client, Component.translatable("misc.translatorpp.translation.failed.chat", err.toString()).withStyle(ChatFormatting.RED));
    }

    /**
     * Stop any ongoing translation and clear the translated state.
     */
    public void stop() {
        if (this.translated) {
            if (translationFuture != null && !translationFuture.isDone()) {
                translationFuture.cancel(true);
            }
            translated = false;
            translatedText = null;
            translatedResult = null;
            translationFuture = null;
        }
    }

    /**
     * Clear the translation cache.
     */
    public void clearCache() {
        this.translationCache.clear();
    }

    /**
     * Refresh the OpenAI client tool with the latest configuration.
     * @deprecated Simply use {@link OpenAIClientTool#refresh()} instead.
     */
    @Deprecated
    public void refreshOpenAIClientTool() {
        TPPConfig config = TPPConfig.getInstance();
        refreshOpenAIClientTool(config.getOpenaiApiKey(), config.getOpenaiBaseUrl(), config.getOpenaiCustomBaseUrl(), config.getOpenaiModel());
    }

    /**
     * Refresh the OpenAI client tool.
     * @deprecated Simply use {@link OpenAIClientTool#safeRefresh(String, OpenAIClientTool.Api, String, String)} instead.
     */
    @Deprecated
    private void refreshOpenAIClientTool(String apiKey, OpenAIClientTool.Api api, String customApi, String model) {
        try {
            TranslatorPP.LOGGER.debug("Refreshing OpenAI Client Tool with {apikey={}, baseurl={}, model={}}",
                    apiKey.isBlank() ? "NOT_SET" : "****" + apiKey.substring(apiKey.length() - 4), api.baseUrl, model); // Avoid logging full API key
            OpenAIClientTool.getInstance().unsafeRefresh(apiKey, api, customApi, model);
        } catch (Exception e) {
            TranslatorPP.LOGGER.error("Error while refreshing OpenAI Client Tool: {}", e.toString());
        }
    }

    /**
     * Get the default style and split result lines.
     */
    public Pair<Style, String[]> getStyledResultLines() {
        Style appliedStyle = Style.EMPTY;
        String resultText = this.translatedResult;

        switch (resultText.substring(resultText.length() - 3)) {
            case PROCESSING -> appliedStyle = appliedStyle.withColor(ChatFormatting.DARK_GRAY);
            case ERROR -> appliedStyle = appliedStyle.withColor(ChatFormatting.RED);
            default -> appliedStyle = appliedStyle.withColor(ChatFormatting.GRAY); // SUCCESS
        }

        String[] texts = resultText.substring(0, resultText.length() - 3).split(SEPARATOR);
        return Pair.of(appliedStyle, texts);
    }

    /**
     * Add the translation result to the component list.
     */
    private void addResultToTooltip(List<Component> lines) {
        var styledResult = getStyledResultLines();
        Style appliedStyle = styledResult.getLeft();
        String[] texts = styledResult.getRight();

        switch (TPPConfig.getInstance().getTranslationMode()) {
            case NAME_ONLY -> {
                lines.add(1, Component.literal(texts[0]).withStyle(appliedStyle));
            }
            case NAME_TOP -> {
                lines.add(1, Component.literal(texts[0]).withStyle(appliedStyle));
                if (texts.length > 1) {
                    // 15 < ${max_length_of_lines} < 30
                    lines.add(Component.literal("-".repeat(Math.min(30, Math.max(15, Arrays.stream(texts).map(String::length).flatMapToInt(IntStream::of).max().getAsInt())))).withStyle(ChatFormatting.DARK_GRAY));
                    for (int i = 1; i < texts.length; i++) {
                        lines.add(Component.literal(texts[i]).withStyle(appliedStyle));
                    }
                }
            }
            case ALL_IN_END -> {
                // 15 < ${max_length_of_lines} < 30
                lines.add(Component.literal("-".repeat(Math.min(30, Math.max(15, Arrays.stream(texts).map(String::length).flatMapToInt(IntStream::of).max().getAsInt())))).withStyle(ChatFormatting.DARK_GRAY));
                for (String text : texts) {
                    lines.add(Component.literal(text).withStyle(appliedStyle));
                }
            }
            case LINE_BY_LINE -> {
                for (int i = 0; i < texts.length; i++) {
                    lines.add(i * 2 + 1, Component.literal(texts[i]).withStyle(appliedStyle));
                }
            }
        }
    }

    /**
     * Create a component with translation result for chat.
     */
    public Component createResultForChat() {
        var styledResult = getStyledResultLines();
        Style appliedStyle = styledResult.getLeft();
        String[] texts = styledResult.getRight();

        var component = Component.literal("").withStyle(appliedStyle);
        for (String text : texts) {
            component.append(text);
        }
        return component;
    }

    @Environment(EnvType.CLIENT)
    public static void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(translationExecutor::shutdownNow));

        ItemTooltipCallbacks.EVENT.register((stack, flag, lines) -> {
            TranslationKit.getInstance().setHoveredText(lines);

            if (TranslationKit.getInstance().isTranslated() &&
                    TranslationKit.getInstance().getTranslatedResult() != null &&
                    TooltipUtl.getCombinedTooltipText(lines).equals(TranslationKit.getInstance().translatedText)) {
                TranslationKit.getInstance().addResultToTooltip(lines);
            }
        });

        ScreenCallbacks.KEY_PRESSED_POST.register((screen, key, scancode, modifiers) -> {
            if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                TranslationKit.getInstance().start(Minecraft.getInstance());
                TranslationKit.getInstance().setKeyDown(true);
            }
        });

        ScreenCallbacks.KEY_RELEASED_POST.register(((screen, key, scancode, modifiers) -> {
            if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                TranslationKit.getInstance().stop();
                TranslationKit.getInstance().setKeyDown(false);
            }
        }));

        ScreenCallbacks.REMOVED.register(screen -> {
            TranslationKit.getInstance().stop();
            TranslationKit.getInstance().setKeyDown(false);
        });
    }
}