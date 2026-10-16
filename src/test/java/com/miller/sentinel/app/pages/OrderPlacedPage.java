package com.miller.sentinel.app.pages;

import com.miller.sentinel.core.ui.BasePage;
import org.openqa.selenium.By;

/**
 * "/payment_done/{amount}" confirmation page.
 */
public class OrderPlacedPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-qa='order-placed']");
    private static final By MESSAGE = By.cssSelector("[data-qa='order-placed'] + p");

    public String getTitle() {
        return textOf(TITLE);
    }

    public String getMessage() {
        return textOf(MESSAGE);
    }
}
