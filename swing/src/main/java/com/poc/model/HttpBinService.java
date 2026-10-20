package com.poc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class HttpBinService {

    public static final String URL = "http://localhost:8080";
    public static final String PATH = "/post";
    public static final String CONTENT_TYPE = "application/json";

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .build();

    public String post(Map<String, String> data) throws IOException, InterruptedException {
        var body = mapper.writeValueAsString(data);
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL + PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
        return response.body();
    }
}
