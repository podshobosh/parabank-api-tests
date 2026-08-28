Feature: Create Account
  @createAccount
  Scenario: User creates a new checking Account
    Given a customer logs in and has an existing funding account
    When user creates a new account of type "CHECKING"
    Then the account should be created successfully with status code 200
