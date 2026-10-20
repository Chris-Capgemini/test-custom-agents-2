# Business Rules — Allegro PoC

> **Generated:** 2025-01-01  
> **System:** websocket_swing / Allegro PoC  
> **Total rules extracted:** 18

---

## Table of Contents

1. [Data Capture Rules](#data-capture-rules)
2. [Form Submission Rules](#form-submission-rules)
3. [UI State Rules](#ui-state-rules)
4. [WebSocket Communication Rules](#websocket-communication-rules)
5. [API / Schema Rules](#api--schema-rules)
6. [Error Handling Rules](#error-handling-rules)

---

## Data Capture Rules

### BR-001 — All Form Fields Initialised to Null

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | Medium |
| **Source** | `PocModel.java` |

On application startup, every `ModelProperties` entry (13 fields) is initialised with a `null` value inside `PocModel`. The presenter then synchronises field values from the current View state during binding initialisation.

**Condition:** Application startup  
**Action:** Initialise all 13 `ValueModel` entries to `null`

---

### BR-002 — Real-Time Model Synchronisation on Keystroke

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | High |
| **Source** | `PocPresenter.java` |

Every time the user types into or deletes from any text field, the corresponding `ValueModel<String>` in the model is immediately updated with the complete current text content via `DocumentListener.insertUpdate()` and `removeUpdate()`.

**Condition:** User types or deletes in any text field  
**Action:** Call `model.setField(currentText)` synchronously on the EDT

---

### BR-003 — Radio Button Gender Synchronisation

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | Medium |
| **Source** | `PocPresenter.java` |

When any gender radio button (`Weiblich`/`Männlich`/`Divers`) changes state, the corresponding `ValueModel<Boolean>` in the model is immediately updated via `ChangeListener`.

**Condition:** Gender radio button selection changes  
**Action:** Update `ValueModel<Boolean>` with `source.isSelected()`

---

### BR-004 — Default Gender Selection is Female

| Attribute | Value |
|-----------|-------|
| **Type** | Decision |
| **Priority** | Low |
| **Source** | `PocView.java`, `PocPresenter.java` |

The `Weiblich` (Female) radio button is pre-selected by default at form initialisation. After a successful form submission and server response, the gender selection is also reset to `Weiblich`.

**Condition:** Application start OR successful form submission event  
**Action:** `female.setSelected(true)`, `male.setSelected(false)`, `diverse.setSelected(false)`

---

## Form Submission Rules

### BR-005 — Form Submission Serialises All Fields

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | Critical |
| **Source** | `PocModel.java` |

When the `Anordnen` button is clicked, **all 13** `ModelProperties` fields are collected into a flat `HashMap<String, String>` and submitted together as a single JSON object in the HTTP POST body. No field is omitted — even null or empty fields are included.

**Condition:** User clicks `Anordnen` button  
**Action:** Serialise all 13 fields and POST to `http://localhost:8080/post`

---

### BR-006 — Non-Empty HTTP Response Triggers Success Event

| Attribute | Value |
|-----------|-------|
| **Type** | Decision |
| **Priority** | High |
| **Source** | `PocModel.java` |

After the HTTP POST completes, if the response body string is not empty, it is emitted to all `EventEmitter` subscribers as the event data (success path).

**Condition:** HTTP response body `!responseBody.isEmpty()`  
**Action:** `eventEmitter.emit(responseBody)`

---

### BR-007 — Empty HTTP Response Triggers Failure Event

| Attribute | Value |
|-----------|-------|
| **Type** | Decision |
| **Priority** | High |
| **Source** | `PocModel.java` |

If the HTTP response body is empty, the fixed string `"Failed operation"` is emitted instead, signalling a failure without an exception.

**Condition:** HTTP response body `responseBody.isEmpty()`  
**Action:** `eventEmitter.emit("Failed operation")`

---

### BR-010 — HTTP POST Target is Fixed Localhost Endpoint

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | Medium |
| **Source** | `HttpBinService.java` |

The HTTP submission target is hardcoded: `http://localhost:8080/post`. The expected backend is a locally running HTTPBin Docker container (`docker run -p 8080:80 kennethreitz/httpbin`).

**Condition:** Any form submission  
**Action:** POST JSON to `http://localhost:8080/post` with `Content-Type: application/json`

---

### BR-016 — Boolean Gender Values Serialised as String

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | Low |
| **Source** | `PocModel.java` |

Boolean `ValueModel` values for `MALE`, `FEMALE`, and `DIVERSE` are serialised via `.toString()`, producing string literals `"true"` or `"false"` in the JSON POST payload.

**Condition:** Form submission  
**Action:** Boolean converted to string `"true"` or `"false"` in JSON body

---

## UI State Rules

### BR-008 — Server Response Resets the Form

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | High |
| **Source** | `PocPresenter.java` |

When `EventEmitter` fires (after any HTTP submission), the View is fully reset: all text fields are cleared to empty strings, gender is reset to `Weiblich`, and the server response JSON is displayed in the `textArea`.

**Condition:** `EventEmitter.emit()` called with any event data  
**Action:** Clear all text fields, reset gender to Female, set `textArea.setText(eventData)`

---

### BR-009 — Window Close Terminates Application

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | Low |
| **Source** | `PocView.java`, `com.Main.java` |

The `JFrame` is configured with `EXIT_ON_CLOSE`. Closing the window directly terminates the JVM. The `CountDownLatch(1)` in `main()` is never decremented (no graceful shutdown path).

---

## WebSocket Communication Rules

### BR-011 — WebSocket message target='textarea' Updates Text Area Only

| Attribute | Value |
|-----------|-------|
| **Type** | Decision |
| **Priority** | High |
| **Source** | `websocket/Main.java` |

In the WebSocket client, if an incoming JSON message has `target="textarea"`, only the `textArea` Swing component is updated with the `content` field value.

**Expected JSON format:**
```json
{ "target": "textarea", "content": "some text" }
```

---

### BR-012 — WebSocket message target='textfield' Populates All Fields

| Attribute | Value |
|-----------|-------|
| **Type** | Decision |
| **Priority** | High |
| **Source** | `websocket/Main.java` |

If the incoming message has `target="textfield"`, the full JSON payload is parsed into a `SearchResult` and all person/address/banking text fields are populated.

**Expected JSON field mapping:**

| JSON Key | Swing Field |
|----------|-------------|
| `name` | tf_name |
| `first` | tf_first |
| `dob` | tf_dob |
| `zip` | tf_zip |
| `ort` | tf_ort |
| `street` | tf_street |
| `hausnr` | tf_hausnr |
| `iban` | tf_ze_iban |
| `bic` | tf_ze_bic |
| `valid_from` | tf_ze_valid_from |

---

### BR-013 — WebSocket Close Releases Application

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | Medium |
| **Source** | `websocket/Main.java` |

When the WebSocket connection closes, `latch.countDown()` is called, unblocking the main thread and allowing graceful application termination.

---

### BR-015 — WebSocket Server Broadcasts to All Clients

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | Medium |
| **Source** | `node-server/src/WebsocketServer.js` |

The Node.js WebSocket server relays every received message to **all** connected clients without any filtering, routing, or authentication.

---

### BR-018 — Person Data Schema (SearchResult)

| Attribute | Value |
|-----------|-------|
| **Type** | Validation |
| **Priority** | Medium |
| **Source** | `websocket/Main.java` |

The WebSocket client expects person data JSON with keys: `name`, `first`, `dob`, `zip`, `ort`, `street`, `hausnr`, `iban`, `bic`, `valid_from`. Missing keys silently leave `SearchResult` fields as `null`.

---

## API / Schema Rules

### BR-014 — All Form Fields Are Optional (No Validation)

| Attribute | Value |
|-----------|-------|
| **Type** | Validation |
| **Priority** | Medium |
| **Source** | `api.yml`, `ModelProperties.java` |

The OpenAPI specification defines all 13 fields as optional strings. No validation is enforced at the UI layer, model layer, or HTTP service — any value (including null) is accepted and submitted.

**Fields defined in API spec:**
`FIRST_NAME`, `LAST_NAME`, `DATE_OF_BIRTH`, `STREET`, `BIC`, `ORT`, `ZIP`, `FEMALE`, `MALE`, `DIVERSE`, `IBAN`, `VALID_FROM`, `TEXT_AREA`

---

## Error Handling Rules

### BR-017 — Checked Exceptions Converted to RuntimeException

| Attribute | Value |
|-----------|-------|
| **Type** | Process |
| **Priority** | High |
| **Source** | `PocPresenter.java` |

`PocPresenter` catches `IOException` and `InterruptedException` from `model.action()` and re-throws them as `RuntimeException`. This means HTTP communication failures become unchecked exceptions that crash the Swing EDT silently with no user-visible feedback.

> ⚠️ **Risk:** Users will see a frozen or crashed window with no explanation if the HTTPBin server is unavailable.
