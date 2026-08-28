package com.podsho.parabank.stepdefinitions;

import com.podsho.parabank.client.ApiClient;
import com.podsho.parabank.utils.ScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class AccountLookUpStepDefs {

    private final ScenarioContext context;
    private String accountId;

    public AccountLookUpStepDefs(ScenarioContext context) {
        this.context = context;
    }

    @Given("an account id is {string}")
    public void an_account_id_is(String accountId) {
        this.accountId = accountId;
    }

    @When("the account is requested")
    public void the_account_is_requested() {
        context.setResponse(ApiClient.get("/accounts/" + accountId));
    }

}
