package net.psunset.translatorpp.translation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.event.ItemTooltipCallbacks;
import net.psunset.translatorpp.event.ScreenCallbacks;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.tool.ClientUtl;
import net.psunset.translatorpp.tool.TooltipUtl;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class TranslationKit {

    protected static TranslationKit INSTANCE;

    public static final String COMPONENT_SEP = "<@>";
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

    // LRU Cache implementation
    private static final int MAX_CACHE_SIZE = 100;
    private final Map<String, String> translationCache = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > MAX_CACHE_SIZE; // Remove eldest if size exceeds limit
                }
            }
    );

    @Nullable
    private String hoveredText = null;
    @Nullable
    private String translatedText = null;
    @Nullable
    private volatile String translatedResult = null;
    private volatile boolean translated = false;
    private boolean translateKeyDown = false;
    private CompletableFuture<Void> translationFuture = null;

    public TranslationKit() {
    }

    public void setHoveredText(@Nullable ItemStack stack, Minecraft client) {
        if (stack == null) {
            this.hoveredText = null;
            return;
        }
        this.hoveredText = TooltipUtl.getCombinedTooltipTexts(stack, client);
    }

    public void setHoveredText(List<Component> tooltip) {
        if (tooltip.isEmpty()) {
            this.hoveredText = null;
            return;
        }
        this.hoveredText = TooltipUtl.getCombinedTooltipTexts(tooltip);
    }

    public void setHoveredText(@Nullable String text) {
        this.hoveredText = text;
    }

    public @Nullable String getHoveredText() {
        return hoveredText;
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

    public boolean isTranslateKeyDown() {
        return translateKeyDown;
    }

    public void setTranslateKeyDown(boolean isKeyDown) {
        translateKeyDown = isKeyDown;
    }

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
                        return TPPConfig.getInstance().getTranslationTool().getTool().translate(
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

    public void clearCache() {
        this.translationCache.clear();
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

    public void refreshOpenAIClientTool() {
        refreshOpenAIClientTool(TPPConfig.getInstance().getOpenaiApiKey(), TPPConfig.getInstance().getOpenaiBaseUrl(),
                TPPConfig.getInstance().getOpenaiCustomBaseUrl(), TPPConfig.getInstance().getOpenaiModel());
    }

    public void refreshOpenAIClientTool(String apiKey, OpenAIClientTool.Api api, String customApi, String model) {
        try {
            TranslatorPP.LOGGER.debug("Refreshing OpenAI Client Tool with {apikey={}, baseurl={}, model={}}",
                    apiKey.isBlank() ? "NOT SET" : "****" + apiKey.substring(apiKey.length() - 4), api.baseUrl, model); // Avoid logging full API key
            OpenAIClientTool.getInstance().setApi(apiKey, api, customApi, model);
        } catch (Exception e) {
            TranslatorPP.LOGGER.error("Error while refreshing OpenAI Client Tool: {}", e.toString());
        }
    }

    public void addResultToTooltip(List<Component> lines) {

        Style appliedStyle = Style.EMPTY;

        switch (translatedResult.substring(translatedResult.length() - 3)) {
            case PROCESSING -> appliedStyle = appliedStyle.withColor(ChatFormatting.DARK_GRAY);
            case ERROR -> appliedStyle = appliedStyle.withColor(ChatFormatting.RED);
            default -> appliedStyle = appliedStyle.withColor(ChatFormatting.GRAY); // SUCCESS
        }

        String combinedText = translatedResult.substring(0, translatedResult.length() - 3);
        String[] texts = combinedText.split(COMPONENT_SEP);

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

    @Environment(EnvType.CLIENT)
    public static void init() {
        TranslatorPP.LOGGER.debug("Initializing TranslationKit");
        INSTANCE = new TranslationKit();
        Runtime.getRuntime().addShutdownHook(new Thread(translationExecutor::shutdownNow));

        ItemTooltipCallbacks.EVENT.register((stack, tooltipContext, flag, lines) -> {
            TranslationKit.getInstance().setHoveredText(lines);

            if (TranslationKit.getInstance().isTranslated() &&
                    TranslationKit.getInstance().getTranslatedResult() != null &&
                    TooltipUtl.getCombinedTooltipTexts(lines).equals(TranslationKit.getInstance().translatedText)) {
                TranslationKit.getInstance().addResultToTooltip(lines);
            }
        });

        ScreenCallbacks.KEY_PRESSED_POST.register((screen, key, scancode, modifiers) -> {
            if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                TranslationKit.getInstance().start(Minecraft.getInstance());
            }
        });

        ScreenCallbacks.KEY_RELEASED_POST.register(((screen, key, scancode, modifiers) -> {
            if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                TranslationKit.getInstance().stop();
            }
        }));

        ScreenCallbacks.REMOVED.register(screen -> {
            TranslationKit.getInstance().stop();
        });
    }
}