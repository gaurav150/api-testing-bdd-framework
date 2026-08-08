package stepDefinitions;

import resources.helpers.APIHelper;
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

    @When("^the user sends a POST request to \"(.+)\"$")
    public void GivenUserSendsPostRequest(String postEndpoint) {
        response = res.post(postEndpoint);
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
        Assert.assertEquals(firstValue, value, "values are not matching");
    }

}
