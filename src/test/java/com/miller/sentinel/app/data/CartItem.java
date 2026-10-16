package com.miller.sentinel.app.data;

/**
 * One product line as the user sees it: name, unit price, quantity and line total (amounts in rupees).
 */
public record CartItem(String name, int unitPrice, int quantity, int total) {

    /** The line the user expects to see after adding {@code quantity} units of a product. */
    public static CartItem expected(String name, int unitPrice, int quantity) {
        return new CartItem(name, unitPrice, quantity, unitPrice * quantity);
    }
}
