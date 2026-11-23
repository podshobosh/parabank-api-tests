package com.podsho.parabank.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import com.podsho.parabank.client.AccountService;
import com.podsho.parabank.testdata.LoginCase;
import com.podsho.parabank.testdata.TestData;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;
import com.podsho.parabank.utils.TestDataHelper;



public class LoginSteps {

    private final ScenarioContext context;
    private final AccountService service;
    private String username, password;

    public LoginSteps(ScenarioContext context, AccountService service) {
        this.context = context;
        this.service = service;
    }

    @Given("a user has an existing account with valid credentials")
    public void a_user_has_an_existing_account_with_valid_credentials() {
        username = ConfigReader.getProperty("test.user");
        password = ConfigReader.getProperty("test.pass");
    }

    @Given("a user has invalid credentials for {string}")
    public void a_user_has_invalid_credentials_for(String caseName) {
        LoginCase login = TestData.invalidLogin(caseName);
        username = login.getUsername();
        password = login.getPassword();
    }

    @Given("a user has invalid credentials for")
    public void a_user_has_invalid_credentials() {
        username = TestDataHelper.randomUsername();
        password = TestDataHelper.randomPassword();
    }
    


    @When("user calls a login request using their username and password")
    public void user_calls_a_login_request_using_their_username_and_password() {
        context.setResponse(service.loginRaw(username, password));
    }
}
