Feature: Validating place API's

  Scenario: Verify if place is added successfully using AddPlaceAPI
      Given PayLoad for Add Place API has been Created
      When User calls "AddPlaceAPI" with post http request
      Then the API calls is success with status code 200
      And "status" in response Body is "OK"
      And "scope" in response Body is "APP"