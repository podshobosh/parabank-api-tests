package com.podsho.parabank.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "classpath:features",
    glue = "com.podsho.parabank",
    plugin = {"pretty",
              "html:target/cucumber-reports.html",
              "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"},
    // No hardcoded tag filter: every scenario runs by default.
    // Filter at runtime instead, e.g.
    //   mvn test -Dcucumber.filter.tags="@login"
    //   mvn test -Dcucumber.filter.tags="@login or @lookUp"
    //   mvn test -Dcucumber.filter.tags="not @createAccount"
    monochrome = true,

    dryRun = false


)

public class TestRunner extends AbstractTestNGCucumberTests{
}
