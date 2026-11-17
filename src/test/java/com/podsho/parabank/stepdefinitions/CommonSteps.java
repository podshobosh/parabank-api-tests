package com.podsho.parabank.stepdefinitions;

import org.testng.Assert;

import com.podsho.parabank.utils.ScenarioContext;

import io.cucumber.java.en.Then;

public class CommonSteps {

    private final ScenarioContext context;

    public CommonSteps(ScenarioContext context) {
        this.context = context;
    }

    @Then("status code should be{int}")
    public void status_code_should_be(Integer expectedStatusCode) {
        Assert.assertEquals(context.getResponse().getStatusCode(), expectedStatusCode.intValue());
    }

    @Then("the response message should be {string}")
    public void the_response_message_should_be(String expectedResponseMsg) {
        Assert.assertEquals(context.getResponse().asString().trim(), expectedResponseMsg);
    }

}
