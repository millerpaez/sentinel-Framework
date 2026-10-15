@ui
Feature: User login

  @TC03 @smoke
  Scenario: Login with invalid credentials shows an error
    Given I am on the login page
    When I log in with email "nobody@sentinel.test" and password "wrong123"
    Then I should see the login error message

  # Hybrid: data prepared through the API, behavior validated through the UI, cleanup through the API (@After)
  @TC04 @smoke @monitor
  Scenario: A user registered through the API can log in through the UI
    Given a new user is registered through the API
    When I log in through the UI with that user's credentials
    Then the header should show the user is logged in
