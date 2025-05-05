package net.psunset.translatorpp.translation;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.ClientOptions;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.models.Model;
import net.psunset.translatorpp.TranslatorPP;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OpenAIClientTool extends AbstractTranslationClientTool {

    private static final OpenAIClientTool INSTANCE = new OpenAIClientTool();
    public static final String PROMPT = """
            I'm playing modded Minecraft.
            But there are some words I didn't understand.
            So, please translate the following words, "%s", from '%s' language to '%s' language.
            Also, because those words are got from modded Minecraft, you can check out whether the translated result is appropriate.
            The response you return *MUST* only contain the translated result. No other description.""";

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

    public Set<String> getModels() {
        try {
            return this.client().models().list().data().stream()
                    .map(Model::id)
                    .map(it -> it.replace("models/", ""))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            TranslatorPP.LOGGER.error("Error while getting OpenAI model list: {}", e.toString());
            return new HashSet<>();
        }
    }

    public enum BaseUrl {
        OpenAI(ClientOptions.PRODUCTION_URL, "gpt-4o-mini"),
        Gemini("https://generativelanguage.googleapis.com/v1beta/openai/", "gemini-2.0-flash"),
        Grok("https://api.x.ai/v1", "grok-3"),
        DeepSeek("https://api.deepseek.com/v1", "deepseek-chat");

        public final String url;
        public final String defaultModel;

        BaseUrl(String url, String defaultModel) {
            this.url = url;
            this.defaultModel = defaultModel;
        }
    }
}
