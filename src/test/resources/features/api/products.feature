@api @smoke
Feature: Products catalog API

  Scenario: The products list returns available products
    When I request the products list
    Then the API response code should be 200
    And the products list should not be empty
