@security
Feature: Authorization
    A customer can only be able to read their own accounts

  @PB-501 @known-defect
  Scenario: A customer cannot read another customer's account
    Given a customer is logged in
    When they request an account that belongs to another customer
    Then status code should be 403
