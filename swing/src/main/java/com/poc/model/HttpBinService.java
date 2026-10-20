package com.poc.model;

import jakarta.json.Json;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HttpBinService {

    public static final String URL = "http://localhost:8080";
    public static final String PATH = "/post";
    public static final String CONTENT_TYPE = "application/json";

    // Reuse a single HttpClient instance across calls (thread-safe and recommended)
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public String post(Map<String, String> data) throws IOException, InterruptedException {
        // Build JSON body using jakarta.json
        var stringWriter = new StringWriter();
        try (var generator = Json.createGenerator(stringWriter)) {
            generator.writeStartObject();
            for (var entry : data.entrySet()) {
                generator.write(entry.getKey(), entry.getValue());
            }
            generator.writeEnd();
        }
        String requestBody = stringWriter.toString();

        // Use modern java.net.http.HttpClient (Java 11+)
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
        return response.body();
    }
}
