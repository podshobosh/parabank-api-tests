package com.podsho.parabank.hooks;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import com.podsho.parabank.utils.ApiLogContext;
import com.podsho.parabank.utils.Log;
import org.apache.logging.log4j.LogManager;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    @Before
    public void beforeScenario(Scenario scenario) {
        ApiLogContext.reset();
        Log.info("Starting Scenario: " + scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            String apiLog = asHtml(ApiLogContext.getLog());
            // Cucumber's own reporter renders this; portable across reporting tools.
            scenario.attach(apiLog, "text/html", "API Log");
            // The Extent adapter ignores scenario attachments, so feed it directly.
            ExtentCucumberAdapter.addTestStepLog(apiLog);
            LogManager.getLogger("failedScenarioLogger").error("FAILED " + scenario.getName());
        } else {
            Log.info("PASSED " + scenario.getName());
        }
        ApiLogContext.remove();
    }

    /* Wraps the transcript in <pre> so the reporters render it readably, and
       escapes HTML so XML/JSON payloads don't break the surrounding markup.
       Ampersand must be escaped first or the entities get double-encoded. */
    private static String asHtml(String log) {
        String escaped = log
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        return "<pre>" + escaped + "</pre>";
    }

}
