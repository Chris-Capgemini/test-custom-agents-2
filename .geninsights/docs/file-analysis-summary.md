# Repository Code Analysis Summary

## Overview

The maintained application code is a small Java Swing/WebSocket proof of concept with a separate Node.js WebSocket relay and Vue 2 browser mock. The domain visible in the source is an Allegro-oriented person/address/payment form and data handoff. The source does **not** support describing this as an HR application.

There are two Java desktop prototypes:

- `com.Main` wires the `com.poc` Swing view, presenter, model, and event emitter.
- `websocket.Main` builds a different Swing form and a Java WebSocket client.

The Vue `Search` component contains hard-coded sample records and performs local matching; it transfers selected data and text through a WebSocket at `localhost:1337`. The Node server broadcasts UTF-8 messages to all connected clients. The Java PoC model separately posts form data to an HTTPBin-compatible endpoint at `localhost:8080`.

The repository therefore contains prototype pieces and overlapping paths, not one clearly integrated production application. `api.yml` describes a POST `/post` JSON contract on port 8080, while the Node server implements a WebSocket relay on port 1337; the contract is not evidence that the Node service implements that HTTP API.

An additional Cucumber/RestAssured API test project is present under `analysis_output/api-test-generator`. Its feature scenarios and support code were included because they are actual test sources, despite the output-oriented directory name. They express expectations for a local POST endpoint; the test suite is not itself endpoint implementation.

## Files by category

**Business (3)**

- `swing/src/main/java/com/poc/model/PocModel.java` — models and submits PoC form state.
- `node-vue-client/src/components/Search.vue` — mock person search, payment selection, and data transfer behavior.
- `analysis_output/api-test-generator/src/test/resources/features/java_update.feature` — BDD scenarios expressing POST API expectations.

**Mixed (4)**

- `swing/src/main/java/com/poc/model/HttpBinService.java` — HTTP integration for form submission.
- `swing/src/main/java/com/poc/presentation/PocPresenter.java` — connects UI controls, model state, and submission outcomes.
- `swing/src/main/java/websocket/Main.java` — Swing interface, WebSocket client, JSON parsing, and message routing.
- `analysis_output/api-test-generator/src/test/java/com/poc/api/steps/PostStepDefinitions.java` — Cucumber orchestration and API response assertions.

**Technical (21)**

- `swing/src/main/java/com/Main.java` — launcher/composition root for the `com.poc` prototype.
- `swing/src/main/java/com/poc/ValueModel.java` — generic mutable value holder.
- `swing/src/main/java/com/poc/model/EventEmitter.java` — in-process event dispatch.
- `swing/src/main/java/com/poc/model/EventListener.java` — event callback contract.
- `swing/src/main/java/com/poc/model/ModelProperties.java` — model field identifiers.
- `swing/src/main/java/com/poc/model/ViewData.java` — empty placeholder.
- `swing/src/main/java/com/poc/presentation/PocView.java` — Swing form construction.
- `node-server/src/WebsocketServer.js` — WebSocket broadcast relay.
- `node-vue-client/src/App.vue` — client shell and header.
- `node-vue-client/src/main.js` — Vue bootstrap.
- `pom.xml` — Java dependencies and compiler settings.
- `api.yml` — OpenAPI contract artifact.
- `node-server/package.json` — Node WebSocket dependency declaration.
- `node-vue-client/package.json` — Vue CLI dependencies and scripts.
- `node-vue-client/babel.config.js` — Vue CLI Babel preset.
- `analysis_output/api-test-generator/src/test/java/com/poc/api/runner/CucumberTestRunner.java` — Cucumber/JUnit suite configuration.
- `analysis_output/api-test-generator/src/test/java/com/poc/api/support/ApiClient.java` — RestAssured request wrapper.
- `analysis_output/api-test-generator/src/test/java/com/poc/api/support/TestContext.java` — per-scenario state.
- `analysis_output/api-test-generator/src/test/java/com/poc/api/support/TestDataBuilder.java` — sample test fixtures.
- `analysis_output/api-test-generator/src/test/resources/cucumber.properties` — Cucumber runtime settings.
- `analysis_output/api-test-generator/pom.xml` — isolated API-test build and dependencies.

## Key capabilities and observed behavior

- **Person search mock:** Search runs against data embedded in the Vue component rather than a service. Populated criteria are combined with OR semantics; postal code matches exactly, while several text criteria use case-insensitive substring checks.
- **Data handoff:** Vue emits `target`/`content` JSON over WebSocket. The server relays text to all connected clients; the Java WebSocket UI interprets textarea and form targets.
- **Swing form submission PoC:** A separate model/presenter flow binds form fields, serializes the model to a string map, and posts to a local HTTPBin endpoint. Null model values can fail during `.toString()` conversion before the request.
- **API test suite:** Cucumber scenarios expect successful POST and echo-like response behavior, including for partial and empty payloads. The scenario labeled as prior submission only initializes fixture state; neither the feature nor its step code demonstrates server-side persistence.
- **Validation and resilience:** Business-field validation is generally absent. The Vue client only handles WebSocket open, and the relay does not validate payload contents or define send-error recovery. Java integration paths have limited error handling.
- **Prototype boundaries:** The two Java entry points represent distinct implementations. The Eclipse launch file points to `websocket.Main`; `com.Main` launches the `com.poc` design. This should be resolved before treating either as the single application entry point.

## Technology stack

- **Java:** Java 22 compiler target in the root Maven descriptor; Swing/AWT UI; Java WebSocket API with Tyrus; JSON-P streaming/generator APIs.
- **Node.js:** `websocket` npm package and built-in `http` module.
- **Frontend:** Vue 2 single-file components, Vue CLI 4, Babel, ESLint, and browser WebSocket API.
- **API testing:** Cucumber, JUnit 5, RestAssured, and AssertJ in a separate Java 17 Maven project.
- **Interface artifact:** OpenAPI 3.0.1 YAML contract.
- **Local endpoints referenced:** WebSocket relay at port 1337 and HTTPBin-compatible POST endpoint at port 8080.

## Scope and discovery notes

Analyzed 20 Java/JavaScript/Vue source files, one Gherkin feature, and 7 directly relevant build/API/test configuration files (28 files total). Excluded generated/build and dependency content (including `target/`, lockfiles, and dependency directories), images, and workspace bootstrap/scaffolding from per-file analysis. The `.github/skills/` directory contains Swing migration skills, but no GenInsights `discover-files`, logging, or JSON-schema skills; repository discovery and output formatting followed the documented agent process and schema.
