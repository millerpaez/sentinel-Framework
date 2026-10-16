package com.miller.sentinel.app.pages;

/**
 * The site renders every amount as "Rs. 1500"; pages expose them as plain integers.
 */
final class Prices {

    private Prices() {
    }

    static int parse(String text) {
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }
}
