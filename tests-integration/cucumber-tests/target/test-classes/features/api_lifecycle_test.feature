Feature: Complete API Lifecycle Management

  Background:
    Given I have initialized the NodeApp server container
    And I have initialized the Default API Manager container
    And I initialize the Publisher REST API client with username "admin", password "admin" and tenant "carbon.super" for container "default"
    And I initialize the Store REST API client with username "admin", password "admin" and tenant "carbon.super" for container "default"

  Scenario: End-to-End API Lifecycle from Creation to Invocation
    When I create an API with name "JaxrsAPI", context "/jaxrs" and version "1.0.0"
    And I add "/customers/{id}" operation without any scopes to the created API with id "<createdApiId>"
    And I deploy a revision of the API with id "<createdApiId>"
    Then I should be able to retrieve the API with id "<createdApiId>"

    When I publish the API with id "<createdApiId>"
    Then The lifecycle status of API "<createdApiId>" should be "Published"

    When I create an application named "CustomerApp" with throttling tier "Unlimited"
    Then I should be able to retrieve the application with id "<createdAppId>"

    When I subscribe to API "<createdApiId>" using application "<createdAppId>" with throttling policy "Bronze"
    Then I should be able to retrieve the subscription for Api "<createdApiId>" by Application "<createdAppId>"


    When I generate client credentials for application id "<createdAppId>" with key type "PRODUCTION"
    And I request an access token using grant type "client_credentials" without any scope
    And I invoke API of ID "<createdApiId>" with path "/customers/123/" and method GET using access token "<generatedAccessToken>"
    Then the API response status should be 200

