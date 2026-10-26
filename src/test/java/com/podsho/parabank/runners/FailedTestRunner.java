package com.podsho.parabank.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

// Re-runs only the scenarios that failed in the last TestRunner run.
// TestRunner's rerun plugin writes them to target/failed_scenarios.txt.
//
// Run without `clean`, or the file gets wiped first:
//   mvn test -Dtest=FailedTestRunner
@CucumberOptions(
    features = "@target/failed_scenarios.txt",
    glue = "com.podsho.parabank",
    plugin = {"pretty",
              "html:target/cucumber-reports-rerun.html",
              "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"},
    monochrome = true
)

public class FailedTestRunner extends AbstractTestNGCucumberTests{
}
