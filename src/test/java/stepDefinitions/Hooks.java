package stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import resources.helpers.APIHelper;

public class Hooks {

    @BeforeAll
    public static void beforeAllScenarios() {
        APIHelper.initLogFiles();
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        APIHelper.writeLogSeparator(scenario.getName());
    }

    @After
    public void afterScenario() {
        // no-op: logs stay in shared files for the full test run
    }
}
