@deposit @regression
Feature: Deposit funds into an account
    As a bank customer
    I want to deposit money into the account
    So that my balance reflects the funds I added

  Background:
    Given a customer with a checking account

  @smoke
  Scenario: Customer deposits a valid amount
    Given the account balance is recorded
    When the customer deposits 200.00 into their account
    Then the deposit is confirmed
    And the account balance has increased by 200.00



  Scenario Outline: Customer cannot deposit an invalid ammount
    Given the account balance is recorded
    When the customer deposits <amount> into the account
    Then the deposit is rejected
    And the account balance is unchanged

    Examples:
      | amount |
      |      0 |
      | -50.00 |
