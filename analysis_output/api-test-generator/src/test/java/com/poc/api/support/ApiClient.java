package com.poc.api.support;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

/**
 * API client wrapper for the Allegro PoC REST API.
 * Provides a fluent interface around RestAssured for sending HTTP requests.
 */
public class ApiClient {

    private final String baseUrl;
    private final RequestSpecification requestSpec;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    /**
     * Sends a POST request to the given path with the provided JSON body.
     *
     * @param path        the endpoint path (e.g. "/post")
     * @param requestBody a map representing the JSON request body fields
     * @return the RestAssured {@link Response}
     */
    public Response post(String path, Map<String, String> requestBody) {
        return RestAssured
                .given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }

    /**
     * Sends a POST request to the given path with an empty body.
     *
     * @param path the endpoint path
     * @return the RestAssured {@link Response}
     */
    public Response postEmpty(String path) {
        return RestAssured
                .given()
                .spec(requestSpec)
                .body("{}")
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }
}
