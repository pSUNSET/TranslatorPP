package net.psunset.translatorpp.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.api.IServiceProvider;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.exception.ServiceException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DeepLTranslationProvider implements IServiceProvider {
    static final DeepLTranslationProvider INSTANCE = new DeepLTranslationProvider();

    private static final String API_URL = "https://api-free.deepl.com/v2/translate";
    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 30000;    // 30 seconds

    public static DeepLTranslationProvider getInstance() {
        return INSTANCE;
    }

    @NotNull
    private String apiKey = "";

    private DeepLTranslationProvider() {
    }

    private void setApiKey(@NotNull String apiKey) {
        this.apiKey = apiKey;
    }


    /**
     * Refresh self properties with the latest configuration.
     */
    public void refresh() {
        TPPConfig config = TPPConfig.getInstance();
        String apiKey = config.getDeepLApiKey();
        this.setApiKey(apiKey.isBlank() ? "" : apiKey.strip());
        String shownApiKey = this.apiKey.isEmpty() ? "NOT_SET" : "****" + this.apiKey.substring(apiKey.length() - 4); // Avoid logging full API key
        TranslatorPP.LOGGER.debug("DeepLTranslationProvider is currently set to {apiKey={}}", shownApiKey);
    }

    @Override
    public String translate(String q, String sl, String tl) throws IOException {
        if (!this.isPresent()) {
            throw new IllegalStateException("DeepLTranslationProvider is not completely configured. API key must be set.");
        }

        JsonObject requestBody = new JsonObject();
        JsonArray text = new JsonArray();
        text.add(q);
        requestBody.add("text", text);
        requestBody.addProperty("target_lang", tl.toUpperCase());
        requestBody.addProperty("source_lang", sl.toUpperCase());

        String jsonRequestBody = requestBody.toString();

        HttpURLConnection con = null;
        try {
            URL url = URI.create(API_URL).toURL();
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "DeepL-Auth-Key " + this.apiKey);
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(CONNECT_TIMEOUT);
            con.setReadTimeout(READ_TIMEOUT);
            con.setDoOutput(true);

            try (DataOutputStream dos = new DataOutputStream(con.getOutputStream())) {
                dos.write(jsonRequestBody.getBytes(StandardCharsets.UTF_8));
            }

            int statusCode = con.getResponseCode();
            StringBuilder responseBodyBuilder = new StringBuilder();
            boolean isError = statusCode < 200 || statusCode >= 300;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    isError ? con.getErrorStream():con.getInputStream(),
                    StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine())!=null) {
                    responseBodyBuilder.append(responseLine);
                }
            }
            String rawResponse = responseBodyBuilder.toString();

            JsonObject responseJson = TranslationKit.GSON.fromJson(rawResponse, JsonObject.class);

            if (isError) {
                String message = responseJson.getAsJsonPrimitive("message").getAsString();
                throw new ServiceException.DeepL(message, statusCode);
            }

            JsonArray translations = responseJson.getAsJsonArray("translations");

            if (translations==null || translations.isEmpty()) {
                throw new IOException("Invalid response: 'translations' array not found or empty. Response: " + rawResponse);
            }

            StringBuilder result = new StringBuilder();
            for (JsonElement element : translations) {
                result.append(element.getAsJsonObject().getAsJsonPrimitive("text").getAsString().trim());
            }

            return result.toString();
        } finally {
            if (con!=null) {
                con.disconnect();
            }
        }
    }

    /**
     * Returns true if all necessary properties are set.
     */
    public boolean isPresent() {
        return !this.apiKey.isEmpty();
    }
}
