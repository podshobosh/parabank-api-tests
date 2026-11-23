package com.podsho.parabank.stepdefinitions;


import com.podsho.parabank.client.AccountService;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class SecurityStepDefs {

    private final ScenarioContext context;
    private final AccountService service;

    private String user = ConfigReader.getProperty("test.user");
    private String pass = ConfigReader.getProperty("test.pass");

    public SecurityStepDefs(ScenarioContext context, AccountService service) {
        this.context = context;
        this.service = service;
    }

    @Given("a customer is logged in")
    public void a_customer_is_logged_in() {
        context.setCustomerId(service.loginAndGetCustomerId(user, pass));
    }

    @When("they request an account that belongs to another customer")
    public void they_request_an_account_that_belongs_to_another_customer() {
        context.setResponse(service.getAccount(ConfigReader.getProperty("test.other.customer.account.id")));
    }
}
