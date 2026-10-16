package com.miller.sentinel.app.pages;

import com.miller.sentinel.core.ui.BasePage;
import org.openqa.selenium.By;

/**
 * "/checkout" page: delivery address review and order summary.
 */
public class CheckoutPage extends BasePage {

    private static final By DELIVERY_NAME = By.cssSelector("#address_delivery .address_firstname");
    private static final By DELIVERY_CITY_LINE = By.cssSelector("#address_delivery .address_city");
    private static final By DELIVERY_COUNTRY = By.cssSelector("#address_delivery .address_country_name");
    private static final By DELIVERY_PHONE = By.cssSelector("#address_delivery .address_phone");
    private static final By TOTAL_AMOUNT =
            By.xpath("//tr[.//b[normalize-space()='Total Amount']]//p[contains(@class, 'cart_total_price')]");

    /** "Mr. Ana Probe": title, first name and last name as rendered by the site. */
    public String getDeliveryName() {
        return textOf(DELIVERY_NAME);
    }

    /** "City State Zipcode" on one line. */
    public String getDeliveryCityLine() {
        return textOf(DELIVERY_CITY_LINE);
    }

    public String getDeliveryCountry() {
        return textOf(DELIVERY_COUNTRY);
    }

    public String getDeliveryPhone() {
        return textOf(DELIVERY_PHONE);
    }

    public int getTotalAmount() {
        return Prices.parse(textOf(TOTAL_AMOUNT));
    }

    /**
     * "Place Order" is a plain link to /payment (the order comment is not inside any form, so the site never
     * sends it). Clicking site links can trigger a full-screen Google ad that blocks the navigation, which
     * happened in CI, so we follow the link's URL directly: same destination, no ad.
     */
    public PaymentPage placeOrder() {
        openPath("/payment");
        return new PaymentPage();
    }
}
