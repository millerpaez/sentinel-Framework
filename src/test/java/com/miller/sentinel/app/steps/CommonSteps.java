package com.miller.sentinel.app.steps;

import com.miller.sentinel.app.context.ScenarioContext;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assertions on the last API response, reusable by any API feature.
 */
public class CommonSteps {

    private final ScenarioContext context;

    public CommonSteps(ScenarioContext context) {
        this.context = context;
    }

    @Then("the API response code should be {int}")
    public void theApiResponseCodeShouldBe(int expected) {
        assertThat(context.getLastApiResult().responseCode())
                .as("responseCode in body (message: %s)", context.getLastApiResult().message())
                .isEqualTo(expected);
    }

    @Then("the API message should be {string}")
    public void theApiMessageShouldBe(String expected) {
        assertThat(context.getLastApiResult().message()).isEqualTo(expected);
    }
}
