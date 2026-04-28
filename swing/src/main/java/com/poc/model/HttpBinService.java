package com.poc.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import jakarta.json.Json;

public class HttpBinService {

    public static final String URL = "http://localhost:8080";
    public static final String PATH = "/post";
    public static final String CONTENT_TYPE = "application/json";

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public String post(Map<String, String> data) throws IOException, InterruptedException {
        var jsonObjectBuilder = Json.createObjectBuilder();
        for (var entry : data.entrySet()) {
            jsonObjectBuilder.add(entry.getKey(), entry.getValue());
        }
        String requestBody = jsonObjectBuilder.build().toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
        return response.body();
    }
}
