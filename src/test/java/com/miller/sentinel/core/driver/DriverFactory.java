package com.miller.sentinel.core.driver;

import com.miller.sentinel.core.config.Config;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Builds browser instances. Selenium Manager resolves the matching chromedriver automatically.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        // EAGER: return once the DOM is ready instead of waiting for every ad and tracker to load.
        // Explicit waits in BasePage cover the elements we actually use.
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        if (Config.getBool("headless")) {
            options.addArguments(
                    "--headless=new",
                    "--window-size=1920,1080",
                    "--no-sandbox",
                    "--disable-dev-shm-usage");
        }

        WebDriver driver = new ChromeDriver(options);
        if (!Config.getBool("headless")) {
            driver.manage().window().maximize();
        }
        return driver;
    }
}
