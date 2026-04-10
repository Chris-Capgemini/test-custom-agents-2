# Allegro PoC API Tests

Comprehensive Cucumber BDD test suite for the Allegro modernization PoC `POST /post` endpoint,
implemented with RestAssured and JUnit 5.

---

## Prerequisites

| Tool         | Version       |
|--------------|---------------|
| Java         | 17 or higher  |
| Maven        | 3.8 or higher |
| Running API  | `http://localhost:8080` |

The tests expect the Allegro PoC server to be listening on `http://localhost:8080`.
Start the server before executing the tests (see the project root `README.md`).

---

## Project Structure

```
analysis_output/api-test-generator/
├── pom.xml                                          # Maven project descriptor with all dependencies
├── README.md                                        # This file
└── src/
    └── test/
        ├── java/
        │   └── com/poc/api/
        │       ├── runner/
        │       │   └── CucumberTestRunner.java      # JUnit Platform Suite entry point
        │       ├── steps/
        │       │   └── PostStepDefinitions.java     # Cucumber step definitions (RestAssured)
        │       └── support/
        │           ├── ApiClient.java               # RestAssured API client wrapper
        │           ├── TestContext.java             # Shared scenario state holder
        │           └── TestDataBuilder.java         # Reusable test data factory
        └── resources/
            ├── cucumber.properties                  # Cucumber runtime configuration
            └── features/
                └── java_update.feature              # BDD feature file (Gherkin scenarios)
```

---

## Running the Tests

### Run all tests

```bash
cd analysis_output/api-test-generator
mvn test
```

### Run only the `java-update` tagged scenarios

```bash
mvn test -Dcucumber.filter.tags="@java-update"
```

### Run happy-path scenarios only

```bash
mvn test -Dcucumber.filter.tags="@happy-path"
```

### Run address-update scenarios only

```bash
mvn test -Dcucumber.filter.tags="@update-address"
```

### Run banking-update scenarios only

```bash
mvn test -Dcucumber.filter.tags="@update-banking"
```

### Target a different API base URL

```bash
mvn test -Dapi.base.url="http://myserver:8080"
```

> **Note**: To support the `-Dapi.base.url` system property, update the `theApiBaseUrlIs` step to
> read `System.getProperty("api.base.url", "http://localhost:8080")` or set it in `cucumber.properties`.

---

## Test Reports

After a test run, HTML and JSON reports are generated under:

```
target/cucumber-reports/
├── cucumber.html   # Human-readable HTML report
└── cucumber.json   # Machine-readable JSON report (compatible with CI integrations)
```

Open `cucumber.html` in your browser for an overview of pass/fail results.

---

## Scenario Tags

| Tag               | Description                                          |
|-------------------|------------------------------------------------------|
| `@java-update`    | All scenarios in the java-update workflow group       |
| `@happy-path`     | End-to-end submit scenarios with complete user data  |
| `@update-name`    | Scenarios that update first / last name              |
| `@update-address` | Scenarios that update street, ZIP, and city          |
| `@update-banking` | Scenarios that update IBAN, BIC, and valid-from date |
| `@validation`     | Boundary / validation scenarios                      |
| `@content-type`   | Scenarios asserting the response Content-Type header |
| `@multiple-submissions` | Scenario outline covering several user personas |
| `@ignored`        | Scenarios excluded from the default run              |

---

## Data Model

The `POST /post` endpoint accepts a JSON object with the following fields (all strings):

| Field           | Description                         | Example                    |
|-----------------|-------------------------------------|----------------------------|
| `FIRST_NAME`    | Given name                          | `Hans`                     |
| `LAST_NAME`     | Surname                             | `Müller`                   |
| `DATE_OF_BIRTH` | Date of birth (DD.MM.YYYY)          | `01.01.1980`               |
| `STREET`        | Street and house number             | `Hauptstraße 1`            |
| `ZIP`           | Postal code                         | `80331`                    |
| `ORT`           | City                                | `München`                  |
| `IBAN`          | International Bank Account Number   | `DE89370400440532013000`   |
| `BIC`           | Bank Identifier Code                | `COBADEFFXXX`              |
| `VALID_FROM`    | Account valid-from date (DD.MM.YYYY)| `01.01.2024`               |
| `MALE`          | Gender flag                         | `true` / `false`           |
| `FEMALE`        | Gender flag                         | `true` / `false`           |
| `DIVERSE`       | Gender flag                         | `true` / `false`           |
| `TEXT_AREA`     | Free-text comments or notes         | `Test submission`          |

---

## Adding New Scenarios

1. Open `src/test/resources/features/java_update.feature`.
2. Add a new `Scenario` or extend an existing `Scenario Outline`.
3. Tag the new scenario with the relevant tag (e.g. `@java-update`).
4. If new step patterns are needed, add them to `PostStepDefinitions.java`.
5. Add reusable test data to `TestDataBuilder.java`.
