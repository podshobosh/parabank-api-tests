package com.podsho.parabank.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "classpath:features",
    glue = "com.podsho.parabank",
    plugin = {"pretty", 
              "html:target/cucumber-reports.html",
              "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"},
    monochrome = true,
    dryRun = false          


)

public class TestRunner extends AbstractTestNGCucumberTests{
}
