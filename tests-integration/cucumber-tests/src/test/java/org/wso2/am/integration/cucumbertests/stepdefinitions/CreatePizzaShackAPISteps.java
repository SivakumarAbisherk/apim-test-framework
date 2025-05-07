package org.wso2.am.integration.cucumbertests.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import io.restassured.http.ContentType;
import org.wso2.am.integration.cucumbertests.di.TestEnvHolder;
import org.wso2.am.testcontainers.DefaultAPIMContainer;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CreatePizzaShackAPISteps {

private Map<String, String> testEnv;
private Response apiResponse;

@Given("the access token is available")
public void givenAccessTokenIsAvailable() {
    DefaultAPIMContainer apimContainer = DefaultAPIMContainer.getInstance();

    // Get environment variables from TestEnvHolder
    testEnv = TestEnvHolder.getEnvMap();

    // Configure RestAssured for HTTPS
    RestAssured.config = RestAssured.config()
            .sslConfig(SSLConfig.sslConfig()
                    .relaxedHTTPSValidation()
                    .allowAllHostnames());

    RestAssured.baseURI = "https://localhost";
    RestAssured.port = apimContainer.getHttpsPort();

    String accessToken = TestEnvHolder.getAccessToken();
    Assert.assertNotNull(accessToken, "Access token should be available in test environment");
    testEnv = TestEnvHolder.getEnvMap();
    testEnv.put("access_token", accessToken);
}

@When("I create the Pizza Shack API with the access token")
public void whenICreatePizzaShackAPIWithAccessToken() {
    String accessToken = testEnv.get("access_token");
    apiResponse = createPizzaShackAPI(accessToken);
}

@Then("the API creation should be successful")
public void thenAPICreationShouldBeSuccessful() {
    Assert.assertEquals(apiResponse.getStatusCode(), 201, "API creation should be successful");
}

@Then("the API ID should be saved for subsequent tests")
public void thenAPICIDShouldBeSavedForSubsequentTests() {
    String apiId = apiResponse.jsonPath().getString("id");
    Assert.assertNotNull(apiId, "API ID should not be null");

// Save API ID in test environment for use in future tests
testEnv.put("api_id", apiId);
}

private Response createPizzaShackAPI(String accessToken) {
String pizzashackEndpoint = testEnv.getOrDefault("pizzashack_endpoint", "https://localhost:9443/am/sample/pizzashack/v1/api/");
String publisherBasePath = testEnv.getOrDefault("publisher_base_path", "/api/am/publisher/v4");

String apiCreatePayload = String.format("{\n" +
"              \"name\": \"PizzaShackAPI\",\n" +
"              \"description\": \"This is a simple API for Pizza Shack online pizza delivery store.\",\n" +
"              \"context\": \"/pizzashack\",\n" +
"              \"version\": \"1.0.0\",\n" +
"              \"transport\": [\n" +
"                \"http\",\n" +
"                \"https\"\n" +
"              ],\n" +
"              \"tags\": [\n" +
"                \"pizza\"\n" +
"              ],\n" +
"              \"policies\": [\n" +
"                \"Unlimited\"\n" +
"              ],\n" +
"              \"securityScheme\": [\n" +
"                \"oauth2\"\n" +
"              ],\n" +
"              \"visibility\": \"PUBLIC\",\n" +
"              \"businessInformation\": {\n" +
"                \"businessOwner\": \"Jane Roe\",\n" +
"                \"businessOwnerEmail\": \"marketing@pizzashack.com\",\n" +
"                \"technicalOwner\": \"John Doe\",\n" +
"                \"technicalOwnerEmail\": \"architecture@pizzashack.com\"\n" +
"              },\n" +
"              \"endpointConfig\": {\n" +
"                \"endpoint_type\": \"http\",\n" +
"                \"sandbox_endpoints\": {\n" +
"                  \"url\": \"%s\"\n" +
"                },\n" +
"                \"production_endpoints\": {\n" +
"                  \"url\": \"%s\"\n" +
"                }\n" +
"              },\n" +
"              \"operations\": [\n" +
"                {\n" +
"                  \"target\": \"/order/{orderId}\",\n" +
"                  \"verb\": \"GET\",\n" +
"                  \"throttlingPolicy\": \"Unlimited\",\n" +
"                  \"authType\": \"Application & Application User\"\n" +
"                },\n" +
"                {\n" +
"                  \"target\": \"/order/{orderId}\",\n" +
"                  \"verb\": \"DELETE\",\n" +
"                  \"throttlingPolicy\": \"Unlimited\",\n" +
"                  \"authType\": \"Application & Application User\"\n" +
"                },\n" +
"                {\n" +
"                  \"target\": \"/order/{orderId}\",\n" +
"                  \"verb\": \"PUT\",\n" +
"                  \"throttlingPolicy\": \"Unlimited\",\n" +
"                  \"authType\": \"Application & Application User\"\n" +
"                },\n" +
"                {\n" +
"                  \"target\": \"/menu\",\n" +
"                  \"verb\": \"GET\",\n" +
"                  \"throttlingPolicy\": \"Unlimited\",\n" +
"                  \"authType\": \"Application & Application User\"\n" +
"                },\n" +
"                {\n" +
"                  \"target\": \"/order\",\n" +
"                  \"verb\": \"POST\",\n" +
"                  \"throttlingPolicy\": \"Unlimited\",\n" +
"                  \"authType\": \"Application & Application User\"\n" +
"                }\n" +
"              ]\n" +
"            }", pizzashackEndpoint, pizzashackEndpoint);

return given()
    .relaxedHTTPSValidation()
    .header("Authorization", "Bearer " + accessToken)
    .header("Host", "localhost")
    .contentType(ContentType.JSON)
    .body(apiCreatePayload)
    .queryParam("openAPIVersion", "v3")
    .post(publisherBasePath + "/apis");
    }
}
