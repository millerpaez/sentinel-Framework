package com.miller.sentinel.app.hooks;

import com.miller.sentinel.core.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

    @Before
    public void logScenarioStart(Scenario scenario) {
        // Shows in the report which pool thread ran each scenario: evidence of parallel execution.
        scenario.log("Scenario '" + scenario.getName() + "' running on thread: " + Thread.currentThread().getName());
    }

    // Lower order runs last among @After hooks, so the browser closes after any API cleanup.
    @After(order = 0)
    public void closeBrowser(Scenario scenario) {
        if (!DriverManager.isStarted()) {
            return;
        }
        try {
            if (scenario.isFailed()) {
                byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "failure");
            }
        } finally {
            DriverManager.quit();
        }
    }
}
