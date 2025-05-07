Feature: Custom Header Authorization and API Key Support

  Background:
    Given I have initialized the NodeApp server container
#    And I have initialized the Default API Manager container
    And I have initialized the Custom API Manager container with label "customHeader" and deployment toml file path at "/home/abisherk/Internship/InternProject/apim-test-framework/tests-integration/cucumber-tests/src/test/resources/features/customHeaderTest/deployment.toml"
    And I initialize the Store REST API client with username "admin", password "admin" and tenant "carbon.super" for container "custom_header"
    And I initialize the Publisher REST API client with username "admin", password "admin" and tenant "carbon.super" for container "custom_header"
    And I create an application named "CustomHeaderApp" with throttling tier "Unlimited"
    And I create an API with name "CustomAuthHeaderTestAPI1", context "customAuthHeaderTest1" and version "1.0.0"
    And I add "/customers/123" operation without any scopes to the created API with id "<createdApiId>"
    And I deploy a revision of the API with id "<createdApiId>"
    And I publish the API with id "<createdApiId>"
    And I subscribe to API "<createdApiId>" using application "<createdAppId>" with throttling policy "Gold"

  Scenario: Invoke API using system-wide custom auth header and custom API Key header

#  Invoke API using system-wide custom auth header
    When I generate client credentials for application id "<createdAppId>" with key type "PRODUCTION"
    And I request an access token using grant type "client_credentials" without any scope
    And I invoke API of ID "<createdApiId>" with path "/customers/123" and method GET using access token "<generatedAccessToken>" and header "Test-Custom-Header"
    Then the API response status should be 200
    When I invoke API of ID "<createdApiId>" with path "/customers/123" and method GET using access token "<generatedAccessToken>" and header "Authorization"
    Then the API response status should be 401

#  Invoke API with default API Key header
    When I generate an API key for application "<createdAppId>"
    And I invoke API of ID "<createdApiId>" with path "/customers/123" and method GET using API key "<generatedApiKey>" and header "ApiKey"
    Then the API response status should be 200
    When I invoke API of ID "<createdApiId>" with path "/customers/123" and method GET using API key "<generatedApiKey>" and header "Custom-ApiKey-Header"
    Then the API response status should be 401

#  Invoke API with custom API Key header
    When I update the API with id "<createdApiId>" to use API key header "Custom-ApiKey-Header"
    And I deploy a revision of the API with id "<createdApiId>"
    And I generate an API key for application "<createdAppId>"
    And I invoke API of ID "<createdApiId>" with path "/customers/123" and method GET using API key "<generatedApiKey>" and header "Custom-ApiKey-Header"
    Then the API response status should be 200
    When I invoke API of ID "<createdApiId>" with path "/customers/123" and method GET using API key "<generatedApiKey>" and header "ApiKey"
    Then the API response status should be 401

    Then I stop the Custom API Manager container
    Then I clear the context
