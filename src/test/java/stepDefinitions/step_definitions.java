package stepDefinitions;

import resources.helpers.APIHelper;
import resources.helpers.APIResources;
import resources.helpers.AddPlaceHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import static io.restassured.RestAssured.given;

public class step_definitions extends APIHelper {

    RequestSpecification res;
    Response response;
    AddPlaceHelper addPlaceHelper = new AddPlaceHelper();
    String placeId;

    @Given("^PayLoad for Add Place API has been Created$")
    public void GivenPayLoadForAddPlace() {
        res = given()
                .spec(buildAddPlaceRequestSpec())
                .body(addPlaceHelper.addPlacePayload());
    }

    @Given("^PayLoad for Add Place API has been Created \"(.+)\" \"(.+)\" \"(.+)\"$")
    public void GivenPayLoadForAddPlaceWithParameters(String name, String language, String address) {
        res = given()
                .spec(buildAddPlaceRequestSpec())
                .body(addPlaceHelper.addPlacePayload(name, language, address));
    }

    @When("^the user sends a \"(.+)\" request to \"(.+)\"$")
    public void GivenUserSendsHttpRequest(String httpRequestType, String resource) {
        APIResources resourceName = APIResources.valueOf(resource);
        if (httpRequestType.equalsIgnoreCase("POST")) {
            response = res.post(resourceName.getResource());
        } else if (httpRequestType.equalsIgnoreCase("GET")) {
            response = res.get(resourceName.getResource());
        } else {
            response = res.delete(resourceName.getResource());
        }
    }

    @Then("^the response status code should be (\\d+)$")
    public void ThenVerifyApiCallIsSuccessful(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode,
                "status code should match");
    }

    @Then("^\"(.+)\" in response Body is \"(.+)\"$")
    public void ThenResponseBody(String key, String value) {
        JsonPath js = response.jsonPath();
        String firstValue = js.getString(key);
        placeId = js.getString("place_id");
        Assert.assertEquals(firstValue, value, "values are not matching");
        Assert.assertNotNull(placeId, "place id can not be null");
    }

    @Then("^the user verifies place_id created maps to \"(.+)\" using \"(.+)\"$")
    public void ThenPlaceIDCreated(String name, String resource) {
        res = given()
                .spec(buildGetPlaceRequest(placeId));

        GivenUserSendsHttpRequest("GET", resource);
        Assert.assertEquals(response.getStatusCode(), 200);
        JsonPath js = response.jsonPath();
        String actualName = js.getString("name");
        Assert.assertEquals(actualName, name, "name Should match with expected");
    }
}
