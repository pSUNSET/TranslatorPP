package net.psunset.translatorpp.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
import java.util.List;
import java.util.Set;

public class OllamaClientProvider implements IServiceProvider {

    static final OllamaClientProvider INSTANCE = new OllamaClientProvider();

    /**
     * Grabbing the model list from online costs too much time.
     * So create a cache here to get it more swiftly.
     */
    private static final Set<String> cacheModels = Sets.newHashSet();

    private static final String DEFAULT_BASE_URL = "http://127.0.0.1:11434/";
    private static final String DEFAULT_MODEL = "qwen3:4b";
    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 30000;    // 30 seconds

    public static OllamaClientProvider getInstance() {
        return INSTANCE;
    }

    @NotNull
    private String baseUrl = "";
    @NotNull
    private String model = "";

    private OllamaClientProvider() {
    }

    private void setBaseUrl(@NotNull String baseUrl) {
        if (baseUrl.isBlank()) {
            this.baseUrl = DEFAULT_BASE_URL;
            return;
        }
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    public @NotNull String getBaseUrl() {
        return this.baseUrl;
    }

    private void setModel(@NotNull String model) {
        if (model.isBlank()) {
            this.model = DEFAULT_MODEL;
            return;
        }
        this.model = model;
    }

    public @NotNull String getModel() {
        return this.model;
    }

    /**
     * Refresh self properties with the latest configuration.
     */
    public void refresh() {
        TPPConfig config = TPPConfig.getInstance();
        safeRefresh(config.getOllamaBaseUrl(), config.getOllamaModel());
    }

    /**
     * Refresh self properties.
     * Auto catch exceptions and log errors.
     */
    private void safeRefresh(String baseUrl, String model) {
        try {
            this.unsafeRefresh(baseUrl, model);
            TranslatorPP.LOGGER.debug("OllamaLocalProvider is currently set to {baseUrl={}, model={}}", this.baseUrl, this.model);
        } catch (Exception e) {
            TranslatorPP.LOGGER.error("Error while refreshing OllamaLocalProvider: {}", e.toString());
        }
    }

    /**
     * Refresh self properties.
     *
     * @throws IllegalArgumentException if {@code api} is {@code Custom} and {@code customApiUrl} is {@code null}.
     */
    void unsafeRefresh(String baseUrl, String model) {
        if (baseUrl.isBlank()) {
            this.setBaseUrl("");
            throw new IllegalArgumentException("Base URL must be provided.");
        } else {
            this.setBaseUrl(baseUrl);
        }
        this.setModel(model.isBlank() ? "" : model.strip());
    }

    @Override
    public String translate(String q, String sl, String tl) throws Exception {
        if (!this.isPresent()) {
            throw new IllegalStateException("OllamaLocalProvider is not completely configured. Base URL and model must be set.");
        }

        String formattedPrompt = OpenAIClientProvider.PROMPT.formatted(sl, tl, this.separator(), q);

        JsonObject requestPayload = new JsonObject();
        requestPayload.addProperty("model", this.model);
        requestPayload.addProperty("stream", false);
        JsonArray messages = new JsonArray();
        JsonObject firstMessage = new JsonObject();
        firstMessage.addProperty("role", "user");
        firstMessage.addProperty("content", formattedPrompt);
        messages.add(firstMessage);
        requestPayload.add("messages", messages);

        String jsonRequestBody = TranslationKit.GSON.toJson(requestPayload);

        HttpURLConnection con = null;
        try {
            URL url = URI.create(this.baseUrl + "api/chat").toURL();
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
                    isError ? con.getErrorStream() : con.getInputStream(),
                    StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBodyBuilder.append(responseLine);
                }
            }
            String rawResponse = responseBodyBuilder.toString();

            if (isError) {
                throw new ServiceException.Ollama(rawResponse, statusCode);
            }

            JsonObject responseJson = TranslationKit.GSON.fromJson(rawResponse, JsonObject.class);
            JsonObject message = responseJson.getAsJsonObject("message");
            if (message == null || !message.has("content")) {
                throw new IOException("Invalid response: 'message' or 'content' object not found. Response: " + rawResponse);
            }

            return message.get("content").getAsString().trim();

        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    /**
     * Returns true if all necessary properties are set.
     */
    public boolean isPresent() {
        return !this.baseUrl.isEmpty() && !this.model.isEmpty();
    }

    /**
     * This is a private method.
     * Please use {@link #refreshCacheModels()} and {@link #getCacheModels()} instead.
     * <br>
     * Returns the model list from online if possible; otherwise, returns the offline one.
     */
    private Set<String> getModels() {
        if (this.baseUrl.isEmpty()) {
            TranslatorPP.LOGGER.warn("Error while getting online model list: Base URL not set, using offline one instead.");
            return getModelListOffline();
        }

        HttpURLConnection con = null;
        try {
            URL url = URI.create(this.baseUrl + "api/tags").toURL();
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setConnectTimeout(CONNECT_TIMEOUT);
            con.setReadTimeout(READ_TIMEOUT);

            int statusCode = con.getResponseCode();
            StringBuilder responseBodyBuilder = new StringBuilder();
            boolean isError = statusCode < 200 || statusCode >= 300;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    isError ? con.getErrorStream() : con.getInputStream(),
                    StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBodyBuilder.append(responseLine);
                }
            }
            String rawResponse = responseBodyBuilder.toString();

            String snippet = rawResponse;
            if (snippet.length() > 500) {
                snippet = snippet.substring(0, 500) + "...";
            }
            if (isError) {
                TranslatorPP.LOGGER.error("Got {} error while getting online model list: {}", statusCode, snippet);
                return getModelListOffline();
            }

            Set<String> modelIds = Sets.newHashSet();
            JsonObject responseJson = TranslationKit.GSON.fromJson(rawResponse, JsonObject.class);
            JsonArray models = responseJson.getAsJsonArray("models");

            if (models != null && !models.isEmpty()) {
                for (JsonElement modelElement : models) {
                    JsonObject modelObject = modelElement.getAsJsonObject();
                    if (modelObject.has("name")) {
                        modelIds.add(modelObject.getAsJsonPrimitive("name").getAsString());
                    }
                }
            } else {
                TranslatorPP.LOGGER.warn("No 'models' array found or it's an empty array. Response: {}", snippet);
            }

            return modelIds;

        } catch (JsonSyntaxException e) {
            TranslatorPP.LOGGER.error("JSON syntax error while parsing models list: {}. Response: {}", e.getMessage(), (con != null && con.getDoInput() ? "Response too long or unreadable" : "No response available or error during read"));
            return getModelListOffline();
        } catch (Exception e) {
            TranslatorPP.LOGGER.error("Exception while getting online model list: {}", e, e);
            return getModelListOffline();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    /**
     * Returns a copy of {@link OllamaClientProvider#TEMP_AVAILABLE_MODEL_LIST}
     * A suck method that shouldn't be used.
     * We use this method ONLY when something went wrong.
     */
    public Set<String> getModelListOffline() {
        return Sets.newHashSet(TEMP_AVAILABLE_MODEL_LIST);
    }

    /**
     * Get the cached model list.
     * To refresh the list, call {@link #refreshCacheModels()}.
     *
     * @see #refreshCacheModels()
     */
    public static Set<String> getCacheModels() {
        return cacheModels;
    }

    /**
     * Refresh the cached model list.
     * To get the list, call {@link #getCacheModels()}.
     *
     * @see #getCacheModels()
     */
    public static void refreshCacheModels() {
        cacheModels.clear();
        cacheModels.addAll(INSTANCE.getModels());
    }

    private static final List<String> TEMP_AVAILABLE_MODEL_LIST = Lists.newArrayList(
            ""
    );
}