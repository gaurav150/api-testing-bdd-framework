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

public class step_definitions extends APIHelper{

    RequestSpecification res;
    Response response;
    AddPlaceHelper addPlaceHelper = new AddPlaceHelper();

    @Given("PayLoad for Add Place API has been Created")
    public void GivenPayLoadForAddPlace() {
        res = given()
                .spec(buildAddPlaceRequestSpec())
                .body(addPlaceHelper.addPlacePayload());
    }

    @When("^User calls \"(.+)\" with post http request$")
    public void GivenUserCallsPostHttpRequest(String httpCallType) {
        if (httpCallType.equalsIgnoreCase("AddPlaceAPI")) {
            response = res.post("maps/api/place/add/json");
        }
    }

    @Then("^the API calls is success with status code ([\\d]+)$")
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
