package com.podsho.parabank.stepdefinitions;

import java.math.BigDecimal;

import org.testng.Assert;

import com.podsho.parabank.client.AccountService;
import com.podsho.parabank.models.AccountType;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class TransferStepDefs {

    private final ScenarioContext context;
    private final AccountService service;

    public TransferStepDefs(ScenarioContext context, AccountService service) {
        this.context = context;
        this.service = service;
    }

    String username = ConfigReader.getProperty("test.user");
    String password = ConfigReader.getProperty("test.pass");

    @Given("a customer with two checking accounts")
    public void a_customer_with_two_checking_accounts() {
        String customerId = service.loginAndGetCustomerId(username, password);
        String fundingAccountId = service.getFirstAccountId(customerId);

        Response first = service.createAccount(customerId, AccountType.CHECKING, fundingAccountId);
        context.setFirstAccountId(first.jsonPath().getString("id"));

        Response second = service.createAccount(customerId, AccountType.CHECKING, fundingAccountId);
        context.setSecondAccountId(second.jsonPath().getString("id"));
    }

    @And("the balance of both accounts is recorded")
    public void the_balance_of_both_accounts_is_recorded() {
        BigDecimal firstInitBalance = service.getBalance(context.getFirstAccountId());
        BigDecimal secondInitBalance = service.getBalance(context.getSecondAccountId());

        context.setFirstBalance(firstInitBalance);
        context.setSecondBalance(secondInitBalance);
    }

    @When("user transfers {bigdecimal} dollars from the first account to the second")
    public void user_transfers_dollars_from_the_first_account_to_the_second(BigDecimal amount) {
        context.setResponse(service.transfer(amount, context.getFirstAccountId(), context.getSecondAccountId()));
        context.setAmount(amount);
    }

    @Then("the transfer is confirmed")
    public void the_transfer_is_confirmed() {
        Assert.assertEquals(context.getResponse().getStatusCode(), 200);
        Assert.assertEquals(context.getResponse().asString().trim(),
            "Successfully transferred $" + context.getAmount() + " from account #" + context.getFirstAccountId()
                + " to account #" + context.getSecondAccountId());
    }

    @Then("the first account balance is decreased by {bigdecimal}")
    public void the_first_account_balance_is_decreased_by(BigDecimal amount) {
        BigDecimal expected = context.getFirstBalance().subtract(amount);
        BigDecimal actual = service.getBalance(context.getFirstAccountId());

        Assert.assertEquals(actual.compareTo(expected), 0);
    }

    @Then("the second account balance is increased by {bigdecimal}")
    public void the_second_account_balance_is_increased_by(BigDecimal amount) {
        BigDecimal expected = context.getSecondBalance().add(amount);
        BigDecimal actual = service.getBalance(context.getSecondAccountId());

        Assert.assertEquals(actual.compareTo(expected), 0);
    }

}
