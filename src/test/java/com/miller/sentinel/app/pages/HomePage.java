package com.miller.sentinel.app.pages;

import com.miller.sentinel.app.pages.components.HeaderComponent;
import com.miller.sentinel.core.ui.BasePage;

/**
 * Landing page ("/"). The site redirects here after a successful login.
 */
public class HomePage extends BasePage {

    private final HeaderComponent header = new HeaderComponent();

    public HeaderComponent header() {
        return header;
    }
}
