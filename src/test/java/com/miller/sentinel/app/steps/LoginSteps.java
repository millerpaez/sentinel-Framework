package com.miller.sentinel.app.steps;

import com.miller.sentinel.app.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private static final String INVALID_CREDENTIALS_ERROR = "Your email or password is incorrect!";

    // Step classes are created per scenario by PicoContainer, so this field never leaks between scenarios.
    private LoginPage loginPage;

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage = new LoginPage().open();
    }

    @When("I log in with email {string} and password {string}")
    public void iLogInWithEmailAndPassword(String email, String password) {
        loginPage = loginPage.loginExpectingError(email, password);
    }

    @Then("I should see the login error message")
    public void iShouldSeeTheLoginErrorMessage() {
        assertThat(loginPage.getErrorMessage()).isEqualTo(INVALID_CREDENTIALS_ERROR);
        assertThat(loginPage.header().isUserLoggedIn()).as("user should not be logged in").isFalse();
    }
}
