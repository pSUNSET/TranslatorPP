package net.psunset.translatorpp.translation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.Util;
import net.psunset.translatorpp.TranslatorPP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OpenAIClientTool implements TranslationTool {

    private static final OpenAIClientTool INSTANCE = new OpenAIClientTool();
    public static final String PROMPT = """
            I'm playing modded Minecraft.
            But there are some words I didn't understand.
            So, please translate the following words, "%s", from '%s' language to '%s' language.
            Also, because those words are from modded Minecraft, you can properly adjust your answer.
            Finally, the response you return *MUST* only contain the translated result. No other description.""";

    /**
     * Grabbing the model list from online wastes too much time.
     * So set a cache here to let us get it more swiftly.
     */
    private static final Set<String> cacheModels = Sets.newHashSet();

    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 30000;    // 30 seconds

    public static OpenAIClientTool getInstance() {
        return INSTANCE;
    }

    private String apiKey;
    private Api api;
    private String model;
    private final Gson gson;

    public OpenAIClientTool() {
        this.gson = new GsonBuilder().create();
    }

    protected void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    protected void setApi(Api api) {
        this.api = api;
        if (api != null && (this.model == null || this.model.isEmpty())) {
            this.model = api.defaultModel;
        }
    }

    public Api getApi() {
        return this.api;
    }

    protected void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return this.model;
    }

    @Override
    public String translate(String q, String sl, String tl) throws Exception {
        if (!isPresent()) {
            throw new IllegalStateException("OpenAIClientTool is not configured. API key, API provider, and model must be set.");
        }

        String formattedPrompt = PROMPT.formatted(q, sl, tl);

        // Use a Map to build the request, then serialize with Gson
        Map<String, Object> requestPayload = Maps.newLinkedHashMap(); // Use LinkedHashMap to preserve insertion order if it matters
        requestPayload.put("model", this.model);
        List<Map<String, String>> messages = Lists.newArrayList();
        Map<String, String> userMessage = Maps.newLinkedHashMap();
        userMessage.put("role", "user");
        userMessage.put("content", formattedPrompt);
        messages.add(userMessage);
        requestPayload.put("messages", messages);
        requestPayload.put("temperature", 0.7);
        // Add other parameters if needed, e.g., max_tokens

        String jsonRequestBody = gson.toJson(requestPayload);

        HttpURLConnection con = null;
        try {
            URL url = URI.create(this.api.baseUrl + "chat/completions").toURL();
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + this.apiKey);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestProperty("Accept", "application/json");
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
                // Try to parse JSON error response from API if possible
                try {
                    JsonObject errorJson = gson.fromJson(rawResponse, JsonObject.class);
                    if (errorJson != null && errorJson.has("error") && errorJson.get("error").isJsonObject()) {
                        JsonObject errorDetails = errorJson.getAsJsonObject("error");
                        String errorMessage = errorDetails.has("message") ? errorDetails.get("message").getAsString() : rawResponse;
                        throw new ServiceException("API call failed: " + errorMessage, statusCode);
                    }
                } catch (JsonSyntaxException e) {
                    // Not a JSON error response, or malformed. Fallback to raw response.
                }
                throw new ServiceException("API call failed: " + rawResponse, statusCode);
            }

            JsonObject responseJson = gson.fromJson(rawResponse, JsonObject.class);
            JsonArray choices = responseJson.getAsJsonArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new IOException("Invalid response: 'choices' array not found or empty. Response: " + rawResponse);
            }
            JsonObject firstChoice = choices.get(0).getAsJsonObject();
            JsonObject message = firstChoice.getAsJsonObject("message");
            if (message == null || !message.has("content")) {
                throw new IOException("Invalid response: 'message.content' not found. Response: " + rawResponse);
            }

            return message.get("content").getAsString().trim();

        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public boolean isPresent() {
        return this.apiKey != null && !this.apiKey.isEmpty() &&
                this.api != null && this.model != null && !this.model.isEmpty();
    }

    /**
     * Returns the model list from online.
     */
    public Set<String> getModels() {
        if (this.apiKey == null || this.apiKey.isEmpty() || this.api == null) {
            TranslatorPP.LOGGER.error("Error while getting online model list: API key or API provider not set.");
            return getModelListOffline();
        }

        HttpURLConnection con = null;
        try {
            URL url = new URL(this.api.baseUrl + "models");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + this.apiKey);
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
            JsonObject responseJson = gson.fromJson(rawResponse, JsonObject.class);
            JsonArray dataArray = responseJson.getAsJsonArray("data");

            if (dataArray != null) {
                for (JsonElement modelElement : dataArray) {
                    if (modelElement.isJsonObject()) {
                        JsonObject modelObject = modelElement.getAsJsonObject();
                        if (modelObject.has("id") && modelObject.get("id").isJsonPrimitive()) {
                            String id = modelObject.get("id").getAsString();
                            modelIds.add(id.replace("models/", ""));
                        }
                    }
                }
            } else {
                TranslatorPP.LOGGER.warn("No 'data' array found in models response or it's not an array. Response: {}", snippet);
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
     * Returns a copy of {@link OpenAIClientTool#TEMP_AVAILABLE_MODEL_LIST}
     * A suck method that shouldn't be used.
     * We use this method ONLY when something went wrong.
     */
    public Set<String> getModelListOffline() {
        return Sets.newHashSet(TEMP_AVAILABLE_MODEL_LIST);
    }

    public static Set<String> getCacheModels() {
        return cacheModels;
    }

    public static void refreshCacheModels() {
        cacheModels.clear();
        cacheModels.addAll(INSTANCE.getModels());
    }

    public enum Api {
        OpenAI("https://api.openai.com/v1/", "gpt-4o-mini"),
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

    public static class ServiceException extends RuntimeException {
        public final int statusCode;
        public ServiceException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }
    }

    /**
     * A temp-available model list.
     * So this may not be correct in the future.
     * If some models are deprecated or new models get updated, this list won't update in time.
     * This list is created on May 07, 2025.
     */
    private static final List<String> TEMP_AVAILABLE_MODEL_LIST = Lists.newArrayList(
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
