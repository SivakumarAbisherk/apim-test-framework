package org.wso2.am.integration.cucumbertests.runners;

import org.testng.annotations.Test;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;

@CucumberOptions(
        features = "src/test/resources/features/api_lifecycle_test.feature", // Path to the feature files
        glue = "org.wso2.am.integration.cucumbertests.stepdefinitions", // Path to step definition package
        plugin = {"pretty", "html:target/cucumber-reports.html"} // Optional: Reporting
)

public class testrunner extends AbstractTestNGCucumberTests {
}

