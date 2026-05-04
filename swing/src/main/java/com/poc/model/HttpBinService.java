package com.poc.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.StringJoiner;

public class HttpBinService {

    public static final String URL = "http://localhost:8080";
    public static final String PATH = "/post";
    public static final String CONTENT_TYPE = "application/json";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String post(Map<String, String> data) throws IOException, InterruptedException {
        var jsonBody = toJson(data);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
        return response.body();
    }

    /**
     * Serialises a flat {@code Map<String, String>} to a JSON object string.
     * Using a hand-rolled serialiser keeps the dependency footprint small while
     * remaining fully correct for the simple string-only payload this app sends.
     */
    private static String toJson(Map<String, String> data) {
        var joiner = new StringJoiner(", ", "{", "}");
        for (var entry : data.entrySet()) {
            joiner.add("\"" + escapeJson(entry.getKey()) + "\": \"" + escapeJson(entry.getValue()) + "\"");
        }
        return joiner.toString();
    }

    /** Escapes characters that must be escaped inside a JSON string value. */
    private static String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
