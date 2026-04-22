package com.poc.api.steps;

import com.poc.api.support.ApiClient;
import com.poc.api.support.TestContext;
import com.poc.api.support.TestDataBuilder;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cucumber step definitions for the Allegro PoC POST endpoint.
 * Covers the "java-update" scenario group.
 */
public class PostStepDefinitions {

    private static final Logger log = LoggerFactory.getLogger(PostStepDefinitions.class);

    private final TestContext context;
    private ApiClient apiClient;

    public PostStepDefinitions() {
        this.context = new TestContext();
    }

    @Before
    public void setUp() {
        context.clearRequestBody();
    }

    // -------------------------------------------------------------------------
    // Given steps
    // -------------------------------------------------------------------------

    @Given("the API base URL is {string}")
    public void theApiBaseUrlIs(String baseUrl) {
        context.setBaseUrl(baseUrl);
        apiClient = new ApiClient(baseUrl);
        log.info("Base URL set to: {}", baseUrl);
    }

    @Given("I have the following user data:")
    public void iHaveTheFollowingUserData(List<Map<String, String>> dataTable) {
        for (Map<String, String> row : dataTable) {
            context.addRequestField(row.get("field"), row.get("value"));
        }
        log.info("Request body prepared with {} fields", context.getRequestBody().size());
    }

    @Given("I have an empty request body")
    public void iHaveAnEmptyRequestBody() {
        context.clearRequestBody();
        log.info("Empty request body prepared");
    }

    @Given("I have previously submitted user data for {string}")
    public void iHavePreviouslySubmittedUserDataFor(String userName) {
        Map<String, String> userData = resolveUserData(userName);
        context.setRequestBody(userData);
        log.info("Loaded base user data for: {}", userName);
    }

    // -------------------------------------------------------------------------
    // And steps (setup)
    // -------------------------------------------------------------------------

    @And("I update the following fields:")
    public void iUpdateTheFollowingFields(List<Map<String, String>> dataTable) {
        for (Map<String, String> row : dataTable) {
            context.addRequestField(row.get("field"), row.get("value"));
        }
        log.info("Updated fields in request body");
    }

    // -------------------------------------------------------------------------
    // When steps
    // -------------------------------------------------------------------------

    @When("I send a POST request to {string}")
    public void iSendAPostRequestTo(String path) {
        Response response;
        if (context.getRequestBody().isEmpty()) {
            response = apiClient.postEmpty(path);
        } else {
            response = apiClient.post(path, context.getRequestBody());
        }
        context.setResponse(response);
        log.info("POST {} -> HTTP {}", path, response.getStatusCode());
    }

    // -------------------------------------------------------------------------
    // Then steps (assertions)
    // -------------------------------------------------------------------------

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        int actualStatusCode = context.getResponse().getStatusCode();
        assertThat(actualStatusCode)
                .as("Expected HTTP status %d but got %d. Response body: %s",
                        expectedStatusCode, actualStatusCode, context.getResponse().getBody().asString())
                .isEqualTo(expectedStatusCode);
    }

    @Then("the response body should contain the submitted data")
    public void theResponseBodyShouldContainTheSubmittedData() {
        Response response = context.getResponse();
        String responseBody = response.getBody().asString();

        for (Map.Entry<String, String> entry : context.getRequestBody().entrySet()) {
            assertThat(responseBody)
                    .as("Response body should contain field '%s' with value '%s'. Body: %s",
                            entry.getKey(), entry.getValue(), responseBody)
                    .contains(entry.getValue());
        }
    }

    @And("the response field {string} should equal {string}")
    public void theResponseFieldShouldEqual(String fieldName, String expectedValue) {
        Response response = context.getResponse();

        // Try to extract as a JSON path expression from the response body
        String actualValue = response.jsonPath().getString(fieldName);

        // Fall back: look for the value anywhere in the response body string
        if (actualValue == null) {
            String responseBody = response.getBody().asString();
            assertThat(responseBody)
                    .as("Response body should contain value '%s' for field '%s'. Body: %s",
                            expectedValue, fieldName, responseBody)
                    .contains(expectedValue);
        } else {
            assertThat(actualValue)
                    .as("Response field '%s' should equal '%s'", fieldName, expectedValue)
                    .isEqualTo(expectedValue);
        }
    }

    @And("the response content type should be {string}")
    public void theResponseContentTypeShouldBe(String expectedContentType) {
        String actualContentType = context.getResponse().getContentType();
        assertThat(actualContentType)
                .as("Expected content type '%s' but got '%s'", expectedContentType, actualContentType)
                .contains(expectedContentType);
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    private Map<String, String> resolveUserData(String userName) {
        return switch (userName) {
            case "Hans Müller" -> TestDataBuilder.hansMueller();
            case "Maria Schmidt" -> TestDataBuilder.mariaSchmidt();
            case "Alex Weber" -> TestDataBuilder.alexWeber();
            default -> throw new IllegalArgumentException("Unknown user persona: " + userName);
        };
    }
}
