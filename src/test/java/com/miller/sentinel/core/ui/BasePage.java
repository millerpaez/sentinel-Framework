package com.miller.sentinel.core.ui;

import com.miller.sentinel.core.config.Config;
import com.miller.sentinel.core.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Common base for pages and components: every interaction goes through an explicit wait.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.getInt("timeout.seconds")));
        this.js = (JavascriptExecutor) driver;
    }

    protected void openPath(String path) {
        driver.get(Config.get("base.url") + path);
    }

    /**
     * Waits until the browser lands on a path. Use it after an action that triggers a redirect:
     * navigating again before the redirect finishes lets the redirect override the new URL.
     */
    protected void waitForPath(String path) {
        wait.until(ExpectedConditions.urlToBe(Config.get("base.url") + path));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    protected String textOf(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
    }

    /** Waits up to the configured timeout; returns false instead of throwing if the element never shows. */
    protected boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---- JavaScript: only for what the WebDriver API cannot do (scroll position, scroll commands) ----

    /** Scrolls to the very bottom of the page, as a user reaching the footer would. */
    protected void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /** Centers the element in the viewport, so sticky headers or ads at the edges cannot cover it. */
    protected void scrollIntoView(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    /** Current vertical scroll offset in pixels. JS numbers arrive as Long or Double, so read them as Number. */
    protected long scrollY() {
        return ((Number) js.executeScript("return window.pageYOffset;")).longValue();
    }

    /** Waits for an animated scroll to finish at the top; returns false if it never gets there. */
    protected boolean waitUntilScrolledToTop() {
        try {
            wait.until(d -> scrollY() == 0);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
