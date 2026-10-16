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
    private static final By ORDER_COMMENT = By.name("message");
    private static final By PLACE_ORDER = By.cssSelector("a[href='/payment']");

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

    public PaymentPage placeOrder(String comment) {
        type(ORDER_COMMENT, comment);
        scrollIntoView(PLACE_ORDER);
        click(PLACE_ORDER);
        return new PaymentPage();
    }
}
