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

    public static final String URL = System.getProperty("httpbin.url", "http://localhost:8080");
    public static final String PATH = "/post";
    public static final String CONTENT_TYPE = "application/json";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String post(Map<String, String> data) throws IOException, InterruptedException {
        var writer = new StringWriter();
        try (var generator = Json.createGeneratorFactory(null).createGenerator(writer)) {
            generator.writeStartObject();
            for (var entry : data.entrySet()) {
                generator.write(entry.getKey(), entry.getValue());
            }
            generator.writeEnd();
        }
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(writer.toString()))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
        return response.body();
    }
}
