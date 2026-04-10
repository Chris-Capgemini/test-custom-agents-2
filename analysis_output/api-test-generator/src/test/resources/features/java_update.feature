Feature: Java Update - Allegro PoC POST API
  As a user of the Allegro modernization system
  I want to submit personal and banking information via the POST endpoint
  So that the data is stored and confirmed successfully

  Background:
    Given the API base URL is "http://localhost:8080"

  @java-update @happy-path
  Scenario: Successfully submit complete user data
    Given I have the following user data:
      | field         | value             |
      | FIRST_NAME    | Hans              |
      | LAST_NAME     | Müller            |
      | DATE_OF_BIRTH | 01.01.1980        |
      | STREET        | Hauptstraße 1     |
      | ZIP           | 80331             |
      | ORT           | München           |
      | IBAN          | DE89370400440532013000 |
      | BIC           | COBADEFFXXX       |
      | VALID_FROM    | 01.01.2024        |
      | MALE          | true              |
      | FEMALE        | false             |
      | DIVERSE       | false             |
      | TEXT_AREA     | Test submission   |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response body should contain the submitted data

  @java-update @happy-path
  Scenario: Successfully submit user data with female gender
    Given I have the following user data:
      | field         | value             |
      | FIRST_NAME    | Maria             |
      | LAST_NAME     | Schmidt           |
      | DATE_OF_BIRTH | 15.06.1990        |
      | STREET        | Bahnhofstraße 5   |
      | ZIP           | 10115             |
      | ORT           | Berlin            |
      | IBAN          | DE44500105175407324931 |
      | BIC           | BELADEBEXXX       |
      | VALID_FROM    | 01.03.2024        |
      | MALE          | false             |
      | FEMALE        | true              |
      | DIVERSE       | false             |
      | TEXT_AREA     | Initial setup     |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response body should contain the submitted data

  @java-update @happy-path
  Scenario: Successfully submit user data with diverse gender
    Given I have the following user data:
      | field         | value             |
      | FIRST_NAME    | Alex              |
      | LAST_NAME     | Weber             |
      | DATE_OF_BIRTH | 22.11.1995        |
      | STREET        | Schillerplatz 3   |
      | ZIP           | 70173             |
      | ORT           | Stuttgart         |
      | IBAN          | DE91100000000123456789 |
      | BIC           | MARKDEFFXXX       |
      | VALID_FROM    | 15.04.2024        |
      | MALE          | false             |
      | FEMALE        | false             |
      | DIVERSE       | true              |
      | TEXT_AREA     | User registration |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response body should contain the submitted data

  @java-update @update-name
  Scenario: Update user first and last name
    Given I have previously submitted user data for "Hans Müller"
    And I update the following fields:
      | field      | value   |
      | FIRST_NAME | Johann  |
      | LAST_NAME  | Fischer |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response field "FIRST_NAME" should equal "Johann"
    And the response field "LAST_NAME" should equal "Fischer"

  @java-update @update-address
  Scenario: Update user address information
    Given I have previously submitted user data for "Maria Schmidt"
    And I update the following fields:
      | field  | value              |
      | STREET | Lindenallee 12     |
      | ZIP    | 20095              |
      | ORT    | Hamburg            |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response field "STREET" should equal "Lindenallee 12"
    And the response field "ZIP" should equal "20095"
    And the response field "ORT" should equal "Hamburg"

  @java-update @update-banking
  Scenario: Update user banking details
    Given I have previously submitted user data for "Alex Weber"
    And I update the following fields:
      | field      | value                  |
      | IBAN       | DE02120300000000202051 |
      | BIC        | BYLADEM1001            |
      | VALID_FROM | 01.07.2024             |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response field "IBAN" should equal "DE02120300000000202051"
    And the response field "BIC" should equal "BYLADEM1001"
    And the response field "VALID_FROM" should equal "01.07.2024"

  @java-update @validation
  Scenario: Submit data with minimum required fields
    Given I have the following user data:
      | field      | value   |
      | FIRST_NAME | Klaus   |
      | LAST_NAME  | Bauer   |
    When I send a POST request to "/post"
    Then the response status code should be 200

  @java-update @validation
  Scenario: Submit data with empty request body
    Given I have an empty request body
    When I send a POST request to "/post"
    Then the response status code should be 200

  @java-update @content-type
  Scenario: Verify response content type is JSON
    Given I have the following user data:
      | field      | value  |
      | FIRST_NAME | Test   |
      | LAST_NAME  | User   |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response content type should be "application/json"

  @java-update @multiple-submissions
  Scenario Outline: Submit data for multiple users
    Given I have the following user data:
      | field         | value          |
      | FIRST_NAME    | <firstName>    |
      | LAST_NAME     | <lastName>     |
      | DATE_OF_BIRTH | <dob>          |
      | STREET        | <street>       |
      | ZIP           | <zip>          |
      | ORT           | <ort>          |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response field "FIRST_NAME" should equal "<firstName>"
    And the response field "LAST_NAME" should equal "<lastName>"

    Examples:
      | firstName | lastName  | dob        | street           | zip   | ort       |
      | Anna      | Schneider | 10.02.1985 | Rathausplatz 1   | 50667 | Köln      |
      | Peter     | Hoffmann  | 25.09.1978 | Friedrichstr. 10 | 10117 | Berlin    |
      | Sophie    | Becker    | 03.07.2000 | Maximilianstr. 8 | 80539 | München   |
