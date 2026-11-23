@login
Feature: Testing login functionality

  @PB-101
  Scenario: User logs in using correct authentication
    Given a user has an existing account with valid credentials
    When user calls a login request using their username and password
    Then status code should be 200

  @negativeLogin @PB-102
  Scenario Outline: Login fails with invalid credentials
    Given a user has invalid credentials for "<case>"
    When user calls a login request using their username and password
    Then status code should be 400
    And the response message should be "Invalid username and/or password"

    Examples:
      | case           |
      | wrong password |
      | unknown user   |
