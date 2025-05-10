package net.psunset.translatorpp.translation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.ClientOptions;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.models.Model;
import net.minecraft.Util;
import net.psunset.translatorpp.TranslatorPP;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OpenAIClientTool implements TranslationTool {

    private static final OpenAIClientTool INSTANCE = new OpenAIClientTool();
    public static final String PROMPT = """
            I'm playing modded Minecraft.
            But there are some words I didn't understand.
            So, please translate the following words, "%s", from '%s' language to '%s' language.
            Also, because those words are from modded Minecraft, you can properly adjust your answer.
            Finally, the response you return *MUST* only contain the translated result. No other description.""";

    public static OpenAIClientTool getInstance() {
        return INSTANCE;
    }

    private Supplier<OpenAIOkHttpClient.Builder> clientBuilderSup;
    private OpenAIClient client;
    private String model;

    public OpenAIClientTool() {
    }

    @Override
    public String translate(String q, String sl, String tl) throws Exception {
        ChatCompletionCreateParams.Builder chatBuilder = ChatCompletionCreateParams.builder()
                .addUserMessage(PROMPT.formatted(q, sl, tl))
                .temperature(0.7)
                .model(this.model);
        return this._translate(chatBuilder);
    }

    public String _translate(ChatCompletionCreateParams.Builder chatBuilder) {
        return this.client()
                .chat()
                .completions()
                .create(chatBuilder.build())
                .choices().getFirst()
                .message()
                .content().get().strip();
    }

    public void setClientBuilderSup(Supplier<OpenAIOkHttpClient.Builder> clientBuilderSup) {
        this.clientBuilderSup = clientBuilderSup;
    }

    public OpenAIClient client() {
        return this.client;
    }

    public void refreshClient() {
        if (this.clientBuilderSup.get() == null) {
            this.client = null;
        } else {
            this.client = this.clientBuilderSup.get().build();
        }
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isPresent() {
        return this.client != null;
    }

    public void ifPresent(Consumer<OpenAIClient> consumer) {
        if (this.client != null) {
            consumer.accept(this.client);
        }
    }

    /**
     * Returns the model list caught online.
     */
    public Set<String> getModels() {
        try {
            Set<String> result = this.client().models().list().data().stream()
                    .map(Model::id)
                    .map(it -> it.replace("models/", ""))
                    .collect(Collectors.toSet());
            TranslatorPP.LOGGER.info(String.join(", ", result));
            return result;
        } catch (Exception e) {
            TranslatorPP.LOGGER.error("Error while getting online model list: {}", e.toString());
            return getModelOffline();
        }
    }

    /**
     * Returns a copy of {@link OpenAIClientTool#TEMP_AVAILABLE_MODEL_LIST}
     * A suck method that not be used now.
     * Hope we no longer use this method.
     */
    public Set<String> getModelOffline() {
        return Sets.newHashSet(TEMP_AVAILABLE_MODEL_LIST);
    }

    public enum Api {
        OpenAI(ClientOptions.PRODUCTION_URL, "gpt-4o-mini"),
        Gemini("https://generativelanguage.googleapis.com/v1beta/openai/", "gemini-2.0-flash"),
        Grok("https://api.x.ai/v1", "grok-3"),
        DeepSeek("https://api.deepseek.com/v1", "deepseek-chat");

        public static final Map<String, Api> entries = Util.make(Maps.newHashMap(), map -> {
            Arrays.asList(values()).forEach(it -> map.put(it.name(), it));
        });

        public final String baseUrl;
        public final String defaultModel;

        Api(String baseUrl, String defaultModel) {
            this.baseUrl = baseUrl;
            this.defaultModel = defaultModel;
        }
    }

    /**
     * A temp-available model list.
     * So this may not correct in the future.
     * If some models are deprecated or new models get updated, this list won't update in time.
     * This list is created on May 07, 2025.
     */
    private static final Set<String> TEMP_AVAILABLE_MODEL_LIST = Sets.newHashSet(
            // OpenAI
            "davinci-002",
            "gpt-4o-mini-2024-07-18",
            "gpt-4.5-preview",
            "tts-1-hd-1106",
            "gpt-4o-mini",
            "gpt-4.1-nano",
            "text-embedding-3-large",
            "o1-preview",
            "gpt-4o-mini-audio-preview-2024-12-17",
            "gpt-3.5-turbo-instruct-0914",
            "omni-moderation-2024-09-26",
            "tts-1-hd",
            "gpt-4o-search-preview",
            "gpt-3.5-turbo-1106",
            "gpt-4o-mini-search-preview-2025-03-11",
            "babbage-002",
            "gpt-image-1",
            "gpt-4o-mini-tts",
            "gpt-4.5-preview-2025-02-27",
            "gpt-4.1-mini-2025-04-14",
            "gpt-3.5-turbo",
            "o1-mini",
            "gpt-3.5-turbo-instruct",
            "dall-e-3",
            "dall-e-2",
            "gpt-4o",
            "o1-preview-2024-09-12",
            "gpt-4o-2024-11-20",
            "omni-moderation-latest",
            "gpt-4o-audio-preview-2024-10-01",
            "tts-1-1106",
            "tts-1",
            "gpt-4o-2024-05-13",
            "gpt-4o-search-preview-2025-03-11",
            "gpt-4o-2024-08-06",
            "text-embedding-3-small",
            "gpt-4o-audio-preview",
            "gpt-4o-mini-search-preview",
            "gpt-4o-mini-audio-preview",
            "gpt-4.1-nano-2025-04-14",
            "whisper-1",
            "gpt-4o-transcribe",
            "gpt-4.1-2025-04-14",
            "gpt-4.1",
            "gpt-4o-mini-transcribe",
            "text-embedding-ada-002",
            "gpt-4.1-mini",
            "gpt-3.5-turbo-16k",
            "gpt-3.5-turbo-0125",
            "o1-mini-2024-09-12",

            // Gemini
            "gemini-2.0-flash-lite-preview-02-05",
            "gemini-2.0-flash-001",
            "gemini-pro-vision",
            "imagen-3.0-generate-002",
            "gemini-2.5-pro-exp-03-25",
            "gemini-1.0-pro-vision-latest",
            "gemini-1.5-flash-latest",
            "gemma-3-1b-it",
            "gemini-2.5-flash-preview-04-17-thinking",
            "gemini-1.5-flash-001-tuning",
            "gemini-1.5-pro-002",
            "gemini-1.5-pro",
            "gemini-1.5-pro-001",
            "gemini-1.5-flash-8b-001",
            "embedding-gecko-001",
            "text-bison-001",
            "gemini-2.0-flash-exp",
            "gemini-2.0-flash",
            "aqa",
            "learnlm-1.5-pro-experimental",
            "gemini-2.5-flash-preview-04-17",
            "gemini-2.0-flash-thinking-exp",
            "gemini-embedding-exp",
            "gemini-1.5-flash-8b-exp-0827",
            "gemma-3-4b-it",
            "gemini-1.5-pro-latest",
            "gemini-1.5-flash-8b-latest",
            "text-embedding-004",
            "gemini-1.5-flash",
            "gemini-2.5-pro-preview-03-25",
            "gemini-2.5-pro-preview-05-06",
            "gemini-2.0-flash-thinking-exp-1219",
            "embedding-001",
            "gemma-3-27b-it",
            "gemini-2.0-flash-lite-preview",
            "gemini-2.0-pro-exp",
            "gemini-exp-1206",
            "gemini-2.0-flash-live-001",
            "gemini-2.0-flash-exp-image-generation",
            "gemini-embedding-exp-03-07",
            "gemini-2.0-pro-exp-02-05",
            "gemini-2.0-flash-lite",
            "gemini-1.5-flash-001",
            "gemini-2.0-flash-lite-001",
            "gemini-2.0-flash-thinking-exp-01-21",
            "gemini-1.5-flash-8b",
            "gemma-3-12b-it",
            "chat-bison-001",
            "gemini-1.5-flash-8b-exp-0924",
            "gemini-1.5-flash-002",
            "learnlm-2.0-flash-experimental",

            // Grok
            "grok-3-beta",
            "grok-3-fast-beta",
            "grok-3-mini-beta",
            "grok-3-mini-fast-beta",
            "grok-2-vision-1212",
            "grok-2-image-1212",
            "grok-2-1212",
            "grok-vision-beta",
            "grok-beta",

            // DeepSeek
            "deepseek-chat",
            "deepseek-reasoner"
    );
}
