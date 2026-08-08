Feature: Validating place API's

    Scenario: Verify if place is added successfully using AddPlaceAPI
        Given PayLoad for Add Place API has been Created
        When the user sends a POST request to "maps/api/place/add/json"
        Then the response status code should be 200
        And "status" in response Body is "OK"
        And "scope" in response Body is "APP"

    Scenario Outline: Verify if Place is added successfully usingAddPlaceAPI with passing data from feature file
        Given PayLoad for Add Place API has been Created "<name>" "<language>" "<address>"
        When the user sends a POST request to "maps/api/place/add/json"
        Then the response status code should be 200
        And "status" in response Body is "OK"
        And "scope" in response Body is "APP"

        Examples:
            | name     | language | address            |
            | AA_house | English  | World Cross Center |