package com.poc.model;

import javax.json.Json;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Simple HTTP client for posting form-like data as JSON.
 * Uses the Java 11+ {@link HttpClient} API instead of the older
 * {@link java.net.HttpURLConnection}.
 */
public class HttpBinService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String PATH = "/post";
    private static final String CONTENT_TYPE = "application/json";

    /** Single shared client – HttpClient is thread-safe and reusable. */
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public String post(Map<String, String> data) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(buildJson(data)))
                .build();

        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.printf("Response code: %d%n", response.statusCode());
        System.out.printf("Response body: %s%n", response.body());
        return response.body();
    }

    /** Serialises a {@code Map<String,String>} to a flat JSON object string. */
    private static String buildJson(Map<String, String> data) {
        var writer = new StringWriter();
        try (var generator = Json.createGenerator(writer)) {
            generator.writeStartObject();
            data.forEach(generator::write);
            generator.writeEnd();
        }
        return writer.toString();
    }
}
