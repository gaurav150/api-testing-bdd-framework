package stepDefinitions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.AfterAll;
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

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void beforeAllScenarios() {
        APIHelper.initLogFiles();
        // start WireMock server with the custom transformer
        wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .dynamicPort()
                .extensions(new wiremock.PlaceResponseTransformer()));
        wireMockServer.start();

        // register stubs to use the transformer for add/get/delete
        WireMock.configureFor("localhost", wireMockServer.port());
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/" + APIResources.addPlaceAPI.getResource()))
            .willReturn(WireMock.aResponse().withTransformers("place-transformer")));
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/" + APIResources.getPlaceAPI.getResource()))
            .willReturn(WireMock.aResponse().withTransformers("place-transformer")));
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/" + APIResources.deletePlaceAPI.getResource()))
            .willReturn(WireMock.aResponse().withTransformers("place-transformer")));

        // set baseUrl system property so APIHelper will pick it up
        String base = "http://localhost:" + wireMockServer.port();
        System.setProperty("baseUrl", base);
    }

    @AfterAll
    public static void afterAllScenarios() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
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
