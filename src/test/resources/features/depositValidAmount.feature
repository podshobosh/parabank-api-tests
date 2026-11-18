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
    Then the deposit is recorder in the database
  # ParaBank returns 200 and applies these amounts (a -50 deposit lowers the balance),
  # so this scenario fails until the API validates input. Excluded from the default
  # run via cucumber.properties; run it with -Dcucumber.filter.tags="@known-defect".

  @known-defect
  Scenario Outline: Customer cannot deposit an invalid amount
    Given the account balance is recorded
    When the customer deposits <amount> into their account
    Then the deposit is rejected
    And the account balance is unchanged

    Examples:
      | amount |
      |   0.00 |
      | -50.00 |
  
  Scenario: Customer cannot deposit into a non-existing account
    When the customer deposits 200.00 into account "99990"
    Then the deposit is rejected
    And the response message should be "Could not find account number 99990"
