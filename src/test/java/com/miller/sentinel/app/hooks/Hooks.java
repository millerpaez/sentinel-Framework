package com.miller.sentinel.app.hooks;

import com.miller.sentinel.app.api.AutomationExerciseApi;
import com.miller.sentinel.app.context.ScenarioContext;
import com.miller.sentinel.app.data.UserData;
import com.miller.sentinel.core.api.ApiResult;
import com.miller.sentinel.core.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

    private final ScenarioContext context;
    private final AutomationExerciseApi api;

    public Hooks(ScenarioContext context, AutomationExerciseApi api) {
        this.context = context;
        this.api = api;
    }

    @Before
    public void logScenarioStart(Scenario scenario) {
        // Shows in the report which pool thread ran each scenario: evidence of parallel execution.
        scenario.log("Scenario '" + scenario.getName() + "' running on thread: " + Thread.currentThread().getName());
    }

    // Higher order runs first among @After hooks: delete test data before the browser closes.
    @After(order = 100)
    public void deleteCreatedUser(Scenario scenario) {
        if (!context.hasCreatedUser()) {
            return;
        }
        UserData user = context.getCreatedUser();
        try {
            ApiResult result = api.deleteAccount(user.email(), user.password());
            scenario.log("Cleanup: deleteAccount " + user.email() + " -> " + result.responseCode() + " " + result.message());
        } catch (RuntimeException e) {
            // A failed cleanup must not turn a passing scenario red; leave a trace in the report instead.
            scenario.log("Cleanup failed for " + user.email() + ": " + e);
        }
    }

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
            scenario.log("Browser closed on thread: " + Thread.currentThread().getName());
        }
    }
}
