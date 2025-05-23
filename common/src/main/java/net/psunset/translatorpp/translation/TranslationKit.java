package net.psunset.translatorpp.translation;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.tool.ClientUtl;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TranslationKit {

    protected static TranslationKit INSTANCE;

    private static final AtomicInteger taskCounter = new AtomicInteger(0);
    private static final ExecutorService translationExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Translation-Worker-" + taskCounter.incrementAndGet());
        t.setDaemon(true); // Allow JVM to exit even if this thread is running
        return t;
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
    private ItemStack hoveredStack = null;
    @Nullable
    private ItemStack translatedStack = null;
    @Nullable
    private volatile MutableComponent translatedResult = null;
    private volatile boolean translated = false;
    private CompletableFuture<Void> translationFuture = null;

    public TranslationKit() {
    }

    public void setHoveredStack(@Nullable ItemStack stack) {
        this.hoveredStack = stack;
    }

    public @Nullable ItemStack getHoveredStack() {
        return hoveredStack;
    }

    public @Nullable ItemStack getTranslatedStack() {
        return translatedStack;
    }

    public @Nullable MutableComponent getTranslatedResult() {
        return translatedResult;
    }

    public boolean isTranslated() {
        return this.translated;
    }

    public void start(Minecraft client) {
        if (hoveredStack == null || hoveredStack.equals(translatedStack))
            return; // Already translating or translated this exact stack instance

        // Cancel any previous ongoing translation
        this.stop();

        translatedStack = hoveredStack;
        String originalText = translatedStack.getHoverName().getString();

        // Check cache first
        String cachedResult = translationCache.get(originalText);
        if (cachedResult != null) {
            TranslatorPP.LOGGER.debug("Cache hit for: {}", originalText);
            translatedResult = Component.translatable("misc.translatorpp.translation", cachedResult);
            translated = true;
            translationFuture = CompletableFuture.completedFuture(null); // Create a completed future
            return; // Skip API call
        }

        translatedResult = Component.translatable("misc.translatorpp.translation.waiting").withStyle(ChatFormatting.GRAY); // Initial placeholder
        translated = true; // Set translated flag immediately

        translationFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return TPPConfig.getInstance().getTranslationTool().getTool().translate(
                                originalText,
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
                    translatedResult = Component.translatable("misc.translatorpp.translation", it);
                    translationCache.put(originalText, it); // Add to cache
                }, translationExecutor)
                .exceptionally(err -> {
                    TranslatorPP.LOGGER.error("Translation failed for: {}. Cause: {}", originalText, err.getCause());
                    translatedResult = Component.translatable("misc.translatorpp.translation.failed").withStyle(ChatFormatting.RED);
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
            translatedStack = null;
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
        refreshOpenAIClientTool(TPPConfig.getInstance().getOpenaiApiKey(),
                TPPConfig.getInstance().getOpenaiBaseUrl(), TPPConfig.getInstance().getOpenaiModel());
    }

    public void refreshOpenAIClientTool(String apiKey, OpenAIClientTool.Api api, String model) {
        try {
            TranslatorPP.LOGGER.debug("Refreshing OpenAI Client Tool with {apikey={}, baseurl={}, model={}}",
                    apiKey.isBlank() ? "NOT SET" : "****" + apiKey.substring(apiKey.length() - 4), api.baseUrl, model); // Avoid logging full API key
            OpenAIClientTool.getInstance().setApiKey(apiKey);
            OpenAIClientTool.getInstance().setApi(api);
            OpenAIClientTool.getInstance().setModel(model.isBlank() ? api.defaultModel : model);
        } catch (Exception e) {
            TranslatorPP.LOGGER.error("Error while refreshing OpenAI Client Tool: {}", e.toString());
        }
    }

    public static void init() {
        TranslatorPP.LOGGER.debug("Initializing TranslationKit");
        INSTANCE = new TranslationKit();
        Runtime.getRuntime().addShutdownHook(new Thread(translationExecutor::shutdownNow));
    }
}