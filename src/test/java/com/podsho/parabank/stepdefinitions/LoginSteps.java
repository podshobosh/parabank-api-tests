package com.podsho.parabank.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import com.podsho.parabank.client.ApiClient;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;
import com.podsho.parabank.utils.TestDataHelper;

import org.testng.Assert;

public class LoginSteps {

    private final ScenarioContext context;
    private String username, password;

    public LoginSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("a user has an existing account with valid credentials")
    public void a_user_has_an_existing_account_with_valid_credentials() {
        username = ConfigReader.getProperty("test.user");
        password = ConfigReader.getProperty("test.pass");
    }

    @Given("a user has invalid credentials")
    public void a_user_has_invalid_credentials() {
        username = TestDataHelper.randomUsername();
        password = TestDataHelper.randomPassword();
    }

    @When("user calls a login request using their username and password")
    public void user_calls_a_login_request_using_their_username_and_password() {
        context.setResponse(ApiClient.get("/login/" + username + "/" + password));
    }

    @Then("status code should be {int}")
    public void status_code_should_be(Integer expectedStatusCode) {
        Assert.assertEquals(context.getResponse().getStatusCode(), expectedStatusCode.intValue());
    }

    @Then("the response message should be {string}")
    public void the_response_message_should_be(String expectedResponseMsg) {
        Assert.assertEquals(context.getResponse().asString().trim(), expectedResponseMsg);
    }
}
