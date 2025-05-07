package org.wso2.am.integration.tests.basictestcontainertest;

import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.wso2.am.testcontainers.DefaultAPIMContainer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;


public class APIAndApplicationBasicTestCase {

    private Map<String, String> testEnv;

    @BeforeClass
    void setUp() {
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
    }

    @Test
    void testCreateAPI() {
        String accessToken = TestEnvHolder.getAccessToken();
        Assert.assertNotNull(accessToken, "Access token should be available in test environment");

        Response apiResponse = createPizzaShackAPI(accessToken);
        Assert.assertEquals(apiResponse.getStatusCode(), 201, "API creation should be successful");

        String apiId = apiResponse.jsonPath().getString("id");
        Assert.assertNotNull(apiId, "API ID should not be null");

        // Store API ID in test environment for subsequent tests
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

