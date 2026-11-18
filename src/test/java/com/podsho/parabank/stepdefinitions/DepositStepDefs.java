package com.podsho.parabank.stepdefinitions;

import java.math.BigDecimal;

import org.testng.Assert;
import com.podsho.parabank.client.AccountService;
import com.podsho.parabank.models.AccountType;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class DepositStepDefs {

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

        Response createResponse = service.createAccount(customerId, AccountType.CHECKING,
                service.getFirstAccountId(customerId));
        context.setAccountId(createResponse.jsonPath().getString("id"));
    }

    @Given("the account balance is recorded")
    public void the_account_balance_is_recorded() {

        BigDecimal balance = service.getBalance(context.getAccountId());
        context.setBalance(balance);
    }

    @When("the customer deposits {bigdecimal} into their account")
    public void the_customer_deposits_into_their_account(BigDecimal amount) {
        context.setAmount(amount);
        context.setResponse(service.deposit(context.getAccountId(), amount));
    }

    @Then("the deposit is confirmed")
    public void the_deposit_is_confirmed() {
        Assert.assertEquals(context.getResponse().getStatusCode(), 200);
        Assert.assertEquals(context.getResponse().asString().trim(),
                "Successfully deposited $" + context.getAmount() + " to account #" + context.getAccountId());
    }

    @Then("the account balance has increased by {bigdecimal}")
    public void the_account_balance_has_increased_by(BigDecimal amount) {
        Assert.assertEquals(service.getBalance(context.getAccountId()).compareTo(context.getBalance().add(amount)), 0);
    }

    @Then("the deposit is rejected")
    public void the_deposit_is_rejected() {
        int status = context.getResponse().getStatusCode();
        Assert.assertTrue(status >= 400 && status < 500,
                "expected a 4xx rejection but got " + status + ": " + context.getResponse().asString());
    }

    @Then("the account balance is unchanged")
    public void the_account_balance_is_unchanged() {
        Assert.assertEquals(service.getBalance(context.getAccountId()).compareTo(context.getBalance()), 0);

    }

    @When("the customer deposits {bigdecimal} into account {string}")
    public void the_customer_deposits_into_account(BigDecimal amount, String accountId) {
       context.setResponse(service.deposit(accountId, amount));
    }
}
