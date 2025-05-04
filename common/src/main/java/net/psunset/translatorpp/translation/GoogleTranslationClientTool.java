package net.psunset.translatorpp.translation;

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

public class GoogleTranslationClientTool extends AbstractTranslationClientTool {

    private static final GoogleTranslationClientTool INSTANCE = new GoogleTranslationClientTool();

    public static GoogleTranslationClientTool getInstance() {
        return INSTANCE;
    }

    public GoogleTranslationClientTool() {
    }

    @Override
    public String _translate(String q, String sl, String tl) throws IOException {
        String url = buildUrl(q, sl, tl);
        String response = getUrlResponse(url);
        return parseResult(response);
    }

    private String buildUrl(String q, String sl, String tl) {
        return "https://translate.googleapis.com/translate_a/single?dt=t&client=gtx&q=" +
                URLEncoder.encode(q, StandardCharsets.UTF_8) +
                "&sl=" + sl +
                "&tl=" + tl;
    }

    private String getUrlResponse(String urlStr) throws IOException {
        URL url = URI.create(urlStr).toURL();
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
