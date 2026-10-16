@ui @e2e
Feature: Purchase

  # End to end: user prepared through the API, the whole purchase through the UI, cleanup through the API (@After)
  @TC06
  Scenario: A registered user buys a product from the product page to the order confirmation
    Given a new user is registered through the API
    And I log in through the UI with that user's credentials
    When I add 3 units of the product with id 1 to the cart
    Then the cart should show that product with the right quantity and total
    When I proceed to checkout
    Then the delivery address should match the registered user
    When I place the order and pay with a test card
    Then I should see the order confirmation
