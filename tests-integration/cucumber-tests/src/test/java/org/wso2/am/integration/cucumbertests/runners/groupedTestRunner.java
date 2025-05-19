package org.wso2.am.integration.cucumbertests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

@CucumberOptions(
        glue = "org.wso2.am.integration.cucumbertests.stepdefinitions",
        plugin = {"pretty", "html:target/cucumber-report/groupedtestrunner.html"}
)

@Test(groups = "groupTest")
public class groupedTestRunner extends AbstractTestNGCucumberTests{
    static {
        String featurePath = System.getProperty("feature", "src/test/resources/features/allowedScopes");
        System.setProperty("cucumber.features", featurePath);
    }
}
