package com.miller.sentinel.app.pages.components;

import com.miller.sentinel.core.ui.BasePage;
import org.openqa.selenium.By;

/**
 * Top navigation bar shared by every page of the site.
 */
public class HeaderComponent extends BasePage {

    private static final By LOGGED_IN_AS = By.xpath("//header//a[contains(., 'Logged in as')]");
    private static final By LOGGED_USER_NAME = By.xpath("//header//a[contains(., 'Logged in as')]/b");
    private static final By LOGOUT_LINK = By.cssSelector("header a[href='/logout']");

    public boolean isUserLoggedIn() {
        return isVisible(LOGGED_IN_AS) && isVisible(LOGOUT_LINK);
    }

    public String getLoggedUserName() {
        return textOf(LOGGED_USER_NAME);
    }
}
