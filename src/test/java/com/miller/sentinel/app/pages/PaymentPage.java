package com.miller.sentinel.app.pages;

import com.miller.sentinel.app.data.PaymentCard;
import com.miller.sentinel.core.ui.BasePage;
import org.openqa.selenium.By;

/**
 * "/payment" page. The site accepts any card data: no real payment happens.
 */
public class PaymentPage extends BasePage {

    private static final By NAME_ON_CARD = By.cssSelector("[data-qa='name-on-card']");
    private static final By CARD_NUMBER = By.cssSelector("[data-qa='card-number']");
    private static final By CVC = By.cssSelector("[data-qa='cvc']");
    private static final By EXPIRY_MONTH = By.cssSelector("[data-qa='expiry-month']");
    private static final By EXPIRY_YEAR = By.cssSelector("[data-qa='expiry-year']");
    private static final By PAY_BUTTON = By.cssSelector("[data-qa='pay-button']");

    public OrderPlacedPage payWith(PaymentCard card) {
        type(NAME_ON_CARD, card.nameOnCard());
        type(CARD_NUMBER, card.number());
        type(CVC, card.cvc());
        type(EXPIRY_MONTH, card.expiryMonth());
        type(EXPIRY_YEAR, card.expiryYear());
        scrollIntoView(PAY_BUTTON);
        click(PAY_BUTTON);
        return new OrderPlacedPage();
    }
}
