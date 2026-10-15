package com.miller.sentinel.app.steps;

import com.miller.sentinel.app.context.ScenarioContext;
import com.miller.sentinel.app.data.UserData;
import com.miller.sentinel.app.pages.HomePage;
import com.miller.sentinel.app.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private static final String INVALID_CREDENTIALS_ERROR = "Your email or password is incorrect!";

    private final ScenarioContext context;

    // Step classes are created per scenario by PicoContainer, so these fields never leak between scenarios.
    private LoginPage loginPage;
    private HomePage homePage;

    public LoginSteps(ScenarioContext context) {
        this.context = context;
    }

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

    @When("I log in through the UI with that user's credentials")
    public void iLogInThroughTheUiWithThatUsersCredentials() {
        UserData user = context.getCreatedUser();
        homePage = new LoginPage().open().loginAs(user.email(), user.password());
    }

    @Then("the header should show the user is logged in")
    public void theHeaderShouldShowTheUserIsLoggedIn() {
        assertThat(homePage.header().isUserLoggedIn()).as("user should be logged in").isTrue();
        assertThat(homePage.header().getLoggedUserName()).isEqualTo(context.getCreatedUser().name());
    }
}
