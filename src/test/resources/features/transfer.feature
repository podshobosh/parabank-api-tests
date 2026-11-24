@transfer
Feature: Transfer funds between a customer's accounts

  @PB-601
  Scenario: A customer wants to move money from one account to another
    Given a customer with two checking accounts
    And the balance of both accounts is recorded
    When user transfers 20.00 dollars from the first account to the second
    Then the transfer is confirmed
    And the first account balance is decreased by 20.00
    And the second account balance is increased by 20.00

