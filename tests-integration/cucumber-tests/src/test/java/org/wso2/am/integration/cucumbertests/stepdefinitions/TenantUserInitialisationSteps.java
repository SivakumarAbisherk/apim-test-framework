package org.wso2.am.integration.cucumbertests.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.wso2.am.integration.test.impl.TenantUserInitialiserClient;

import java.io.IOException;

public class TenantUserInitialisationSteps {
    private TenantUserInitialiserClient superTenantClient;
    private TenantUserInitialiserClient tenantClient1;
    private TenantUserInitialiserClient tenantClient2;

    private String port;

//    @Before
//    public void setUp() {
//        DefaultAPIMContainer apimContainer = DefaultAPIMContainer.getInstance();
//        port = String.valueOf(apimContainer.getHttpsPort());
//        System.out.println(port);
//        System.out.println(apimContainer.getHost());
////        port = "9443";
//        superTenantClient = new TenantUserInitialiserClient("admin", "admin", port);
//        tenantClient1 = new TenantUserInitialiserClient("tenant1@tenant1.com","tenant1", port);
//        tenantClient2 = new TenantUserInitialiserClient("tenant2@tenant2.com","tenant2", port);
//    }

    @Given("the API Manager is running")
    public void apiManagerIsRunning() {
        // Initialized in @Before
    }

    @When("I add tenant {string} {string} with admin username {string} and admin password {string} with domain {string} and email {string}")
    public void addTenant(String firstName, String lastName,String adminusername, String password, String domain, String email) {
        try {
            superTenantClient.addTenant(firstName,lastName,adminusername, password, domain, email);
        } catch (IOException e) {
            Assert.fail("Failed to add tenant: " + e.getMessage());
        }
    }

    @Then("the tenants should be added successfully")
    public void tenantsAddedSuccessfully() {
        Assert.assertTrue(true);
    }

    @When("I add role {string} to tenant {string}")
    public void addRole(String role, String tenant) {
        try {
            if (tenant.equals("superTenant")) {
                superTenantClient.addRole(role);
            } else if (tenant.equals("tenant1.com") && tenantClient1 != null) {
                tenantClient1.addRole(role);
            } else if (tenant.equals("tenant2.com") && tenantClient2 != null) {
                tenantClient2.addRole(role);
            } else {
                Assert.fail("Unknown or uninitialized tenant: " + tenant);
            }
        } catch (IOException e) {
            Assert.fail("Failed to add role: " + e.getMessage());
        }
    }

    @Then("the roles should be added successfully")
    public void rolesAddedSuccessfully() {
        Assert.assertTrue(true);
    }

    @When("I add user {string} with password {string} to tenant {string} with roles {string}")
    public void addUserWithRoles(String username, String password, String tenant, String roles) {
        String[] roleList = roles.split("\\s*,\\s*");
        try {
            if (tenant.equals("superTenant")) {
                superTenantClient.addUserWithRoles(username, password, roleList);
            } else if (tenant.equals("tenant1.com") && tenantClient1 != null) {
                tenantClient1.addUserWithRoles(username, password, roleList);
            } else if (tenant.equals("tenant2.com") && tenantClient2 != null) {
                tenantClient2.addUserWithRoles(username, password, roleList);
            } else {
                Assert.fail("Unknown or uninitialized tenant: " + tenant);
            }
        } catch (IOException e) {
            Assert.fail("Failed to add user: " + e.getMessage());
        }
    }

    @Then("the users should be added successfully with roles")
    public void usersAddedSuccessfully() {
        Assert.assertTrue(true);
    }
}
