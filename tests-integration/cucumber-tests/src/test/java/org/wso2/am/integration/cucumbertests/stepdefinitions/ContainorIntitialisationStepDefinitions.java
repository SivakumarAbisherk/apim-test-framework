package org.wso2.am.integration.cucumbertests.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.wso2.am.integration.cucumbertests.di.TestContext;
import org.wso2.am.testcontainers.CustomAPIMContainer;
import org.wso2.am.testcontainers.DefaultAPIMContainer;
import org.wso2.am.testcontainers.NodeAppServer;
import org.wso2.am.testcontainers.TomcatServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ContainorIntitialisationStepDefinitions {
    String baseUrl;
    String serviceBaseUrl;
    String baseGatewayUrl;
    Integer HTTPS_PORT=8243;
    Integer HTTP_PORT=8280;
    CustomAPIMContainer customApimContainer;


    private final TestContext context;

    public ContainorIntitialisationStepDefinitions(TestContext context) {
        this.context = context;
    }

    @Given("I have initialized the Default API Manager container")
    public void initializeDefaultAPIMContainer() {
        DefaultAPIMContainer apimContainer = DefaultAPIMContainer.getInstance();
        baseUrl = apimContainer.getAPIManagerUrl();
        context.set("baseUrl",baseUrl);
        Integer gatewayPort= apimContainer.getMappedPort(HTTPS_PORT);
        String gatewayHost = apimContainer.getHost();
        baseGatewayUrl= String.format("https://%s:%d", gatewayHost, gatewayPort);
        context.set("baseGatewayUrl",baseGatewayUrl);
        context.set("label","default");
    }

    @Given("I have initialized the Custom API Manager container with label {string} and deployment toml file path at {string}")
    public void initializeCustomAPIMContainer(String label,String tomlPath) throws IOException, InterruptedException {
        String baseDir = System.getProperty("user.dir");
        System.out.println(baseDir);
        String fullPath = baseDir+tomlPath;
        customApimContainer = new CustomAPIMContainer(label,fullPath);
        customApimContainer.start();

        // Verifying that the file was copied correctly
        String filePathInsideContainer = "/opt/repository/conf/deployment.toml";
        String fileContents = customApimContainer.execInContainer("cat", filePathInsideContainer).getStdout();
        System.out.println("Contents of the copied deployment.toml inside the container:");
        System.out.println(fileContents);

        baseUrl = customApimContainer.getAPIManagerUrl();
        context.set("baseUrl",baseUrl);
        Integer gatewayPort= customApimContainer.getMappedPort(HTTPS_PORT);
        String gatewayHost = customApimContainer.getHost();
        baseGatewayUrl= String.format("https://%s:%d", gatewayHost, gatewayPort);
        context.set("baseGatewayUrl",baseGatewayUrl);
        context.set("label",label);
    }

    @Then("I stop the Custom API Manager container")
    public void endCustomAPIMContainer() throws InterruptedException {
//       customApimContainer.stop();
       customApimContainer.close();
//       Thread.sleep(3000);
    }

    @Given("I have initialized the Tomcat server container")
    public void initializeTomcatServerContainer() {

        TomcatServer tomcat = TomcatServer.getInstance();
        serviceBaseUrl = "http://tomcatbackend:8080/";
        context.set("serviceBaseUrl",serviceBaseUrl);
    }

    @Given("I have initialized the NodeApp server container")
    public void initializeNodeAppServerContainer() {
        NodeAppServer nodeapp = NodeAppServer.getInstance();
        serviceBaseUrl = "http://nodebackend:8080/";

        context.set("serviceBaseUrl",serviceBaseUrl);

    }

    // DefaultAPIM step
    @Given("I have initialized test instance")
    public void initializeAPIMContainer() {
        baseUrl = "http://localhost:9443/";
        context.set("baseUrl",baseUrl);
        baseGatewayUrl="https://localhost:8243";
        context.set("baseGatewayUrl",baseGatewayUrl);
        serviceBaseUrl = "http://nodebackend:8080/";
        context.set("serviceBaseUrl",serviceBaseUrl);
    }

    @Then("I clear the context")
    public void clearContext(){
        context.clear();
    }

}


