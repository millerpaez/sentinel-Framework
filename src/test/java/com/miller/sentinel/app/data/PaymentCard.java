package com.miller.sentinel.app.data;

/**
 * Card data for the site's fake payment form.
 */
public record PaymentCard(String nameOnCard, String number, String cvc, String expiryMonth, String expiryYear) {

    /** 4111 1111 1111 1111 is the industry-standard Visa test number: it can never charge anyone. */
    public static PaymentCard testVisaFor(String nameOnCard) {
        return new PaymentCard(nameOnCard, "4111111111111111", "123", "12", "2030");
    }
}
