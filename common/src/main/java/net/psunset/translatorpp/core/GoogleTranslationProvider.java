package net.psunset.translatorpp.core;

import com.google.gson.JsonArray;
import net.psunset.translatorpp.api.IServiceProvider;
import net.psunset.translatorpp.exception.ServiceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleTranslationProvider implements IServiceProvider {

    static final GoogleTranslationProvider INSTANCE = new GoogleTranslationProvider();

    public static GoogleTranslationProvider getInstance() {
        return INSTANCE;
    }

    private GoogleTranslationProvider() {
    }

    @Override
    public String translate(String q, String sl, String tl) throws IOException {
        String url = buildUrl(q, sl, tl);
        String response = getUrlResponse(url);
        return parseResult(response);
    }

    private String buildUrl(String q, String sl, String tl) {
        return "https://translate.googleapis.com/translate_a/single?dt=t&client=gtx&q=" +
                URLEncoder.encode(q, StandardCharsets.UTF_8) +
//                "&sl=" + sl +
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

            StringBuilder responseBuilder = new StringBuilder();
            int statusCode = conn.getResponseCode();
            boolean isError = statusCode < 200 || statusCode >= 300;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    isError ? conn.getErrorStream() : conn.getInputStream(),
                    StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine);
                }
            }
            String response = responseBuilder.toString();

            if (isError) {
                Pattern pattern = Pattern.compile("<p>(.*?)<ins>", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(response);
                String content = "";
                int count = 0;
                while (matcher.find()) {
                    count++;
                    if (count == 2) {
                        content = matcher.group(1).trim();
                        break;
                    }
                }
                if (content.isEmpty()) {
                    content = "Unknown";
                }
                throw new ServiceException.Google(content, statusCode);
            }

            return response;
        } finally {
            conn.disconnect();
        }
    }

    private String parseResult(String response) {
        JsonArray json = TranslationKit.GSON.fromJson(response, JsonArray.class);
        // Idk what the contents in the json mean. Just get what I need here.
        JsonArray results = json.get(0).getAsJsonArray();
        StringBuilder sb = new StringBuilder();
        for (var result : results) {
            sb.append(result.getAsJsonArray().get(0).getAsString().trim());
        }
        return sb.toString();
    }
}
