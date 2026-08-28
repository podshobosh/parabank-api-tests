Feature: Testing login functionality

    Scenario: User logs in using correct authentication
        Given a user has an existing account with valid credentials
        When user calls a login request using their username and password
        Then status code should be 200

    #Negative Test
    Scenario: Login fails with invalid credentials
        Given a user has invalid credentials
        When user calls a login request using their username and password
        Then status code should be 400
        And the response message should be "Invalid username and/or password"