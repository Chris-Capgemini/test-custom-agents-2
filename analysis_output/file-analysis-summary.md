# File Analysis Summary — Allegro PoC (websocket_swing)

> **Generated:** 2025-01-01  
> **Repository:** test-custom-agents-2 / websocket_swing  
> **Total files analysed:** 11 Java source files  
> **Languages detected:** Java 22, JavaScript (Node.js), YAML (OpenAPI 3.0)

---

## Table of Contents

1. [Repository Overview](#repository-overview)
2. [Module Structure](#module-structure)
3. [File-by-File Analysis](#file-by-file-analysis)
   - [com.Main](#commain)
   - [com.poc.ValueModel](#compovalue-model)
   - [com.poc.model.EventEmitter](#compomodeleventemitter)
   - [com.poc.model.EventListener](#compomodeleventlistener)
   - [com.poc.model.HttpBinService](#compomodekhttpbinservice)
   - [com.poc.model.ModelProperties](#compomodelmodelproperties)
   - [com.poc.model.PocModel](#compomocelpocmodel)
   - [com.poc.model.ViewData](#compomomelviewdata)
   - [com.poc.presentation.PocView](#compopresentationpocview)
   - [com.poc.presentation.PocPresenter](#compopresentationpocpresenter)
   - [websocket.Main](#websocketmain)
4. [Dependency Overview](#dependency-overview)
5. [Summary Statistics](#summary-statistics)

---

## Repository Overview

The **Allegro PoC** is a Java 22 Swing desktop application demonstrating two integration approaches for a customer data entry form:

1. **MVP Module** (`com/` package) — A clean Model-View-Presenter implementation where user data is collected in a form, submitted via HTTP POST to a local HTTPBin echo server, and the JSON response is displayed back in the form.

2. **WebSocket Client** (`websocket/` package) — A standalone Swing application that connects to a Node.js WebSocket server and renders incoming server-pushed JSON data into the same form layout.

Both applications share the same visual design: an "Allegro" form window with German-locale labels collecting personal information, address data, and banking details.

---

## Module Structure

```
websocket_swing/
├── pom.xml                          Maven build (Java 22, Tyrus 1.15, javax.json)
├── api.yml                          OpenAPI 3.0 spec for the HTTPBin POST endpoint
├── swing/src/main/java/
│   ├── com/                         MVP Application module
│   │   ├── Main.java                Entry point — wires MVP triad
│   │   └── poc/
│   │       ├── ValueModel.java      Generic value wrapper
│   │       ├── model/
│   │       │   ├── EventEmitter.java       Pub-sub event bus
│   │       │   ├── EventListener.java      Observer interface
│   │       │   ├── HttpBinService.java     HTTP POST service
│   │       │   ├── ModelProperties.java    Enum of form field keys
│   │       │   ├── PocModel.java           Central data model
│   │       │   └── ViewData.java           Stub/placeholder DTO
│   │       └── presentation/
│   │           ├── PocPresenter.java       MVP Presenter
│   │           └── PocView.java            Swing View
│   └── websocket/                   WebSocket Client module
│       └── Main.java                Standalone WebSocket Swing client
└── node-server/src/
    └── WebsocketServer.js           Node.js WebSocket broadcast server
```

---

## File-by-File Analysis

### com.Main

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/Main.java` |
| **Category** | Technical — Entry Point |
| **Lines** | 23 |
| **Language** | Java 22 |

**Purpose:** Bootstrap class that constructs and wires all components of the MVP triad, then parks the main thread with `CountDownLatch(1)` to keep the application alive while the Swing EDT handles events.

**Key detail:** Uses the Java 22 unnamed variable pattern `var _ = new PocPresenter(...)` to silence the unused-variable compiler warning.

---

### com.poc.ValueModel

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/poc/ValueModel.java` |
| **Category** | Technical — Data Structure |
| **Lines** | 18 |
| **Language** | Java |

**Purpose:** A generic `ValueModel<T>` box that holds a single typed value. Used as the data cell for each `ModelProperties` field in `PocModel`. Provides `getField()` / `setField(T)` accessors.

---

### com.poc.model.EventEmitter

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/poc/model/EventEmitter.java` |
| **Category** | Technical — Infrastructure |
| **Lines** | 21 |

**Purpose:** Lightweight synchronous pub-sub bus. Maintains an `ArrayList<EventListener>` and broadcasts a `String` event payload to each subscriber when `emit()` is called.

> ⚠️ **Note:** Not thread-safe — concurrent `subscribe()` and `emit()` could cause `ConcurrentModificationException`.

---

### com.poc.model.EventListener

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/poc/model/EventListener.java` |
| **Category** | Technical — Interface |
| **Lines** | 5 |

**Purpose:** Functional interface with a single `onEvent(String)` method. Allows lambda implementation (as done in `PocPresenter`).

---

### com.poc.model.HttpBinService

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/poc/model/HttpBinService.java` |
| **Category** | Technical — Service |
| **Lines** | 38 |

**Purpose:** HTTP client that POSTs a `Map<String,String>` as a JSON object to `http://localhost:8080/post`. Uses `javax.json` streaming API for serialisation and `java.net.HttpURLConnection` for transport.

| Constant | Value |
|----------|-------|
| `URL` | `http://localhost:8080` |
| `PATH` | `/post` |
| `CONTENT_TYPE` | `application/json` |

> ⚠️ **Issues:** URL is hardcoded; `HttpURLConnection` is a legacy API; no try-with-resources for connection cleanup; HTTP call blocks the calling thread.

---

### com.poc.model.ModelProperties

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/poc/model/ModelProperties.java` |
| **Category** | Business — Domain Enum |
| **Lines** | 18 |

**Purpose:** Defines the authoritative set of form field keys as an enum:

| Enum Value | Description |
|------------|-------------|
| `FIRST_NAME` | Customer first name |
| `LAST_NAME` | Customer last name |
| `DATE_OF_BIRTH` | Date of birth |
| `ZIP` | Postal code (PLZ) |
| `ORT` | City |
| `STREET` | Street address |
| `IBAN` | International Bank Account Number |
| `BIC` | Bank Identifier Code |
| `VALID_FROM` | Banking detail valid-from date |
| `FEMALE` | Gender: Female (Boolean) |
| `MALE` | Gender: Male (Boolean) |
| `DIVERSE` | Gender: Diverse (Boolean) |
| `TEXT_AREA` | Response text area content |

---

### com.poc.model.PocModel

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/poc/model/PocModel.java` |
| **Category** | Business — Domain Model |
| **Lines** | 49 |

**Purpose:** Central model. Stores form state as `EnumMap<ModelProperties, ValueModel<?>>`. On `action()`: logs all values, serialises to `HashMap`, calls `HttpBinService.post()`, and emits a success or failure event.

> ⚠️ **Issue:** `model` map is public, `HttpBinService` is instantiated directly (no DI), Boolean values serialised via `.toString()` to string.

---

### com.poc.model.ViewData

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/poc/model/ViewData.java` |
| **Category** | Technical — Stub |
| **Lines** | 5 |

**Purpose:** Empty placeholder class. Not implemented or referenced anywhere.

> ⚠️ **Dead code** — should be removed or implemented as a DTO.

---

### com.poc.presentation.PocView

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/poc/presentation/PocView.java` |
| **Category** | Business — View |
| **Lines** | 203 |

**Purpose:** Renders the "Allegro" Swing form (800×650 px) with a 6-column `GridBagLayout` containing:

| Row | Fields |
|-----|--------|
| 0 | Vorname (First Name), Name (Last Name), Geburtsdatum (DOB) |
| 1 | Geschlecht radio group (Weiblich / Männlich / Divers) |
| 2 | Strasse (Street), PLZ (ZIP), Ort (City) |
| 3 | IBAN, BIC, Gültig ab (Valid From) |
| 4 | RT text area (response display) |
| 5 | Anordnen button (submit) |

> ⚠️ **Bug:** `textArea` is added to the panel twice (lines 188–189). Default gender is `Weiblich`.

---

### com.poc.presentation.PocPresenter

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/com/poc/presentation/PocPresenter.java` |
| **Category** | Business — Presenter |
| **Lines** | 113 |

**Purpose:** The MVP Presenter. Establishes two-way binding between View and Model:
- `bind(JTextComponent, ModelProperties)` — attaches `DocumentListener` for real-time text sync
- `bind(JRadioButton, ModelProperties)` — attaches `ChangeListener` for gender selection sync
- Button `ActionListener` → calls `model.action()`
- `EventEmitter` subscriber → resets form on server response

> ⚠️ **Issue:** HTTP exceptions re-thrown as `RuntimeException` on EDT; `model.action()` blocks EDT during HTTP call.

---

### websocket.Main

| Attribute | Value |
|-----------|-------|
| **Path** | `swing/src/main/java/websocket/Main.java` |
| **Category** | Mixed — Monolithic Client |
| **Lines** | 458 |
| **Language** | Java |

**Purpose:** Self-contained Swing + WebSocket application. Connects to `ws://localhost:1337/`, receives JSON messages, routes them to the UI:
- `target: "textarea"` → updates text area
- `target: "textfield"` → populates all person/address/banking fields

Contains 3 inner classes: `WebsocketClientEndpoint`, `Message`, `SearchResult`.

> ⚠️ **Issues:** Largest file, God-class anti-pattern, duplicate UI code from `PocView`, manual JSON parsing with boolean flags, button has no submission logic.

---

## Dependency Overview

```
com.Main
  └── PocView
  └── EventEmitter ──► EventListener (interface)
  └── PocModel
        └── ValueModel<T>
        └── ModelProperties (enum)
        └── HttpBinService ──► HTTPBin (localhost:8080)
        └── EventEmitter
  └── PocPresenter
        └── PocView (reads components)
        └── PocModel (calls action())
        └── EventEmitter (subscribes)

websocket.Main (standalone)
  └── WebsocketClientEndpoint ──► Node.js WS Server (localhost:1337)
  └── Message (inner record)
  └── SearchResult (inner record)
```

---

## Summary Statistics

| Metric | Value |
|--------|-------|
| Total Java source files | 11 |
| Total approximate LOC | 670 |
| Packages | 4 |
| Classes | 10 |
| Interfaces | 1 |
| Enums | 1 |
| Inner classes | 3 |
| Test files | 0 |
| Test coverage | 0% |
| External dependencies (Maven) | 5 |
| Hardcoded URLs | 2 |
| Java version | 22 |
