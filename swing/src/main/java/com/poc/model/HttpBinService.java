package com.poc.model;

import jakarta.json.Json;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * HTTP client that posts form data as JSON to the configured endpoint.
 *
 * <p>Uses the modern {@link HttpClient} API (Java 11+) in place of the
 * deprecated {@link java.net.HttpURLConnection}.</p>
 */
public class HttpBinService {

    public static final String BASE_URL = "http://localhost:8080";
    public static final String PATH = "/post";
    public static final String CONTENT_TYPE = "application/json";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Serialises {@code data} to a JSON object string using Jakarta JSON Processing.
     */
    private String buildJsonBody(Map<String, String> data) {
        var writer = new StringWriter();
        try (var generator = Json.createGenerator(writer)) {
            generator.writeStartObject();
            data.forEach(generator::write);
            generator.writeEnd();
        }
        return writer.toString();
    }

    /**
     * POSTs {@code data} as a JSON body and returns the raw response body.
     *
     * @param data key-value pairs to serialise
     * @return response body string (never {@code null})
     * @throws IOException          on network or I/O error
     * @throws InterruptedException if the request is interrupted
     */
    public String post(Map<String, String> data) throws IOException, InterruptedException {
        var jsonBody = buildJsonBody(data);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.printf("Response code: %d%n", response.statusCode());
        System.out.printf("Response body: %s%n", response.body());
        return response.body();
    }
}
