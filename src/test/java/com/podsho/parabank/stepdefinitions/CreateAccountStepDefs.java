package com.podsho.parabank.stepdefinitions;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.podsho.parabank.client.ApiClient;
import com.podsho.parabank.models.Account;
import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class CreateAccountStepDefs {

    private final ScenarioContext context;
    private String customerId;
    private String fromAccountId;

    public CreateAccountStepDefs(ScenarioContext context) {
        this.context = context;
    }

    @Given("a customer logs in and has an existing funding account")
    public void a_customer_logs_in_and_has_an_existing_funding_account() {
        String username = ConfigReader.getProperty("test.user");
        String password = ConfigReader.getProperty("test.pass");

        Response loginResponse = ApiClient.get("/login/" + username + "/" + password);
        customerId = loginResponse.jsonPath().getString("id");

        Response accountResponse = ApiClient.get("/customers/" + customerId + "/accounts");
        fromAccountId = accountResponse.jsonPath().getString("[0].id");
    }

    @When("user creates a new account of type {string}")
    public void user_creates_a_new_account_of_type(String accountType) {
        int typeCode = accountType.equalsIgnoreCase("SAVINGS") ? 1 : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        params.put("newAccountType", typeCode);
        params.put("fromAccountId", fromAccountId);

        context.setResponse(ApiClient.postWithParams("/createAccount", params));
    }

    @Then("the account should be created successfully with status code {int}")
    public void the_account_should_be_created_successfully_with_status_code(Integer expectedStatusCode) {
        assertThat(context.getResponse().getStatusCode(), equalTo(expectedStatusCode));

        Account createdAccount = context.getResponse().as(Account.class);
        assertThat(createdAccount.getCustomerId(), equalTo(Integer.valueOf(customerId)));
    }
}
