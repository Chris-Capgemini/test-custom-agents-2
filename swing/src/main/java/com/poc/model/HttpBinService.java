package com.poc.model;

import javax.json.Json;
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

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public String post(Map<String, String> data) throws IOException, InterruptedException {
        String jsonBody = buildJsonBody(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        return response.body();
    }

    private String buildJsonBody(Map<String, String> data) {
        var objectBuilder = Json.createObjectBuilder();
        for (var entry : data.entrySet()) {
            objectBuilder.add(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
        }
        var sw = new StringWriter();
        try (var writer = Json.createWriter(sw)) {
            writer.writeObject(objectBuilder.build());
        }
        return sw.toString();
    }
}
