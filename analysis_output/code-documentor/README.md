# Repository Code Documentation

## Scope and validation

This documentation describes source and test files outside `.github/agents`. Application source was read-only; no code was changed and no application or tests were executed. Structured outputs are in this folder:

- `analysis_results.json` — file-by-file source, test, configuration, and contract analysis.
- `business_rules_extractor_analysis.json` — extracted implemented behavior and test expectations, separated by workflow.
- `package_summary.json` — package responsibilities and dependency edges.
- `application_summary.json` — application purpose, stack, architecture, personas, and observed gaps.

The JSON documents are written as single-line JSON objects. Validation was by inspection of source and generated artifact contents; no JSON parser, build, or test runner was available through the provided tools.

## Application overview

The repository is a proof of concept joining an older Java Swing desktop UI to a browser mock search UI. The Vue component searches embedded person/payment-recipient sample records and sends selections over WebSocket to a Node.js relay on port 1337. The relay broadcasts raw UTF-8 messages to connected clients. A separate Java Swing WebSocket client consumes these envelopes and populates fields. Another Java Swing application follows an MVP-like structure and submits all modeled fields as JSON to an HTTPBin-compatible endpoint at `localhost:8080/post`.

The current root Maven build targets Java 22 and uses Tyrus/WebSocket and JSON-P libraries. The browser client uses Vue 2. API tests live under the existing `analysis_output/api-test-generator` path and use Cucumber, JUnit 5, RestAssured, and AssertJ. `api.yml` describes a single OpenAPI 3.0.1 POST operation.

## Business workflows and rules

### Browser lookup and record handoff

The search form uses a local, hard-coded sample catalog. Surname, first name, city, street, and house number are matched case-insensitively by substring; ZIP matches exactly. Multiple supplied criteria are combined with OR, so a record matching any one supplied criterion qualifies. A selected person has associated payment-recipient records. The selected recipient is substituted into a copied person object, which is sent using a `textfield` message target. No backend lookup, validation, or empty-selection guard is implemented.

The Node relay broadcasts received UTF-8 content to every connected peer, including the sender. It does not parse, persist, validate, or authorize message contents. Vue notes changes are sent with target `textarea`; the alternate Swing client replaces its notes text with received content. The Swing WebSocket button currently has no business submission action; it only logs a click.

### Primary Swing submission

The primary Swing form binds name, birth date, address, banking details, notes, and three gender flags to model values. Its submit action converts every enumerated field to a string and posts it to the local HTTP echo service. A non-empty response body is emitted to the UI; an empty response emits `Failed operation`. The response code is printed but does not control this success/failure choice. No required field or format validation was found, and null model values can fail while building the payload. On an event, the presenter displays response text, clears most identity/address/banking controls, and resets gender to female; notes and model values are not reset.

### API test expectations

The Cucumber suite expects HTTP 200 for full, minimal, and empty JSON requests. It checks that posted data is reflected in the response, field updates are reflected, and selected responses use a JSON content type. These are test expectations against an echo-style endpoint, not evidence of durable storage or application-side validation. The OpenAPI request body is required, but individual properties are not marked required; the test suite separately tries an empty JSON object.

## Architecture and package responsibilities

| Area | Role |
|---|---|
| `swing/src/main/java/com` | Starts primary desktop MVP components. |
| `swing/src/main/java/com/poc/model` | Defines field names and state, posts JSON, and emits results. |
| `swing/src/main/java/com/poc/presentation` | Desktop form and model/UI bindings. |
| `swing/src/main/java/websocket` | Separate Swing WebSocket receiving client and JSON field mapping. |
| `node-server/src` | WebSocket listener and message broadcast. |
| `node-vue-client/src` | Vue app shell and mock lookup/transfer workflow. |
| `analysis_output/api-test-generator/src/test` | BDD runner, API steps, test state/data, and feature scenarios. |

Primary submission path: Swing controls → `PocPresenter` → `PocModel` → `HttpBinService` → `localhost:8080/post`; response → `EventEmitter` → presenter.

Record transfer path: Vue `Search` → WebSocket `localhost:1337` → Node broadcast → connected client(s), including the alternate Swing receiver.

## API and data fields

`api.yml` defines `POST /post` at `http://localhost:8080`. `PostObject` properties are strings: `FIRST_NAME`, `LAST_NAME`, `DATE_OF_BIRTH`, `STREET`, `BIC`, `ORT`, `ZIP`, `FEMALE`, `MALE`, `DIVERSE`, `IBAN`, `VALID_FROM`, and `TEXT_AREA`. The API response schema documents echoed HTTPBin-style sections and a post object, but does not define field-level format or validation constraints.

## Configuration notes

- Root `pom.xml`: Java source/target 22; WebSocket/Tyrus and JSON-P dependencies.
- `node-server/package.json`: `websocket` runtime package.
- `node-vue-client/package.json`: Vue 2.6, Vue CLI 4 scripts for serve/build/lint.
- `analysis_output/api-test-generator/pom.xml`: test compilation at Java 17 with Cucumber 7.15, JUnit 5.10, RestAssured 5.4 and related assertion/report dependencies.
- `config/modernization-config.json`: generic planning template rather than active application configuration; template targets mention Java 21+ and Spring Boot 3.x, which differ from current root Maven configuration.

## Test coverage and limitations

The feature scenarios cover three gender examples, full data echo, name/address/banking updates, minimal fields, an empty object, JSON response content type, and multiple names. The reviewed tests do not cover malformed payloads, invalid field formats, WebSocket filtering or authorization, persistence, or null values in the primary Swing model. No tests were run as part of this documentation.
