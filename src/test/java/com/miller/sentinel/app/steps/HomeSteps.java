package com.miller.sentinel.app.steps;

import com.miller.sentinel.app.pages.HomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeSteps {

    private HomePage homePage;

    @Given("I am on the home page")
    public void iAmOnTheHomePage() {
        homePage = new HomePage().open();
    }

    @When("I scroll down to the footer")
    public void iScrollDownToTheFooter() {
        homePage.scrollToFooter();
    }

    @Then("the subscription section should be visible")
    public void theSubscriptionSectionShouldBeVisible() {
        assertThat(homePage.isSubscriptionSectionVisible()).as("subscription section visible").isTrue();
        assertThat(homePage.getSubscriptionTitle()).isEqualToIgnoringCase("Subscription");
        assertThat(homePage.getScrollPosition()).as("page scrolled down (px)").isPositive();
    }

    @When("I click the scroll-up arrow")
    public void iClickTheScrollUpArrow() {
        homePage.scrollUpWithArrow();
    }

    @Then("the page should be back at the top")
    public void thePageShouldBeBackAtTheTop() {
        assertThat(homePage.getScrollPosition()).as("scroll position (px)").isZero();
        assertThat(homePage.isHeroTitleVisible()).as("hero title visible").isTrue();
    }
}
