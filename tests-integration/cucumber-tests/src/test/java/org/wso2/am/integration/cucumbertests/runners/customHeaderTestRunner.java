package org.wso2.am.integration.cucumbertests.runners;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;

@CucumberOptions(
        features = "src/test/resources/features/customHeaderTest", // Path to the feature files
        glue = "org.wso2.am.integration.cucumbertests.stepdefinitions", // Path to step definition package
        plugin = {"pretty", "html:target/cucumber-reports.html"} // Optional: Reporting
)
public class customHeaderTestRunner extends AbstractTestNGCucumberTests{
}
