package com.poc.api.support;

import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the shared test state across Cucumber step definitions.
 * One instance is created per scenario.
 */
public class TestContext {

    private String baseUrl;
    private Map<String, String> requestBody = new HashMap<>();
    private Response response;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    public void addRequestField(String field, String value) {
        this.requestBody.put(field, value);
    }

    public void clearRequestBody() {
        this.requestBody = new HashMap<>();
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
