package com.miller.sentinel.app.steps;

import com.miller.sentinel.app.api.AutomationExerciseApi;
import com.miller.sentinel.app.context.ScenarioContext;
import com.miller.sentinel.app.data.UserBuilder;
import com.miller.sentinel.app.data.UserData;
import com.miller.sentinel.core.api.ApiResult;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiSteps {

    private final ScenarioContext context;
    private final AutomationExerciseApi api;

    public ApiSteps(ScenarioContext context, AutomationExerciseApi api) {
        this.context = context;
        this.api = api;
    }

    @When("I request the products list")
    public void iRequestTheProductsList() {
        context.setLastApiResult(api.getProductsList());
    }

    @When("I verify login with email {string} and password {string}")
    public void iVerifyLoginWithEmailAndPassword(String email, String password) {
        context.setLastApiResult(api.verifyLogin(email, password));
    }

    @Then("the products list should not be empty")
    public void theProductsListShouldNotBeEmpty() {
        assertThat(context.getLastApiResult().body().getList("products")).isNotEmpty();
    }

    @Given("a new user is registered through the API")
    public void aNewUserIsRegisteredThroughTheApi() {
        UserData user = UserBuilder.aUser().build();
        ApiResult result = api.createAccount(user);
        if (result.responseCode() == 201) {
            // Registered only on success, so the @After cleanup never deletes an account we did not create
            context.setCreatedUser(user);
        }
        assertThat(result.responseCode())
                .as("createAccount responseCode (message: %s)", result.message())
                .isEqualTo(201);
    }
}
