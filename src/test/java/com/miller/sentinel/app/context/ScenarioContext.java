package com.miller.sentinel.app.context;

import com.miller.sentinel.app.data.CartItem;
import com.miller.sentinel.app.data.UserData;
import com.miller.sentinel.core.api.ApiResult;

/**
 * State shared between step classes and hooks within ONE scenario.
 *
 * <p>PicoContainer creates a fresh instance per scenario and injects the same instance into every
 * step/hook constructor that asks for it, so nothing leaks into the next scenario.
 */
public class ScenarioContext {

    private UserData createdUser;
    private ApiResult lastApiResult;
    private CartItem expectedCartItem;
    private int expectedProductId;

    public UserData getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(UserData createdUser) {
        this.createdUser = createdUser;
    }

    public boolean hasCreatedUser() {
        return createdUser != null;
    }

    public ApiResult getLastApiResult() {
        return lastApiResult;
    }

    public void setLastApiResult(ApiResult lastApiResult) {
        this.lastApiResult = lastApiResult;
    }

    /** What the user put in the cart, captured from the product page so later steps can compare against it. */
    public CartItem getExpectedCartItem() {
        return expectedCartItem;
    }

    public int getExpectedProductId() {
        return expectedProductId;
    }

    public void setExpectedCartItem(int productId, CartItem expectedCartItem) {
        this.expectedProductId = productId;
        this.expectedCartItem = expectedCartItem;
    }
}
