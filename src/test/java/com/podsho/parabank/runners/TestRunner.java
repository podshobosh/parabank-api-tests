package com.podsho.parabank.runners;

import org.testng.annotations.DataProvider;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "classpath:features",
    glue = "com.podsho.parabank",
    plugin = {"pretty",
              "html:target/cucumber-reports.html",
              // Machine-readable results for CI dashboards and test-management tools
              "json:target/cucumber.json",
              "junit:target/cucumber.xml",
              "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
              // Writes failed scenarios (file:line) for FailedTestRunner to pick up
              "rerun:target/failed_scenarios.txt"},
    // No hardcoded tag filter: every scenario runs by default.
    // Filter at runtime instead, e.g.
    //   mvn test -Dcucumber.filter.tags="@smoke"
    //   mvn test -Dcucumber.filter.tags="@login or @lookUp"
    //   mvn test -Dcucumber.filter.tags="not @createAccount"
    //   mvn test -Dtest=TestRunner -Dcucumber.execution.dry-run=true

    monochrome = true

)

public class TestRunner extends AbstractTestNGCucumberTests{

    @Override 
    @DataProvider (parallel = true)
    public Object [][] scenarios() {
        return super.scenarios();
    }
}
