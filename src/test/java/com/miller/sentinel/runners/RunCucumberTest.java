package com.miller.sentinel.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(
        features = "classpath:features",
        glue = "com.miller.sentinel.app",
        // Empty = every scenario. Gradle's -Dtags (and CI) always override this value.
        tags = "",
        plugin = {
                "pretty",
                "html:build/reports/cucumber/index.html",
                "json:build/reports/cucumber/cucumber.json"
        })
public class RunCucumberTest extends AbstractTestNGCucumberTests {

    @Override
    @Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        super.runScenario(pickleWrapper, featureWrapper);
    }

    /** parallel = true: TestNG hands each scenario to its pool; size set in testng.xml. */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
