package com.miller.sentinel.core.driver;

import org.openqa.selenium.WebDriver;

/**
 * Holds one browser per thread.
 *
 * <p>TestNG runs scenarios on a pool of reused threads. Each thread lazily gets its own driver on first
 * UI use, and {@link #quit()} always clears the slot so the next scenario on that thread starts clean.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    /** Returns this thread's driver, creating it on first use. API-only scenarios never call this. */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            driver = DriverFactory.createDriver();
            DRIVER.set(driver);
        }
        return driver;
    }

    public static boolean isStarted() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            // Without remove() a reused pool thread would hand the next scenario a dead session.
            DRIVER.remove();
        }
    }
}
