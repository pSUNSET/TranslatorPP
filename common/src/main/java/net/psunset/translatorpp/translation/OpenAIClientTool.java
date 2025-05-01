package net.psunset.translatorpp.translation;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

public class OpenAIClientTool extends AbstractTranslationClientTool {

    private static final OpenAIClientTool INSTANCE = new OpenAIClientTool();

    public static OpenAIClientTool getInstance() {
        return INSTANCE;
    }

    public OpenAIClient client;

    public OpenAIClientTool() {
    }

    @Override
    protected String _translate(String q, String sl, String tl) throws Exception {
        return "";
    }

    public void init() {

    }
}
