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

    public String post(Map<String, String> data) throws IOException, InterruptedException {
        var jsonGeneratorFactory = Json.createGeneratorFactory(null);
        var writer = new StringWriter();
        var generator = jsonGeneratorFactory.createGenerator(writer);
        generator.writeStartObject();
        for (var entry : data.entrySet()) {
            generator.write(entry.getKey(), entry.getValue());
        }
        generator.writeEnd();
        generator.close();
        String jsonBody = writer.toString();

        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        return response.body();
    }
}
