package org.wso2.am.integration.cucumbertests.di;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestEnvHolder {
    private static final Map<String, String> testEnvMap = new ConcurrentHashMap<>();
    private static final String SUPER_ADMIN_USERNAME = "admin";
    private static final String SUPER_ADMIN_PASSWORD = "admin";

    public static Map<String, String> getEnvMap() {
        return testEnvMap;
    }

    public static String getAccessToken() {
        // Ensure client_id and client_secret are available
        String clientId = testEnvMap.get("dcr_app_client_id");
        String clientSecret = testEnvMap.get("dcr_app_client_secret");

        if (clientId == null || clientSecret == null) {
            Response dcrResponse = registerDCRApplication();
            if (dcrResponse.getStatusCode() != 200) {
                throw new RuntimeException("DCR application registration failed: " + dcrResponse.getBody().asString());
            }

            clientId = dcrResponse.jsonPath().getString("clientId");
            clientSecret = dcrResponse.jsonPath().getString("clientSecret");

            testEnvMap.put("dcr_app_client_id", clientId);
            testEnvMap.put("dcr_app_client_secret", clientSecret);
        }

        // Retrieve access token
        Response tokenResponse = getAccessToken(clientId, clientSecret);
        if (tokenResponse.getStatusCode() != 200) {
            throw new RuntimeException("Access token retrieval failed: " + tokenResponse.getBody().asString());
        }

        String accessToken = tokenResponse.jsonPath().getString("access_token");
        testEnvMap.put("dcr_app_access_token", accessToken);

        return accessToken;
    }

    private static Response registerDCRApplication() {
        String dcrPath = "/client-registration/v0.17/register";
        String basicAuth = Base64.getEncoder().encodeToString(
                (SUPER_ADMIN_USERNAME + ":" + SUPER_ADMIN_PASSWORD).getBytes()
        );

        return RestAssured.given()
                .header("Authorization", "Basic " + basicAuth)
                .header("Host", "localhost")
                .contentType("application/json")
                .body("{\n" +
                        "\t\"callbackUrl\": \"www.google.lk\",\n" +
                        "\t\"clientName\": \"rest_api_publisher\",\n" +
                        "\t\"owner\": \"admin\",\n" +
                        "\t\"grantType\": \"client_credentials password refresh_token\",\n" +
                        "\t\"saasApp\": true\n" +
                        "}")
                .post(dcrPath);
    }

    private static Response getAccessToken(String clientId, String clientSecret) {
        String tokenPath = "/oauth2/token";
        String basicAuth = Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes()
        );

        return RestAssured.given()
                .header("Authorization", "Basic " + basicAuth)
                .header("Host", "localhost")
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", SUPER_ADMIN_USERNAME)
                .formParam("password", SUPER_ADMIN_PASSWORD)
                .formParam("scope", "apim:api_view apim:api_publish apim:api_create " +
                        "apim:subscribe apim:subscription_view apim:document_create " +
                        "apim:comment_write apim:admin apim:tier_manage apim:scope_manage " +
                        "apim:admin_operations apim:shared_scope_manage " +
                        "apim:mediation_policy_create apim:api_import_export " +
                        "apim:mediation_policy_view apim:app_manage apim:sub_manage " +
                        "apim:admin apim:tier_view apim:subscription_block " +
                        "apim:mediation_policy_create apim:mediation_policy_manage " +
                        "apim:mediation_policy_view apim:policies_import_export " +
                        "apim:common_operation_policy_manage apim:common_operation_policy_view")
                .post(tokenPath);
    }
}
