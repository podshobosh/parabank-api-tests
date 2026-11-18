package com.podsho.parabank.stepdefinitions;

import com.podsho.parabank.client.ApiClient;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AccountLookUpStepDefs {

    private final ScenarioContext context;

    public AccountLookUpStepDefs(ScenarioContext context) {
        this.context = context;
    }

    @Given("an account id is {string}")
    public void an_account_id_is(String accountId) {
        context.setAccountId(accountId);
    }

    @When("the account is requested")
    public void the_account_is_requested() {
        context.setResponse(ApiClient.get("/accounts/" + context.getAccountId()));
    }

    @Then("the response matches the account schema")
    public void the_response_matches_the_account_schema() {
        context.getResponse().then().assertThat().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/account.json"));
    }

    @Given("non-existent account")
    public void non_existent_account() {
        context.setAccountId(ConfigReader.getProperty("test.nonexistent.account.id"));
    }
}
