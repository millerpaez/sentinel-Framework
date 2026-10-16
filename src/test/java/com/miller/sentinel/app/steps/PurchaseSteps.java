package com.miller.sentinel.app.steps;

import com.miller.sentinel.app.context.ScenarioContext;
import com.miller.sentinel.app.data.CartItem;
import com.miller.sentinel.app.data.PaymentCard;
import com.miller.sentinel.app.data.UserData;
import com.miller.sentinel.app.pages.CartPage;
import com.miller.sentinel.app.pages.CheckoutPage;
import com.miller.sentinel.app.pages.OrderPlacedPage;
import com.miller.sentinel.app.pages.ProductDetailsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class PurchaseSteps {

    private final ScenarioContext context;

    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private OrderPlacedPage orderPlacedPage;

    public PurchaseSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I add {int} units of the product with id {int} to the cart")
    public void iAddUnitsOfTheProductToTheCart(int quantity, int productId) {
        ProductDetailsPage product = new ProductDetailsPage().open(productId);
        // Expected values come from the product page itself, not hard-coded: the test survives catalog changes
        context.setExpectedCartItem(productId, CartItem.expected(product.getName(), product.getPrice(), quantity));
        cartPage = product.addToCart(quantity).goToCart();
    }

    @Then("the cart should show that product with the right quantity and total")
    public void theCartShouldShowThatProduct() {
        CartItem expected = context.getExpectedCartItem();
        assertThat(cartPage.getItem(context.getExpectedProductId())).isEqualTo(expected);
    }

    @When("I proceed to checkout")
    public void iProceedToCheckout() {
        checkoutPage = cartPage.proceedToCheckout();
    }

    @Then("the delivery address should match the registered user")
    public void theDeliveryAddressShouldMatchTheRegisteredUser() {
        UserData user = context.getCreatedUser();
        // Soft assertions: report every wrong field at once instead of stopping at the first one
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(checkoutPage.getDeliveryName())
                .isEqualTo(user.title() + ". " + user.firstname() + " " + user.lastname());
        softly.assertThat(checkoutPage.getDeliveryCityLine())
                .contains(user.city(), user.state(), user.zipcode());
        softly.assertThat(checkoutPage.getDeliveryCountry()).isEqualTo(user.country());
        softly.assertThat(checkoutPage.getDeliveryPhone()).isEqualTo(user.mobileNumber());
        softly.assertThat(checkoutPage.getTotalAmount()).as("order total").isEqualTo(context.getExpectedCartItem().total());
        softly.assertAll();
    }

    @When("I place the order and pay with a test card")
    public void iPlaceTheOrderAndPayWithATestCard() {
        UserData user = context.getCreatedUser();
        orderPlacedPage = checkoutPage
                .placeOrder()
                .payWith(PaymentCard.testVisaFor(user.firstname() + " " + user.lastname()));
    }

    @Then("I should see the order confirmation")
    public void iShouldSeeTheOrderConfirmation() {
        assertThat(orderPlacedPage.getTitle()).isEqualToIgnoringCase("Order Placed!");
        assertThat(orderPlacedPage.getMessage()).isEqualTo("Congratulations! Your order has been confirmed!");
    }
}
