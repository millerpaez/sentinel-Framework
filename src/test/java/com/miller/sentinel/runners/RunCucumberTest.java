package com.miller.sentinel.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "classpath:features",
        glue = "com.miller.sentinel.app",
        plugin = {
                "pretty",
                "html:build/reports/cucumber/index.html",
                "json:build/reports/cucumber/cucumber.json"
        })
public class RunCucumberTest extends AbstractTestNGCucumberTests {

    /** parallel = true: TestNG hands each scenario to its pool; size set in testng.xml. */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
