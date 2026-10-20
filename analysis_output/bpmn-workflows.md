# BPMN Workflows — Allegro PoC

> **Generated:** 2025-01-01  
> **System:** websocket_swing / Allegro PoC

---

## Table of Contents

1. [WF-001 — MVP Form Submission Workflow](#wf-001--mvp-form-submission-workflow)
2. [WF-002 — WebSocket Data Receive Workflow](#wf-002--websocket-data-receive-workflow)
3. [WF-003 — Application Bootstrap Workflow](#wf-003--application-bootstrap-workflow)
4. [WF-004 — WebSocket Connection Lifecycle](#wf-004--websocket-connection-lifecycle)

---

## WF-001 — MVP Form Submission Workflow

**Description:** End-to-end flow from user data entry to server response display in the MVP (PocPresenter / PocModel / PocView) application.

**Actors:** User, Swing EDT, PocPresenter, PocModel, HttpBinService, HTTPBin Server, EventEmitter

```mermaid
flowchart TD
    A([▶ User Opens Application]) --> B[Form Displayed in 'Allegro' Window]
    B --> C{User Action?}

    C -->|Types in field| D[DocumentListener fires insertUpdate / removeUpdate]
    D --> E[ValueModel updated with new text]
    E --> C

    C -->|Selects gender| F[ChangeListener fires]
    F --> G[ValueModel Boolean updated]
    G --> C

    C -->|Clicks 'Anordnen'| H[ActionListener calls model.action]

    H --> I[PocModel: Log all 13 fields to stdout]
    I --> J[PocModel: Serialise all fields to HashMap String String]
    J --> K[HttpBinService.post: Build JSON via javax.json streaming API]
    K --> L[HTTP POST to http://localhost:8080/post]

    L --> M{HTTP Response?}

    M -->|Response body non-empty| N[eventEmitter.emit responseBody]
    M -->|Response body empty| O[eventEmitter.emit 'Failed operation']
    M -->|IOException / InterruptedException| P[throw RuntimeException - EDT crash risk]

    N --> Q[PocPresenter EventListener lambda triggered]
    O --> Q

    Q --> R[textArea.setText eventData]
    Q --> S[Clear all text fields]
    Q --> T[Reset gender to Weiblich]

    R --> U([◼ Form Reset - Ready for next entry])
    S --> U
    T --> U

    P --> V([◼ Application Error - Unhandled])
```

---

## WF-002 — WebSocket Data Receive Workflow

**Description:** Flow for receiving and rendering server-pushed data in the standalone WebSocket Swing client (`websocket.Main`).

**Actors:** Node.js WebSocket Server, WebsocketClientEndpoint, Swing EDT

```mermaid
flowchart TD
    A([▶ WebSocket Client Connected to ws://localhost:1337/]) --> B[Waiting for server message]

    B --> C[WebSocket message received - JSON string]
    C --> D[WebsocketClientEndpoint.onMessage called]
    D --> E[extract JSON: parse target and content fields]

    E --> F{message.target?}

    F -->|target = 'textarea'| G[textArea.setText message.content]
    G --> H([◼ Text area updated])

    F -->|target = 'textfield'| I[toSearchResult message.content called]
    I --> J[Parse JSON: name, first, dob, zip, ort, street, hausnr, iban, bic, valid_from]
    J --> K[Populate all 10 Swing text fields from SearchResult]
    K --> L([◼ All form fields populated])

    F -->|unknown target| M[No UI update - silently ignored]
    M --> N([◼ No-op])

    H --> B
    L --> B
    N --> B
```

---

## WF-003 — Application Bootstrap Workflow

**Description:** Startup sequence for the MVP Swing application from JVM invocation to UI ready.

**Actors:** JVM, Main, PocView, EventEmitter, PocModel, PocPresenter, Swing EDT

```mermaid
flowchart TD
    A([▶ JVM: java com.Main]) --> B[Create CountDownLatch 1]
    B --> C[new PocView]
    C --> D[initUI: Build GridBagLayout form]
    D --> E[frame.setVisible true - Allegro window appears]
    E --> F[new EventEmitter - empty subscriber list]
    F --> G[new PocModel eventEmitter]
    G --> H[Initialise 13 ValueModel entries as null in EnumMap]
    H --> I[new PocPresenter pocView, pocModel, eventEmitter]

    I --> J[eventEmitter.subscribe EventListener lambda]
    J --> K[view.button.addActionListener]
    K --> L[initializeBindings]

    L --> L1[bind textArea to TEXT_AREA]
    L --> L2[bind firstName to FIRST_NAME]
    L --> L3[bind name to LAST_NAME]
    L --> L4[bind dateOfBirth to DATE_OF_BIRTH]
    L --> L5[bind zip, ort, street]
    L --> L6[bind iban, bic, validFrom]
    L --> L7[bind female, male, diverse radio buttons]

    L1 & L2 & L3 & L4 & L5 & L6 & L7 --> M[All bindings active]
    M --> N[latch.await - main thread parked]
    N --> O([◼ Swing EDT handles all events])
```

---

## WF-004 — WebSocket Connection Lifecycle

**Description:** Full lifecycle of the WebSocket connection in `websocket.Main`, from startup to shutdown.

**Actors:** Main, WebsocketClientEndpoint, Node.js Server, CountDownLatch

```mermaid
flowchart TD
    A([▶ JVM: java websocket.Main]) --> B[initUI - render Allegro form]
    B --> C[latch = new CountDownLatch 1]
    C --> D[new WebsocketClientEndpoint URI ws://localhost:1337/]

    D --> E{Server reachable?}

    E -->|Yes| F[ContainerProvider.getWebSocketContainer.connectToServer]
    F --> G[onOpen: session stored, log 'opening websocket']
    G --> H[latch.await - main thread waits]

    H --> I{Server sends message?}
    I -->|Yes| J[onMessage: route by target]
    J --> K[Update Swing UI]
    K --> I

    I -->|Connection closed| L[onClose: session = null, latch.countDown]
    L --> M([◼ Main thread released - application exits])

    E -->|No - DeploymentException| N[throw RuntimeException]
    N --> O([◼ Application crashes on startup])
```

---

## Workflow Summary

| Workflow | Trigger | Key Components | Outcome |
|----------|---------|---------------|---------|
| WF-001 MVP Form Submit | Click 'Anordnen' | PocPresenter → PocModel → HttpBinService → EventEmitter | Form reset + response displayed |
| WF-002 WebSocket Receive | Server pushes JSON | WebsocketClientEndpoint → Swing UI | Form fields or textarea updated |
| WF-003 App Bootstrap | JVM start | Main → PocView → PocModel → PocPresenter | Window displayed, bindings active |
| WF-004 WS Lifecycle | JVM start (websocket module) | WebsocketClientEndpoint → Node.js Server | Connection maintained until server disconnect |
