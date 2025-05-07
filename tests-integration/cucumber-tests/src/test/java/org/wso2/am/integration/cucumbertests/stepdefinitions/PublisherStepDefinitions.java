package org.wso2.am.integration.cucumbertests.stepdefinitions;

import com.google.gson.Gson;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIScopeDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.ScopeDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIOperationsDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.DocumentDTO;
import org.wso2.am.integration.cucumbertests.di.TestContext;
import org.wso2.am.integration.test.impl.RestAPIPublisherImpl;
import org.wso2.am.integration.test.utils.bean.APIRequest;
import org.wso2.am.integration.test.utils.bean.APIRevisionDeployUndeployRequest;
import org.wso2.am.integration.test.utils.bean.APIRevisionRequest;
import org.wso2.am.testcontainers.DefaultAPIMContainer;
import org.wso2.am.testcontainers.TomcatServer;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PublisherStepDefinitions {

    private String API_ENDPOINT_POSTFIX_URL = "jaxrs_basic/services/customers/customerservice/";

//    private String API_ENDPOINT_POSTFIX_URL = "am/sample/pizzashack/v1/api/";

    RestAPIPublisherImpl publisher;
    String createdApiId;
    String createdDocId;
    String baseUrl;
    String serviceBaseUrl;

    private final TestContext context;

    public PublisherStepDefinitions(TestContext testcontext) {
        this.context = testcontext;
        baseUrl = context.get("baseUrl").toString();
        serviceBaseUrl= context.get("serviceBaseUrl").toString();
//        baseUrl="https://localhost:9443/";

        // pizzashack
//        serviceBaseUrl="https://localhost:9443/";
    }

    @When("I initialize the Publisher REST API client with username {string}, password {string} and tenant {string} for container {string}")
    public void i_initialize_publisher_client(String username, String password, String tenantDomain, String containerLabel) {
        publisher = new RestAPIPublisherImpl(username, password, tenantDomain, baseUrl, containerLabel);
    }

    @When("I create an API with name {string}, context {string} and version {string}")
    public void i_create_customer_service_api(String name, String APContext, String version) throws Exception {
        String apiProductionEndPointUrl = serviceBaseUrl + API_ENDPOINT_POSTFIX_URL;
        context.set("apiProductionEndPointUrl", apiProductionEndPointUrl);
        String apiDescription = "JAX-RS CustomerService API created for integration testing.";
        String apiTag = "customer,order,service";

        APIRequest apiCreationRequestBean = new APIRequest(name, APContext, new URL(apiProductionEndPointUrl));

        apiCreationRequestBean.setVersion(version);
        apiCreationRequestBean.setDescription(apiDescription);
        apiCreationRequestBean.setTags(apiTag);
        apiCreationRequestBean.setTiersCollection("Gold,Bronze");
        apiCreationRequestBean.setTier("Gold");
        apiCreationRequestBean.setDefault_version_checked("true");
        List<String> securitySchemes = new ArrayList<>();
        securitySchemes.add("oauth2");
        securitySchemes.add("api_key");
        apiCreationRequestBean.setSecurityScheme(securitySchemes);

//        List<APIOperationsDTO> operationsDTOS = new ArrayList<>();
//
//        // GET /customers/{id}
//        APIOperationsDTO getCustomerByIdOperation = new APIOperationsDTO();
//        getCustomerByIdOperation.setVerb("GET");
//        getCustomerByIdOperation.setTarget("/customers/{id}");
//        getCustomerByIdOperation.setThrottlingPolicy("Unlimited");
//        getCustomerByIdOperation.setAuthType("Application");
//        operationsDTOS.add(getCustomerByIdOperation);
//
//        apiCreationRequestBean.setOperationsDTOS(operationsDTOS);

        apiCreationRequestBean.setBusinessOwner("customerOwner");
        apiCreationRequestBean.setBusinessOwnerEmail("owner@company.com");
        apiCreationRequestBean.setTechnicalOwner("customerTech");
        apiCreationRequestBean.setTechnicalOwnerEmail("tech@company.com");

        HttpResponse apiCreationResponse = publisher.addAPI(apiCreationRequestBean);
        String apiId = apiCreationResponse.getData();
        this.context.set("createdApiId", apiId);
    }

    @When("I add {string} operation without any scopes to the created API with id {string}")
    public void i_add_operation_without_scopes_to_api(String operation,String apiId) throws Exception {
        String actualApiId = apiId;
        if (apiId.startsWith("<") && apiId.endsWith(">")) {
            actualApiId = (String) context.get(apiId.substring(1, apiId.length() - 1));
        }
        HttpResponse createdApiResponse = publisher.getAPI(actualApiId);
        APIDTO apiDto = new Gson().fromJson(createdApiResponse.getData(), APIDTO.class);

        List<APIOperationsDTO> operations = new ArrayList<>();
        APIOperationsDTO op = new APIOperationsDTO();
        op.setVerb("GET");
//        op.setTarget("/customers/{id}");
        op.setTarget(operation);
        op.setAuthType("Application & Application User");
        op.setThrottlingPolicy("Unlimited");

        operations.add(op);
        apiDto.setOperations(operations);

        publisher.updateAPI(apiDto, actualApiId);
    }

    @When("I add {string} operation with scopes {string} to the created API with id {string}")
    public void i_add_operation_with_scopes_to_api(String operation, String scopesCsv, String apiId) throws Exception {
        String actualApiId = apiId;
        if (apiId.startsWith("<") && apiId.endsWith(">")) {
            actualApiId = (String) context.get(apiId.substring(1, apiId.length() - 1));
        }
        HttpResponse createdApiResponse = publisher.getAPI(actualApiId);
        APIDTO apiDto = new Gson().fromJson(createdApiResponse.getData(), APIDTO.class);

        List<String> scopesList = new ArrayList<>();
        for (String scopeName : scopesCsv.split(",")) {
            scopesList.add(scopeName.trim());
        }

        List<APIOperationsDTO> operations = new ArrayList<>();
        APIOperationsDTO op = new APIOperationsDTO();
        op.setVerb("GET");
//        op.setTarget("/customers/{id}");
        op.setTarget(operation);
        op.setAuthType("Application & Application User");
        op.setThrottlingPolicy("Unlimited");
        op.setScopes(scopesList);

        operations.add(op);
        apiDto.setOperations(operations);

        publisher.updateAPI(apiDto, actualApiId);
    }

    @When("I update the API with id {string} to use API key header {string}")
    public void update_api_with_custom_api_key_header(String apiId, String headerName) throws Exception {
        String actualApiId = resolveFromContext(apiId);
        APIDTO apiDto = publisher.getAPIByID(actualApiId);
        apiDto.setApiKeyHeader(headerName);
        publisher.updateAPI(apiDto);
        APIDTO apiDtoResponse = publisher.getAPIByID(actualApiId);
        Assert.assertEquals(apiDtoResponse.getApiKeyHeader(),headerName);

    }



    @When("I create scope {string} with roles {string}")
    public void i_create_scope_with_roles(String scopeName, String rolesCsv) throws Exception {

        List<String> rolesList = new ArrayList<>();
        for (String role : rolesCsv.split(",")) {
            rolesList.add(role.trim());
        }

        ScopeDTO scope = new ScopeDTO();
        scope.setName(scopeName.trim());
        scope.setBindings(rolesList);

        context.set(scopeName.trim(), scope);
    }


    @When("I add scopes {string} to the created API with id {string}")
    public void i_add_scopes_to_created_api(String scopesCsv, String apiId) throws Exception {
        String actualApiId = apiId;
        if (apiId.startsWith("<") && apiId.endsWith(">")) {
            actualApiId = (String) context.get(apiId.substring(1, apiId.length() - 1));
        }
        HttpResponse createdApiResponse = publisher.getAPI(actualApiId);
        APIDTO apiDto = new Gson().fromJson(createdApiResponse.getData(), APIDTO.class);

        List<APIScopeDTO> scopesList = new ArrayList<>();
        for (String scopeName : scopesCsv.split(",")) {
            APIScopeDTO apiScopeDTO = new APIScopeDTO();
            apiScopeDTO.setScope((ScopeDTO) context.get(scopeName));
            scopesList.add(apiScopeDTO);
        }
        apiDto.setScopes(scopesList);
        publisher.updateAPI(apiDto, actualApiId);
    }

    @When("I deploy a revision of the API with id {string}")
    public void i_deploy_revision(String apiId) throws Exception {
        String actualApiId = apiId;
        if (apiId.startsWith("<") && apiId.endsWith(">")) {
            actualApiId = (String) context.get(apiId.substring(1, apiId.length() - 1));
        }
        String revisionUUID = publisher.createAPIRevisionAndDeployUsingRest(actualApiId);
        Thread.sleep(10000);
        context.set("revisionID",revisionUUID);
    }

    @When("I update the API with id {string} to have name {string}")
    public void i_update_the_api(String apiId, String name) throws Exception {
        APIRequest request = new APIRequest(name, "/updated-context", new URL("http://example.com"));
        request.setVersion("1.0.0");
        publisher.updateAPI(request, apiId);
    }

    @When("I delete the API with id {string}")
    public void i_delete_the_api(String apiId) throws Exception {
        publisher.deleteAPI(apiId);
    }

    @When("I publish the API with id {string}")
    public void i_publish_the_api(String apiId) throws Exception {
        String actualApiId = apiId;
        if (apiId.startsWith("<") && apiId.endsWith(">")) {
            actualApiId = (String) context.get(apiId.substring(1, apiId.length() - 1));
        }
        publisher.changeAPILifeCycleStatus(actualApiId, "Publish", null);
    }

    @When("I block the API with id {string}")
    public void i_block_the_api(String apiId) throws Exception {
        publisher.blockAPI(apiId);
    }

    @When("I deprecate the API with id {string}")
    public void i_deprecate_the_api(String apiId) throws Exception {
        publisher.deprecateAPI(apiId);
    }

    @When("I create a new version {string} from API with id {string}")
    public void i_create_new_version_of_api(String newVersion, String apiId) throws Exception {
        publisher.createNewAPIVersion(newVersion, apiId, false);
    }

    @Then("I should be able to retrieve the API with id {string}")
    public void i_should_be_able_to_retrieve_the_api(String apiId) throws Exception {
        String actualApiId = apiId;
        if (apiId.startsWith("<") && apiId.endsWith(">")) {
            actualApiId = (String) context.get(apiId.substring(1, apiId.length() - 1));
        }

        HttpResponse response = publisher.getAPI(actualApiId);
        Assert.assertEquals(response.getResponseCode(), 200);
    }

    @Then("The lifecycle status of API {string} should be {string}")
    public void the_lifecycle_status_should_be(String apiId, String status) throws Exception {
        String actualApiId = apiId;
        if (apiId.startsWith("<") && apiId.endsWith(">")) {
            actualApiId = (String) context.get(apiId.substring(1, apiId.length() - 1));
        }
        HttpResponse response = publisher.getLifecycleStatus(actualApiId);
        Assert.assertEquals(response.getData(),status);
    }

    @Then("I search APIs with query {string}")
    public void i_search_apis_with_query(String query) throws Exception {
        publisher.searchAPIs(query);
    }

    @When("I add a document to API {string} with name {string}")
    public void i_add_document_to_api(String apiId, String docName) throws Exception {
        DocumentDTO doc = new DocumentDTO();
        doc.setName(docName);
        doc.setType(DocumentDTO.TypeEnum.HOWTO);
        doc.setSourceType(DocumentDTO.SourceTypeEnum.INLINE);
        doc.setVisibility(DocumentDTO.VisibilityEnum.API_LEVEL);
        HttpResponse response = publisher.addDocument(apiId, doc);
        createdDocId = response.getData();
    }

    @When("I add content {string} to document {string} of API {string}")
    public void i_add_content_to_document(String content, String docId, String apiId) throws Exception {
        publisher.addContentDocument(apiId, docId, content);
    }

    @When("I delete the document {string} from API {string}")
    public void i_delete_document(String docId, String apiId) throws Exception {
        publisher.deleteDocument(apiId, docId);
    }

    @When("I import Swagger file {string} for API with properties {string}")
    public void i_import_swagger_file(String filePath, String properties) throws Exception {
        File file = new File(filePath);
        publisher.importOASDefinition(file, properties);
    }

    @When("I import GraphQL schema file {string} for API with properties {string}")
    public void i_import_graphql_schema_file(String filePath, String properties) throws Exception {
        File file = new File(filePath);
        publisher.importGraphqlSchemaDefinition(file, properties);
    }

    @When("I import WSDL file {string} for API with properties {string}")
    public void i_import_wsdl_file(String filePath, String properties) throws Exception {
        File file = new File(filePath);
        publisher.importWSDLSchemaDefinition(file, null, properties, "WSDL1");
    }

    @When("I create a revision for API {string} with description {string}")
    public void i_create_api_revision(String apiId, String description) throws Exception {
        APIRevisionRequest request = new APIRevisionRequest();
        request.setApiUUID(apiId);
        request.setDescription(description);
        publisher.addAPIRevision(request);
    }

    @When("I deploy revision {string} of API {string} to gateway {string}")
    public void i_deploy_api_revision(String revisionId, String apiId, String gateway) throws Exception {
        APIRevisionDeployUndeployRequest deployRequest = new APIRevisionDeployUndeployRequest();
        deployRequest.setName(gateway);
        deployRequest.setVhost("localhost");
        deployRequest.setDisplayOnDevportal(true);
        publisher.deployAPIRevision(apiId, revisionId, deployRequest, "API");
    }

    @When("I undeploy revision {string} of API {string} from gateway {string}")
    public void i_undeploy_api_revision(String revisionId, String apiId, String gateway) throws Exception {
        APIRevisionDeployUndeployRequest deployRequest = new APIRevisionDeployUndeployRequest();
        deployRequest.setName(gateway);
        deployRequest.setVhost("localhost");
        deployRequest.setDisplayOnDevportal(true);
        List<APIRevisionDeployUndeployRequest> list = Collections.singletonList(deployRequest);
        publisher.undeployAPIRevision(apiId, revisionId, list);
    }

    @When("I upload endpoint certificate {string} with alias {string} for endpoint {string}")
    public void i_upload_endpoint_certificate(String certPath, String alias, String endpoint) throws Exception {
        File file = new File(certPath);
        publisher.uploadEndpointCertificate(file, alias, endpoint);
    }

    @When("I validate endpoint {string} for API {string}")
    public void i_validate_endpoint(String endpoint, String apiId) throws Exception {
        publisher.checkValidEndpoint(endpoint, apiId);
    }

//    @When("I create shared scope {string}")
//    public void i_create_shared_scope(String scopeName) throws Exception {
//        ScopeDTO scope = new ScopeDTO();
//        scope.setName(scopeName);
//        scope.setBindings(new ScopeBindingsDTO());
//        scope.getBindings().setType("default");
//        scope.getBindings().setValues(Collections.singletonList("admin"));
//        publisher.addSharedScope(scope);
//    }

    @When("I delete shared scope with id {string}")
    public void i_delete_shared_scope(String scopeId) throws Exception {
        publisher.deleteSharedScope(scopeId);
    }

    private String resolveFromContext(String input) {
        if (input.startsWith("<") && input.endsWith(">")) {
            return (String) context.get(input.substring(1, input.length() - 1));
        }
        return input;
    }

    @Then("I clean up API with id {string}")
    public void i_clean_up_api_and_application(String apiId, String appIdPlaceholder) throws Exception {
        String actualApiId = (String) context.get(apiId.substring(1, apiId.length() - 1));
        publisher.deleteAPI(actualApiId);
    }

}
