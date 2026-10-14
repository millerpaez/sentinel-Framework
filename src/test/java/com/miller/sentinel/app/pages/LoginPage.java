package com.miller.sentinel.app.pages;

import com.miller.sentinel.app.pages.components.HeaderComponent;
import com.miller.sentinel.core.ui.BasePage;
import org.openqa.selenium.By;

/**
 * "/login" page. Only the login half of the page is modeled; signup is created through the API.
 */
public class LoginPage extends BasePage {

    private static final String PATH = "/login";

    private static final By EMAIL_INPUT = By.cssSelector("[data-qa='login-email']");
    private static final By PASSWORD_INPUT = By.cssSelector("[data-qa='login-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("[data-qa='login-button']");
    private static final By ERROR_MESSAGE = By.cssSelector(".login-form form p");

    private final HeaderComponent header = new HeaderComponent();

    /** Navigates by URL instead of clicking the menu, so ads cannot intercept the click. */
    public LoginPage open() {
        openPath(PATH);
        return this;
    }

    /** Logs in with valid credentials; the site redirects to the home page. */
    public HomePage loginAs(String email, String password) {
        submitCredentials(email, password);
        return new HomePage();
    }

    /** Submits credentials the site will reject; the browser stays on the login page. */
    public LoginPage loginExpectingError(String email, String password) {
        submitCredentials(email, password);
        return this;
    }

    public String getErrorMessage() {
        return textOf(ERROR_MESSAGE);
    }

    public HeaderComponent header() {
        return header;
    }

    private void submitCredentials(String email, String password) {
        type(EMAIL_INPUT, email);
        type(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
    }
}
