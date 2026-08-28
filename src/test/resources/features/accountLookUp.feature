Feature: Account lookup by account ID
@wip
    Scenario Outline: Account lookup returns expected status code
        Given an account id is "<accountId>"
        When the account is requested
        Then status code should be <status>

        Examples:
            | accountId | status |
            | 12345     | 200    |
            | 99999     | 400    |
