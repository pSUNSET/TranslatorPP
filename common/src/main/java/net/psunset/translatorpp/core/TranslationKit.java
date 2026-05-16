package net.psunset.translatorpp.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.event.ItemTooltipCallbacks;
import net.psunset.translatorpp.event.ScreenCallbacks;
import net.psunset.translatorpp.exception.ServiceException;
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
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * The main class to manage all translation process.
 */
public final class TranslationKit {

    static final TranslationKit INSTANCE = new TranslationKit();
    static final Gson GSON = new GsonBuilder().create();

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
     * When there is no screen open, set to false.
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
                        return TPPConfig.getInstance().getService().provider.translate(
                                translatedText,
                                TPPConfig.getInstance().getSourceLanguage(),
                                TPPConfig.getInstance().getTargetLanguage()
                        );
                    } catch (Exception e) {
                        throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
                    }
                }, translationExecutor)
                .thenAcceptAsync(it -> {
                    // Update the result and cache it
                    translatedResult = it + SUCCESS;
                    translationCache.put(translatedText, it); // Add to cache
                }, translationExecutor)
                .exceptionally(err -> {
                    TranslatorPP.LOGGER.error("Translation failed for: {}. Cause: {}", translatedText, err.getCause());
                    translatedResult = I18n.get("misc.translatorpp.translation.failed") + ERROR;
                    this.clientExecuteSendingError(client, err.getCause());
                    return null; // Indicate exception was handled
                });
    }

    private void clientExecuteSendingError(Minecraft client, Throwable throwable) {
        client.execute(() -> sendErrorToClient(client, throwable));
    }

    private void sendErrorToClient(Minecraft client, Throwable err) {
        if (err instanceof ServiceException se) {
            ClientUtl.message(client, Component.translatable("misc.translatorpp.translation.failed.chat.status_code",
                    se.statusCode, se.getMessage()).withStyle(ChatFormatting.RED));
        } else {
            ClientUtl.message(client, Component.translatable("misc.translatorpp.translation.failed.chat", err.toString()).withStyle(ChatFormatting.RED));
        }
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

        String[] texts = resultText.substring(0, resultText.length() - 3).split(literalSeparator());
        return Pair.of(appliedStyle, texts);
    }

    /**
     * Add the translation result to the component list.
     */
    private void addResultToTooltip(List<Component> lines) {
        var styledResult = getStyledResultLines();
        Style appliedStyle = styledResult.getLeft();
        String[] texts = styledResult.getRight();

        switch (TPPConfig.getInstance().getMode()) {
            case NAME_ONLY -> {
                lines.add(1, Component.literal(texts[0]).withStyle(appliedStyle));
            }
            case NAME_TOP -> {
                lines.add(1, Component.literal(texts[0]).withStyle(appliedStyle));
                if (texts.length > 1) {
                    // 15 < ${max_length_of_lines} < 30
                    lines.add(Component.literal("-".repeat(Mth.clamp(Arrays.stream(texts).map(String::length).flatMapToInt(IntStream::of).max().getAsInt(), 15, 30))).withStyle(ChatFormatting.DARK_GRAY));
                    for (int i = 1; i < texts.length; i++) {
                        lines.add(Component.literal(texts[i]).withStyle(appliedStyle));
                    }
                }
            }
            case ALL_IN_END -> {
                // 15 < ${max_length_of_lines} < 30
                lines.add(Component.literal("-".repeat(Mth.clamp(Arrays.stream(texts).map(String::length).flatMapToInt(IntStream::of).max().getAsInt(), 15, 30))).withStyle(ChatFormatting.DARK_GRAY));
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

    public static String separator() {
        return TPPConfig.getInstance().getService().provider.separator();
    }

    public static String literalSeparator() {
        return Pattern.quote(separator());
    }
}