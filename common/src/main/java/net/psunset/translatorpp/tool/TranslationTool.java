package net.psunset.translatorpp.tool;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.psunset.translatorpp.TranslatorPP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslationTool {

    private static final TranslationTool INSTANCE = new TranslationTool();
    public static final String ERROR = "!@#$%^&*()_+";

    public static TranslationTool getInstance() {
        return INSTANCE;
    }

    public TranslationTool() {
    }

    public String translate(String q, String sl, String tl) {
        try {
            String url = buildUrl(q, sl, tl);
            String response = getUrlResponse(url);
            String result = parseResult(response);
            return result;
        } catch (Exception e) {
            TranslatorPP.LOGGER.error(e.getMessage());
            e.printStackTrace();
            return ERROR;
        }
    }

    private String buildUrl(String q, String sl, String tl) {
        return "https://translate.googleapis.com/translate_a/single?dt=t&client=gtx&q=" +
                URLEncoder.encode(q, StandardCharsets.UTF_8) +
                "&sl=" + sl +
                "&tl=" + tl;
    }

    private String getUrlResponse(String urlStr) throws Exception {
        URL url = URI.create(urlStr).toURL();
        TranslatorPP.LOGGER.info("Connecting to URL: {}", url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("User-Agent", "TranslatorPP");
            conn.connect();

            StringBuilder response = new StringBuilder();

            if (Thread.currentThread().isInterrupted()) {
                TranslatorPP.LOGGER.warn("{} interrupted", Thread.currentThread().getName());
                throw new RuntimeException("Thread interrupted, translation terminated");
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                TranslatorPP.LOGGER.info("HTTP response: {}", response);
            } else {
                TranslatorPP.LOGGER.error("HTTP error code: {}", conn.getResponseCode());

                try (BufferedReader er = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String errorLine;
                    while ((errorLine = er.readLine()) != null) {
                        response.append(errorLine);
                    }
                }

                TranslatorPP.LOGGER.error("HTTP response: {}", response);
            }
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }

    private String parseResult(String response) {
        JsonElement json = JsonParser.parseReader(new StringReader(response));
        return json.getAsJsonArray().get(0).getAsJsonArray().get(0).getAsJsonArray().get(0).getAsString();
    }
}
