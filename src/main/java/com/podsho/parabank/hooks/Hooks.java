package com.podsho.parabank.hooks;

import com.podsho.parabank.utils.Log;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    @Before
    public void beforeScenario(Scenario scenario) {
        Log.info("Starting Scenario: " + scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            Log.error("FAILED " + scenario.getName());
        } else {
            Log.info("PASSED " + scenario.getName());
        }
    }

}
