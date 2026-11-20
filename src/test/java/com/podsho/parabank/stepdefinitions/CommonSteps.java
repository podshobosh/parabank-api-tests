package com.podsho.parabank.stepdefinitions;

import org.testng.Assert;

import com.podsho.parabank.client.AccountService;
import com.podsho.parabank.models.AccountType;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class CommonSteps {

    private final ScenarioContext context;
    private final AccountService service;

    public CommonSteps(ScenarioContext context, AccountService service) {
        this.context = context;
        this.service = service;
    }

    @Given("a customer with a checking account")
    public void a_customer_with_a_checking_account() {
        String username = ConfigReader.getProperty("test.user");
        String password = ConfigReader.getProperty("test.pass");
        String customerId = service.loginAndGetCustomerId(username, password);

        Response createResponse = service.createAccount(customerId, AccountType.CHECKING,
                service.getFirstAccountId(customerId));
        context.setAccountId(createResponse.jsonPath().getString("id"));
    }

    @Then("status code should be {int}")
    public void status_code_should_be(Integer expectedStatusCode) {
        Assert.assertEquals(context.getResponse().getStatusCode(), expectedStatusCode.intValue());
    }

    @Then("the response message should be {string}")
    public void the_response_message_should_be(String expectedResponseMsg) {
        Assert.assertEquals(context.getResponse().asString().trim(), expectedResponseMsg);
    }

    @Then("the response says the account was not found")
    public void the_response_says_the_account_was_not_found() {
        Assert.assertEquals(context.getResponse().asString().trim(),
                "Could not find account number " + context.getAccountId());
    }

    @Then("the response time is under {int} milliseconds")
    public void the_response_time_is_under_milliseconds(Integer milliseconds) {
        Assert.assertTrue(context.getResponse().getTime() < milliseconds);
    }

    @Then("response is JSON")
    public void response_is_json() {
        Assert.assertTrue(context.getResponse().getContentType().startsWith("application/json"),"");
    }

}
