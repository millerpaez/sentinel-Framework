package com.miller.sentinel.app.pages;

import com.miller.sentinel.app.data.CartItem;
import com.miller.sentinel.core.ui.BasePage;
import org.openqa.selenium.By;

/**
 * "/view_cart" page.
 */
public class CartPage extends BasePage {

    private static final By PROCEED_TO_CHECKOUT = By.cssSelector("a.check_out");

    public CartPage open() {
        openPath("/view_cart");
        return this;
    }

    /** Reads the cart row of one product (each row has id "product-{id}"). */
    public CartItem getItem(int productId) {
        String row = "#product-" + productId;
        return new CartItem(
                textOf(By.cssSelector(row + " .cart_description h4")),
                Prices.parse(textOf(By.cssSelector(row + " .cart_price"))),
                Integer.parseInt(textOf(By.cssSelector(row + " .cart_quantity"))),
                Prices.parse(textOf(By.cssSelector(row + " .cart_total"))));
    }

    /** For a logged-in user the site's script sends the browser to /checkout. */
    public CheckoutPage proceedToCheckout() {
        click(PROCEED_TO_CHECKOUT);
        return new CheckoutPage();
    }
}
