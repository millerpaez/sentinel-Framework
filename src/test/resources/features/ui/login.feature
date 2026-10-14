@ui
Feature: User login

  @smoke
  Scenario: Login with invalid credentials shows an error
    Given I am on the login page
    When I log in with email "nobody@sentinel.test" and password "wrong123"
    Then I should see the login error message
