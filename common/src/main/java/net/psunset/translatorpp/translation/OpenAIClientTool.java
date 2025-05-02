package net.psunset.translatorpp.translation;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

import java.util.function.Supplier;

public class OpenAIClientTool extends AbstractTranslationClientTool {

    private static final OpenAIClientTool INSTANCE = new OpenAIClientTool();

    public static OpenAIClientTool getInstance() {
        return INSTANCE;
    }

    private Supplier<OpenAIOkHttpClient.Builder> clientBuilderSup;
    private OpenAIClient client;

    public OpenAIClientTool() {
    }

    @Override
    public String _translate(String q, String sl, String tl) throws Exception {
        return "";
    }

    public void setClientBuilderSup(Supplier<OpenAIOkHttpClient.Builder> clientBuilderSup) {
        this.clientBuilderSup = clientBuilderSup;
    }

    public OpenAIClient presentClient() {
        return this.client;
    }

    public void refreshClient() {
        this.client = this.clientBuilderSup.get().build();
    }
}
