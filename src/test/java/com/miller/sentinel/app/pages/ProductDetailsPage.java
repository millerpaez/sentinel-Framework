package com.miller.sentinel.app.pages;

import com.miller.sentinel.core.ui.BasePage;
import org.openqa.selenium.By;

/**
 * "/product_details/{id}" page.
 */
public class ProductDetailsPage extends BasePage {

    private static final By NAME = By.cssSelector(".product-information h2");
    private static final By PRICE = By.cssSelector(".product-information > span > span");
    private static final By QUANTITY_INPUT = By.id("quantity");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector(".product-information button.cart");
    // cart.js adds the "show" class to the modal when the AJAX call to /add_to_cart succeeds
    private static final By ADDED_MODAL_TITLE = By.cssSelector("#cartModal.show .modal-title");

    public ProductDetailsPage open(int productId) {
        openPath("/product_details/" + productId);
        return this;
    }

    public String getName() {
        return textOf(NAME);
    }

    /** Price as an integer: the site shows it as "Rs. 500". */
    public int getPrice() {
        return Prices.parse(textOf(PRICE));
    }

    /** Sets the quantity and adds the product, waiting until the site confirms it ("Added!"). */
    public ProductDetailsPage addToCart(int quantity) {
        type(QUANTITY_INPUT, String.valueOf(quantity));
        scrollIntoView(ADD_TO_CART_BUTTON);
        click(ADD_TO_CART_BUTTON);
        textOf(ADDED_MODAL_TITLE);
        return this;
    }

    /** Navigates by URL instead of the modal link, so ads cannot intercept the click. */
    public CartPage goToCart() {
        return new CartPage().open();
    }
}
