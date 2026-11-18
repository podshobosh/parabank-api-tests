Feature: Account lookup by account ID
@lookUp
    Scenario Outline: Account lookup returns expected status code
        Given an account id is "<accountId>"
        When the account is requested
        Then status code should be <status>

        Examples:
            | accountId | status |
            | 12345     | 200    |
            | 99999     | 400    |
    @lookUp        
    Scenario: Existing account matches the account schema
        Given an account id is "12345"
        When the account is requested
        Then the response matches the account schema