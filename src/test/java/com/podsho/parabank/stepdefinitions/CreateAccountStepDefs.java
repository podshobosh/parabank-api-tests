package com.podsho.parabank.stepdefinitions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.podsho.parabank.client.AccountService;
import com.podsho.parabank.models.Account;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class CreateAccountStepDefs {

    private final ScenarioContext context;
    private final AccountService service;
    private String customerId;
    private String fromAccountId;

    public CreateAccountStepDefs(ScenarioContext context, AccountService service) {
        this.context = context;
        this.service = service;
    }

    @Given("a customer logs in and has an existing funding account")
    public void a_customer_logs_in_and_has_an_existing_funding_account() {
        String username = ConfigReader.getProperty("test.user");
        String password = ConfigReader.getProperty("test.pass");

        customerId = service.login(username, password);
        fromAccountId = service.getFirstAccountId(customerId);
    }

    @When("user creates a new account of type {string}")
    public void user_creates_a_new_account_of_type(String accountType) {
        context.setResponse(service.createAccount(customerId, accountType, fromAccountId));
    }

    @Then("the account should be created successfully with status code {int}")
    public void the_account_should_be_created_successfully_with_status_code(Integer expectedStatusCode) {
        assertThat(context.getResponse().getStatusCode(), equalTo(expectedStatusCode));

        Account createdAccount = context.getResponse().as(Account.class);
        assertThat(createdAccount.getCustomerId(), equalTo(Integer.valueOf(customerId)));
    }
}
