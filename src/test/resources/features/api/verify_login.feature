@api
Feature: Login verification API

  # An empty cell means the parameter is not sent at all (the API treats "missing" and "empty" differently).
  @TC02
  Scenario Outline: Invalid login requests are rejected
    When I verify login with email "<email>" and password "<password>"
    Then the API response code should be <code>
    And the API message should be "<message>"

    Examples:
      | email                | password | code | message                                                              |
      | nobody@sentinel.test | wrong123 | 404  | User not found!                                                      |
      |                      | wrong123 | 400  | Bad request, email or password parameter is missing in POST request. |
