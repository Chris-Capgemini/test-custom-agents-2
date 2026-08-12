Feature: OS Update - Security Patch Submission via POST API
  As a system administrator using the Allegro modernization system
  I want to submit OS update security patch records via the POST endpoint
  So that security patch information is recorded and confirmed successfully

  Background:
    Given the API base URL is "http://localhost:8080"

  @os-update-security-patch @happy-path
  Scenario: Successfully submit a security patch record for a system administrator
    Given I have the following user data:
      | field         | value                          |
      | FIRST_NAME    | Thomas                         |
      | LAST_NAME     | Richter                        |
      | DATE_OF_BIRTH | 14.03.1975                     |
      | STREET        | Sicherheitsweg 7               |
      | ZIP           | 60329                          |
      | ORT           | Frankfurt                      |
      | IBAN          | DE12500105170648489890         |
      | BIC           | BELADEBEXXX                    |
      | VALID_FROM    | 01.08.2024                     |
      | MALE          | true                           |
      | FEMALE        | false                          |
      | DIVERSE       | false                          |
      | TEXT_AREA     | OS security patch CVE-2024-001 |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response body should contain the submitted data

  @os-update-security-patch @happy-path
  Scenario: Successfully submit a security patch record with patch notes in TEXT_AREA
    Given I have the following user data:
      | field         | value                              |
      | FIRST_NAME    | Sabine                             |
      | LAST_NAME     | Neumann                            |
      | DATE_OF_BIRTH | 29.07.1983                         |
      | STREET        | Patchstraße 15                     |
      | ZIP           | 01069                              |
      | ORT           | Dresden                            |
      | IBAN          | DE75512108001245126199             |
      | BIC           | SSKMDEMM                           |
      | VALID_FROM    | 15.09.2024                         |
      | MALE          | false                              |
      | FEMALE        | true                               |
      | DIVERSE       | false                              |
      | TEXT_AREA     | Critical patch: kernel update 5.15 |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response body should contain the submitted data

  @os-update-security-patch @update-patch-notes
  Scenario: Update security patch notes for an existing record
    Given I have previously submitted user data for "Thomas Richter"
    And I update the following fields:
      | field     | value                                  |
      | TEXT_AREA | OS security patch CVE-2024-001 applied |
      | VALID_FROM| 10.08.2024                             |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response field "TEXT_AREA" should equal "OS security patch CVE-2024-001 applied"
    And the response field "VALID_FROM" should equal "10.08.2024"

  @os-update-security-patch @validation
  Scenario: Submit security patch with only required identification fields
    Given I have the following user data:
      | field      | value   |
      | FIRST_NAME | Klaus   |
      | LAST_NAME  | Wagner  |
      | TEXT_AREA  | Minimal patch record |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response field "FIRST_NAME" should equal "Klaus"
    And the response field "LAST_NAME" should equal "Wagner"

  @os-update-security-patch @content-type
  Scenario: Verify response content type is JSON for security patch submission
    Given I have the following user data:
      | field     | value                 |
      | FIRST_NAME| PatchAdmin            |
      | LAST_NAME | System                |
      | TEXT_AREA | Security patch record |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response content type should be "application/json"

  @os-update-security-patch @multiple-patches
  Scenario Outline: Submit security patch records for multiple administrators
    Given I have the following user data:
      | field      | value         |
      | FIRST_NAME | <firstName>   |
      | LAST_NAME  | <lastName>    |
      | TEXT_AREA  | <patchNote>   |
      | VALID_FROM | <validFrom>   |
    When I send a POST request to "/post"
    Then the response status code should be 200
    And the response field "FIRST_NAME" should equal "<firstName>"
    And the response field "TEXT_AREA" should equal "<patchNote>"

    Examples:
      | firstName | lastName   | patchNote                    | validFrom  |
      | Erik      | Braun      | Patch CVE-2024-101 applied   | 01.10.2024 |
      | Laura     | Köhler     | Patch CVE-2024-202 applied   | 15.10.2024 |
      | Michael   | Lange      | Patch CVE-2024-303 applied   | 01.11.2024 |
