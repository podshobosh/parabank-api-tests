package com.podsho.parabank.stepdefinitions;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

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

    public DepositStepDefs(ScenarioContext context) {
        this.context = context;
    }

    @Given("a customer with a checking account")
    public void a_customer_with_a_checking_account() {
        String username = ConfigReader.getProperty("test.user");
        String password = ConfigReader.getProperty("test.pass");

        Response loginResponse = ApiClient.get("/login/" + username + "/" + password);
        Assert.assertEquals(loginResponse.getStatusCode(), 200, "login failed");
        String customerId = loginResponse.jsonPath().getString("id");

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
        // GET /accounts/{id}
        Response getAccountInfo = ApiClient.get("/accounts/" + context.getAccountId());

        // read balance with JsonPath
        double balance = getAccountInfo.jsonPath().getDouble("balance");

        // context.setBalance(...)
        context.setBalance(balance);
        // System.out.println("initial balance = " + context.getBalance());
    }

    @When("the customer deposits {double} into their account")
    public void the_customer_deposits_into_their_account(Double amount) {

        Map<String, Object> params = new HashMap<>();
        params.put("accountId", context.getAccountId());
        params.put("amount", amount);

        context.setAmount(amount);
        context.setResponse(ApiClient.postWithParams("/deposit", params));
    }

    @Then("the deposit is confirmed")
    public void the_deposit_is_confirmed() {
        Assert.assertEquals(context.getResponse().getStatusCode(), 200);
        Assert.assertEquals(context.getResponse().asString().trim(),
                "Successfully deposited $" + context.getAmount() + " to account #" + context.getAccountId());
    }

    @Then("the account balance has increased by {double}")
    public void the_account_balance_has_increased_by(Double amount) {
        Response getAccountInfo = ApiClient.get("/accounts/" + context.getAccountId());
        double newBalance = getAccountInfo.jsonPath().getDouble("balance");
        Assert.assertEquals(newBalance, context.getBalance() + amount, 0.001);
    }

}
