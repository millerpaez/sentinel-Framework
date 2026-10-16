package com.miller.sentinel.app.pages;

import com.miller.sentinel.app.pages.components.HeaderComponent;
import com.miller.sentinel.core.ui.BasePage;
import org.openqa.selenium.By;

/**
 * Landing page ("/"). The site redirects here after a successful login.
 */
public class HomePage extends BasePage {

    private static final By HERO_TITLE = By.cssSelector("#slider-carousel .item.active h2");
    private static final By SUBSCRIPTION_TITLE = By.cssSelector("#footer .single-widget h2");
    // Created by the jquery.scrollUp plugin once the page is scrolled more than 300px
    private static final By SCROLL_UP_ARROW = By.id("scrollUp");

    private final HeaderComponent header = new HeaderComponent();

    public HomePage open() {
        openPath("/");
        return this;
    }

    public HomePage scrollToFooter() {
        scrollToBottom();
        return this;
    }

    /** Clicks the floating arrow and waits for its scroll animation to reach the top. */
    public HomePage scrollUpWithArrow() {
        click(SCROLL_UP_ARROW);
        waitUntilScrolledToTop();
        return this;
    }

    public boolean isSubscriptionSectionVisible() {
        return isVisible(SUBSCRIPTION_TITLE);
    }

    public String getSubscriptionTitle() {
        return textOf(SUBSCRIPTION_TITLE);
    }

    public boolean isHeroTitleVisible() {
        return isVisible(HERO_TITLE);
    }

    public long getScrollPosition() {
        return scrollY();
    }

    public HeaderComponent header() {
        return header;
    }
}
