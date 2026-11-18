Feature: Account lookup by account ID

  @lookUp
  Scenario: Account lookup returns expected status code
    Given a customer with a checking account
    When the account is requested
    Then status code should be 200
    And the response matches the account schema

@lookUp
  Scenario: Non-existent account is rejected
    Given non-existent account
    When the account is requested
    Then status code should be 400

 