package stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import org.testng.Assert;
import resources.helpers.APIHelper;
import resources.helpers.APIResources;
import resources.helpers.AddPlaceHelper;

import static io.restassured.RestAssured.given;
import static stepDefinitions.step_definitions.placeId;

public class Hooks {

    APIHelper apiHelper = new APIHelper();
    AddPlaceHelper addPlaceHelper = new AddPlaceHelper();

    @BeforeAll
    public static void beforeAllScenarios() {
        APIHelper.initLogFiles();
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        APIHelper.writeLogSeparator(scenario.getName());
        createPlaceIfNeeded();
    }

    @After
    public void afterScenario() {
        // no-op: logs stay in shared files for the full test run
    }

    private void createPlaceIfNeeded() {
        if (placeId == null || placeId.isBlank()) {
            Response addResponse = given()
                    .spec(apiHelper.buildAddPlaceRequestSpec())
                    .body(addPlaceHelper.addPlacePayload())
                    .post(APIResources.addPlaceAPI.getResource());
            Assert.assertEquals(addResponse.getStatusCode(), 200, "Add Place API should succeed before delete");
            placeId = apiHelper.getJsonPath(addResponse, "place_id");
            Assert.assertNotNull(placeId, "place id can not be null");
        }
    }

}
