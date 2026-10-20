# UML Diagrams — Allegro PoC

> **Generated:** 2025-01-01  
> **System:** websocket_swing / Allegro PoC  
> **Format:** Mermaid syntax

---

## Table of Contents

1. [Class Diagram — MVP Module](#1-class-diagram--mvp-module)
2. [Class Diagram — WebSocket Client](#2-class-diagram--websocket-client)
3. [Class Diagram — Full System Overview](#3-class-diagram--full-system-overview)
4. [Sequence Diagram — Form Submission](#4-sequence-diagram--form-submission)
5. [Sequence Diagram — WebSocket Message Receive](#5-sequence-diagram--websocket-message-receive)
6. [Sequence Diagram — Application Bootstrap](#6-sequence-diagram--application-bootstrap)
7. [Use Case Diagram](#7-use-case-diagram)

---

## 1. Class Diagram — MVP Module

```mermaid
classDiagram
    direction TB

    class Main {
        +main(String[] args) void
    }

    class PocView {
        #JFrame frame
        #JTextArea textArea
        #JTextField name
        #JTextField firstName
        #JTextField dateOfBirth
        #JTextField zip
        #JTextField ort
        #JTextField street
        #JTextField iban
        #JTextField bic
        #JTextField validFrom
        #JRadioButton female
        #JRadioButton male
        #JRadioButton diverse
        #ButtonGroup gender
        #JButton button
        +PocView()
        -initUI() void
    }

    class PocPresenter {
        -PocView view
        -PocModel model
        +PocPresenter(PocView, PocModel, EventEmitter)
        -bind(JTextComponent, ModelProperties) void
        -bind(JRadioButton, ModelProperties) void
        -initializeBindings() void
    }

    class PocModel {
        +Map~ModelProperties, ValueModel~ model
        -HttpBinService httpBinService
        -EventEmitter eventEmitter
        +PocModel(EventEmitter)
        +action() void
    }

    class ValueModel~T~ {
        -T field
        +ValueModel(T field)
        +getField() T
        +setField(T field) void
    }

    class ModelProperties {
        <<enumeration>>
        TEXT_AREA
        FIRST_NAME
        LAST_NAME
        DATE_OF_BIRTH
        ZIP
        ORT
        STREET
        IBAN
        BIC
        VALID_FROM
        FEMALE
        MALE
        DIVERSE
    }

    class EventEmitter {
        -List~EventListener~ listeners
        +subscribe(EventListener) void
        +emit(String eventData) void
    }

    class EventListener {
        <<interface>>
        +onEvent(String eventData) void
    }

    class HttpBinService {
        +URL$ String
        +PATH$ String
        +CONTENT_TYPE$ String
        +post(Map~String,String~) String
    }

    class ViewData {
        <<stub>>
    }

    Main --> PocView : creates
    Main --> EventEmitter : creates
    Main --> PocModel : creates
    Main --> PocPresenter : creates

    PocPresenter --> PocView : reads components
    PocPresenter --> PocModel : calls action()
    PocPresenter ..|> EventListener : implements (lambda)
    PocPresenter --> EventEmitter : subscribes

    PocModel --> ValueModel : stores 13x
    PocModel --> ModelProperties : keys
    PocModel --> EventEmitter : emits events
    PocModel --> HttpBinService : delegates POST

    EventEmitter --> EventListener : notifies
```

---

## 2. Class Diagram — WebSocket Client

```mermaid
classDiagram
    direction TB

    class WebsocketMain {
        <<class websocket.Main>>
        -CountDownLatch latch$
        -JFrame frame$
        -JTextArea textArea$
        -JTextField tf_name$
        -JTextField tf_first$
        -JTextField tf_dob$
        -JTextField tf_zip$
        -JTextField tf_ort$
        -JTextField tf_street$
        -JTextField tf_hausnr$
        -JTextField tf_ze_iban$
        -JTextField tf_ze_bic$
        -JTextField tf_ze_valid_from$
        -JRadioButton rb_female$
        -JRadioButton rb_male$
        -JRadioButton rb_diverse$
        -JsonParserFactory jsonParserFactory$
        +main(String[]) void$
        -initUI() void$
        +toSearchResult(String json) SearchResult$
    }

    class WebsocketClientEndpoint {
        <<ClientEndpoint>>
        +Session userSession
        +WebsocketClientEndpoint(URI)
        +onOpen(Session) void
        +onClose(Session, CloseReason) void
        +onMessage(String json) void
        +sendMessage(String) void
        +extract(String json) Message$
    }

    class Message {
        <<record-like>>
        +String target
        +String content
        +Message(String target, String message)
    }

    class SearchResult {
        <<record-like>>
        +String name
        +String first
        +String dob
        +String zip
        +String ort
        +String street
        +String hausnr
        +String ze_iban
        +String ze_bic
        +String ze_valid_from
    }

    WebsocketMain +-- WebsocketClientEndpoint : inner class
    WebsocketMain +-- Message : inner class
    WebsocketMain +-- SearchResult : inner class

    WebsocketClientEndpoint --> Message : creates via extract()
    WebsocketClientEndpoint --> SearchResult : creates via toSearchResult()
    WebsocketClientEndpoint --> WebsocketMain : updates static UI fields
```

---

## 3. Class Diagram — Full System Overview

```mermaid
classDiagram
    direction TB

    namespace MVPModule {
        class PocView
        class PocPresenter
        class PocModel
        class EventEmitter
        class EventListener
        class HttpBinService
        class ValueModel
        class ModelProperties
    }

    namespace WebSocketModule {
        class WsMain["websocket.Main"]
        class WsClientEndpoint["WebsocketClientEndpoint"]
        class Message
        class SearchResult
    }

    PocPresenter --> PocView
    PocPresenter --> PocModel
    PocPresenter ..|> EventListener
    PocPresenter --> EventEmitter

    PocModel --> ValueModel
    PocModel --> ModelProperties
    PocModel --> HttpBinService
    PocModel --> EventEmitter

    EventEmitter --> EventListener

    WsMain +-- WsClientEndpoint
    WsMain +-- Message
    WsMain +-- SearchResult

    WsClientEndpoint --> Message
    WsClientEndpoint --> SearchResult

    HttpBinService ..> ExternalHTTPBin["HTTPBin\n(localhost:8080)"] : HTTP POST
    WsClientEndpoint ..> NodeJsServer["Node.js WS Server\n(localhost:1337)"] : WebSocket
```

---

## 4. Sequence Diagram — Form Submission

```mermaid
sequenceDiagram
    actor User
    participant View as PocView
    participant Presenter as PocPresenter
    participant Model as PocModel
    participant HTTP as HttpBinService
    participant Server as HTTPBin Server
    participant Emitter as EventEmitter

    User->>View: Type in text field
    View->>Presenter: DocumentListener.insertUpdate(event)
    Presenter->>Model: valueModel.setField(content)

    User->>View: Click 'Anordnen'
    View->>Presenter: ActionListener fired
    Presenter->>Model: action()

    Model->>Model: Log all 13 fields to stdout
    Model->>Model: Build HashMap<String,String> from model
    Model->>HTTP: post(data)
    HTTP->>HTTP: Build JSON via javax.json
    HTTP->>Server: HTTP POST /post (JSON body)
    Server-->>HTTP: 200 OK + echoed JSON body

    alt Response non-empty
        HTTP-->>Model: responseBody
        Model->>Emitter: emit(responseBody)
    else Response empty
        HTTP-->>Model: ""
        Model->>Emitter: emit("Failed operation")
    end

    Emitter->>Presenter: onEvent(eventData)
    Presenter->>View: textArea.setText(eventData)
    Presenter->>View: Clear all text fields
    Presenter->>View: Reset gender to Weiblich
```

---

## 5. Sequence Diagram — WebSocket Message Receive

```mermaid
sequenceDiagram
    participant Server as Node.js WS Server
    participant Endpoint as WebsocketClientEndpoint
    participant Parser as javax.json JsonParser
    participant UI as Swing UI (static fields)

    Server->>Endpoint: onMessage(json)
    Endpoint->>Parser: extract(json)
    Parser->>Parser: Parse target and content fields
    Parser-->>Endpoint: Message(target, content)

    alt message.target == "textarea"
        Endpoint->>UI: textArea.setText(message.content)
    else message.target == "textfield"
        Endpoint->>Parser: toSearchResult(message.content)
        Parser->>Parser: Parse all 10 person fields
        Parser-->>Endpoint: SearchResult
        Endpoint->>UI: tf_name.setText(searchResult.name)
        Endpoint->>UI: tf_first.setText(searchResult.first)
        Endpoint->>UI: tf_dob.setText(searchResult.dob)
        Endpoint->>UI: tf_zip / tf_ort / tf_street / tf_hausnr
        Endpoint->>UI: tf_ze_iban / tf_ze_bic / tf_ze_valid_from
    else unknown target
        Endpoint->>Endpoint: No-op (silently ignored)
    end
```

---

## 6. Sequence Diagram — Application Bootstrap

```mermaid
sequenceDiagram
    participant JVM
    participant Main
    participant View as PocView
    participant Emitter as EventEmitter
    participant Model as PocModel
    participant Presenter as PocPresenter
    participant EDT as Swing EDT

    JVM->>Main: main(args)
    Main->>Main: new CountDownLatch(1)
    Main->>View: new PocView()
    View->>View: initUI()
    View->>EDT: frame.setVisible(true)
    Note over EDT: Swing EDT starts handling events
    Main->>Emitter: new EventEmitter()
    Main->>Model: new PocModel(emitter)
    Model->>Model: Initialise 13 ValueModel<null> entries
    Main->>Presenter: new PocPresenter(view, model, emitter)
    Presenter->>Emitter: subscribe(EventListener lambda)
    Presenter->>View: button.addActionListener(...)
    Presenter->>Presenter: initializeBindings()
    Presenter->>View: bind all 10 JTextComponents
    Presenter->>View: bind 3 JRadioButtons
    Main->>Main: latch.await()
    Note over Main: Main thread parked indefinitely
    Note over EDT: Handles all UI events from here
```

---

## 7. Use Case Diagram

```mermaid
flowchart LR
    subgraph Actors
        U([👤 User])
        S([🖥️ HTTPBin Server])
        WS([📡 WebSocket Server])
    end

    subgraph "Allegro PoC — Use Cases"
        UC1[Enter Personal Information]
        UC2[Enter Address Details]
        UC3[Enter Banking Details]
        UC4[Select Gender]
        UC5[Submit Form - Anordnen]
        UC6[View Server Response]
        UC7[Connect to WebSocket Server]
        UC8[Receive Real-Time Data - textarea]
        UC9[Receive Real-Time Data - form fields]
    end

    U --> UC1
    U --> UC2
    U --> UC3
    U --> UC4
    U --> UC5
    U --> UC7

    UC5 --> S
    S --> UC6
    UC6 --> U

    WS --> UC8
    WS --> UC9
    UC8 --> U
    UC9 --> U
```
