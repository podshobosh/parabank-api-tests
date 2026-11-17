package com.podsho.parabank.stepdefinitions;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

import com.podsho.parabank.client.AccountService;
import com.podsho.parabank.client.ApiClient;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class DepositStepDefs {

    private static final int CHECKING = 0;

    private final ScenarioContext context;
    private final AccountService service;

    public DepositStepDefs(ScenarioContext context, AccountService service) {
        this.context = context;
        this.service = service;
    }

    @Given("a customer with a checking account")
    public void a_customer_with_a_checking_account() {
        String username = ConfigReader.getProperty("test.user");
        String password = ConfigReader.getProperty("test.pass");

        String customerId = service.login(username, password);
        
        // an existing account funds the new one (ParaBank requires a source account)
        Response accountsResponse = ApiClient.get("/customers/" + customerId + "/accounts");
        Assert.assertEquals(accountsResponse.getStatusCode(), 200, "could not list accounts");
        String fundingAccountId = accountsResponse.jsonPath().getString("[0].id");

        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        params.put("newAccountType", CHECKING);
        params.put("fromAccountId", fundingAccountId);

        Response createResponse = ApiClient.postWithParams("/createAccount", params);
        Assert.assertEquals(createResponse.getStatusCode(), 200, "account creation failed");

        context.setAccountId(createResponse.jsonPath().getString("id"));
    }

    @Given("the account balance is recorded")
    public void the_account_balance_is_recorded() {

        double balance = service.getBalance(context.getAccountId());
        context.setBalance(balance);
    }

    @When("the customer deposits {double} into their account")
    public void the_customer_deposits_into_their_account(Double amount) {
        context.setAmount(amount);
        context.setResponse(service.deposit(context.getAccountId(), amount));
    }

    @Then("the deposit is confirmed")
    public void the_deposit_is_confirmed() {
        Assert.assertEquals(context.getResponse().getStatusCode(), 200);
        Assert.assertEquals(context.getResponse().asString().trim(),
                "Successfully deposited $" + context.getAmount() + " to account #" + context.getAccountId());
    }

    @Then("the account balance has increased by {double}")
    public void the_account_balance_has_increased_by(Double amount) {
        Assert.assertEquals(service.getBalance(context.getAccountId()), context.getBalance() + amount, 0.001);
    }

    @Then("the deposit is rejected")
    public void the_deposit_is_rejected() {
        int status = context.getResponse().getStatusCode();
        Assert.assertTrue(status >= 400 && status < 500,
                "expected a 4xx rejection but got " + status + ": " + context.getResponse().asString());
    }

    @Then("the account balance is unchanged")
    public void the_account_balance_is_unchanged() {
        Assert.assertEquals(service.getBalance(context.getAccountId()), context.getBalance(), 0.001);
    }

}
