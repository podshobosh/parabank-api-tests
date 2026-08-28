package com.podsho.parabank.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;


import com.podsho.parabank.utils.ConfigReader;

import org.testng.Assert;

import com.podsho.parabank.client.ApiClient;


public class LoginSteps {
   private Response response;
   private String username, password;
   


    @Given("a user has an existing account with valid credentials")
    public void a_user_has_an_existing_account_with_valid_credentials() {
        username = ConfigReader.getProperty("test.user");
        password = ConfigReader.getProperty("test.pass");

    }

    @When("user calls a login request using their username and password")
    public void user_calls_a_login_request_using_their_username_and_password() {
        response = ApiClient.get("/services/bank/login/" + username + "/" + password);
    }

    @Then("status code should be {int}")
    public void status_code_should_be(Integer int1) {
        Assert.assertEquals(response.getStatusCode(), int1.intValue());
    }
}
