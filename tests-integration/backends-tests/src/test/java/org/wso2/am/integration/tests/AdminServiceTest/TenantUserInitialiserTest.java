package org.wso2.am.integration.tests.AdminServiceTest;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.am.integration.test.impl.TenantUserInitialiserClient;
import org.wso2.am.testcontainers.DefaultAPIMContainer;

import java.io.IOException;

public class TenantUserInitialiserTest {
    private TenantUserInitialiserClient superTenantClient;
    private TenantUserInitialiserClient tenantClient1;
    private TenantUserInitialiserClient tenantClient2;

    @BeforeClass
    public void setUp() {
        DefaultAPIMContainer apimContainer = DefaultAPIMContainer.getInstance();
        String port = String.valueOf(apimContainer.getHttpsPort());
//        String port="9443";
//        System.out.println(port);
//        System.out.println(apimContainer.getHost());
        superTenantClient = new TenantUserInitialiserClient("admin","admin", port);
        tenantClient1 = new TenantUserInitialiserClient("tenant1@tenant1.com","tenant1", port);
        tenantClient2 = new TenantUserInitialiserClient("tenant2@tenant2.com","tenant2", port);
    }

    @Test()
    public void testAddTenant() {
        try {
            superTenantClient.addTenant("First", "Tenant","tenant1","tenant1","tenant1.com", "admin@tenant1.com");
            superTenantClient.addTenant("Second", "Tenant","tenant2","tenant2","tenant2.com", "admin@tenant2.com");
            Assert.assertTrue(true, "Tenants added successfully");
        } catch (IOException e) {
            Assert.fail("Failed to add tenant: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods="testAddTenant")
    public void testAddRole() {
        try {
            superTenantClient.addRole("test_role_super");
            tenantClient1.addRole("test_role_1");
            tenantClient2.addRole("test_role_2");
            Assert.assertTrue(true, "Roles added successfully");
        } catch (IOException e) {
            Assert.fail("Failed to add role: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods="testAddRole")
    public void testAddUserWithRoles() {
        try {
            superTenantClient.addUserWithRoles("user1", "user1","admin","Internal/analytics");
            superTenantClient.addUserWithRoles("user2","user2","Internal/analytics","test_role_super","Internal/system");
            tenantClient1.addUserWithRoles("user3","user3","admin","Internal/analytics","Internal/integration_dev","Internal/publisher");
            tenantClient1.addUserWithRoles("user4","user4","test_role_1");
            tenantClient2.addUserWithRoles("user5","user5","Internal/analytics","Internal/integration_dev","Internal/publisher","Internal/creator","Internal/system");
            tenantClient2.addUserWithRoles("user6","user6","admin","test_role_2");
            Assert.assertTrue(true, "Users added successfully with roles");
        } catch (IOException e) {
            Assert.fail("Failed to add user with roles: " + e.getMessage());
        }
    }
}