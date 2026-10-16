@ui
Feature: Page navigation

  # Scroll position is not exposed by the WebDriver API: the steps read it through JavascriptExecutor
  @TC05
  Scenario: The scroll-up arrow takes the user back to the top of the home page
    Given I am on the home page
    When I scroll down to the footer
    Then the subscription section should be visible
    When I click the scroll-up arrow
    Then the page should be back at the top
