package com.poc.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HttpBinService {

    public static final String URL = "http://localhost:8080";
    public static final String PATH = "/post";
    public static final String CONTENT_TYPE = "application/json";

    // HttpClient is designed to be long-lived and shared; one instance per service is the intended usage pattern.
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String post(Map<String, String> data) throws IOException, InterruptedException {
        var json = buildJson(data);
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
        return response.body();
    }

    private String buildJson(Map<String, String> data) {
        var sb = new StringBuilder("{");
        var first = true;
        for (var entry : data.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escape(entry.getKey())).append("\":");
            sb.append("\"").append(escape(entry.getValue())).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /** Escapes a string value per RFC 8259 (JSON string escaping). */
    private String escape(String value) {
        var sb = new StringBuilder(value.length() + 4);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> {
                    if (c < 0x20) {
                        // Use manual hex padding to avoid String.format overhead in the loop
                        String hex = Integer.toHexString(c);
                        sb.append("\\u");
                        for (int j = hex.length(); j < 4; j++) sb.append('0');
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}
