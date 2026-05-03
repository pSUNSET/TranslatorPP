package net.psunset.translatorpp.core;

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

public class LibreTranslateProvider implements IServiceProvider {
    static final LibreTranslateProvider INSTANCE = new LibreTranslateProvider();
    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 30000;    // 30 seconds

    public static LibreTranslateProvider getInstance() {
        return INSTANCE;
    }

    @NotNull
    private String apiKey = "";
    @NotNull
    private String baseUrl = "";

    private LibreTranslateProvider() {
    }

    private void setApiKey(@NotNull String apiKey) {
        this.apiKey = apiKey;
    }

    private void setBaseUrl(@NotNull String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl:baseUrl + "/";
    }

    public @NotNull String getBaseUrl() {
        return this.baseUrl;
    }

    @Override
    public String separator() {
        return "\n";
    }

    /**
     * Refresh self properties with the latest configuration.
     */
    public void refresh() {
        TPPConfig config = TPPConfig.getInstance();
        safeRefresh(config.getLibreApiKey(), config.getLibreBaseUrl());
    }

    /**
     * Refresh self properties.
     * Auto catch exceptions and log errors.
     */
    private void safeRefresh(String apiKey, String baseUrl) {
        try {
            this.unsafeRefresh(apiKey, baseUrl);
            String shownApiKey = this.apiKey.isEmpty() ? "NOT_SET":"****" + this.apiKey.substring(apiKey.length() - 4); // Avoid logging full API key
            TranslatorPP.LOGGER.debug("LibreTranslateProvider is currently set to {apiKey={}, baseUrl={}}", shownApiKey, this.baseUrl);
        } catch (Exception e) {
            TranslatorPP.LOGGER.error("Error while refreshing LibreTranslateProvider: {}", e.toString());
        }
    }

    /**
     * Refresh self properties.
     *
     * @throws IllegalArgumentException if {@code api} is {@code Custom} and {@code customApiUrl} is {@code null}.
     */
    void unsafeRefresh(String apiKey, String baseUrl) {
        this.setApiKey(apiKey.isBlank() ? "":apiKey.strip());
        if (baseUrl.isBlank()) {
            this.setBaseUrl("");
            throw new IllegalArgumentException("Base URL must be provided.");
        } else {
            this.setBaseUrl(baseUrl);
        }
    }

    @Override
    public String translate(String q, String sl, String tl) throws IOException {
        JsonObject requestBody = new JsonObject();
        String qText = String.join(this.separator(), q);
        requestBody.addProperty("q", qText);
        requestBody.addProperty("target", tl);
        requestBody.addProperty("source", sl);
        requestBody.addProperty("format", "text");
        if (!this.apiKey.isBlank()) {
            requestBody.addProperty("api_key", this.apiKey);
        }

        String jsonRequestBody = requestBody.toString();

        HttpURLConnection con = null;
        try {
            URL url = URI.create(this.baseUrl + "translate").toURL();
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
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
                String error = responseJson.get("error").getAsString();
                throw new ServiceException.Libre(error, statusCode);
            }

            return responseJson.getAsJsonPrimitive("translatedText").getAsString().trim();
        } finally {
            if (con!=null) {
                con.disconnect();
            }
        }
    }
}
