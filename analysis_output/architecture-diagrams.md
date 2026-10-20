# Architecture Diagrams — Allegro PoC

> **Generated:** 2025-01-01  
> **System:** websocket_swing / Allegro PoC  
> **Format:** Mermaid syntax

---

## Table of Contents

1. [Layer Architecture Diagram](#1-layer-architecture-diagram)
2. [Component Diagram — MVP Module](#2-component-diagram--mvp-module)
3. [Component Diagram — WebSocket Module](#3-component-diagram--websocket-module)
4. [Full System Context Diagram](#4-full-system-context-diagram)
5. [Dependency Graph](#5-dependency-graph)
6. [Data Flow Diagram](#6-data-flow-diagram)
7. [Package Structure Diagram](#7-package-structure-diagram)

---

## 1. Layer Architecture Diagram

```mermaid
flowchart TB
    subgraph "🖥️ Presentation Layer"
        PV[PocView\nSwing JFrame + Form Components]
        PP[PocPresenter\nData Binding + Event Wiring]
    end

    subgraph "🧠 Model / Domain Layer"
        PM[PocModel\nForm State + Business Logic]
        VM["ValueModel&lt;T&gt;\nGeneric Value Holder"]
        MP[ModelProperties\nField Key Enum]
        EE[EventEmitter\nPub-Sub Event Bus]
        EL[EventListener\nObserver Interface]
        VD[ViewData\nStub / Placeholder]
    end

    subgraph "🔌 Service / Integration Layer"
        HBS[HttpBinService\nHTTP POST Client]
    end

    subgraph "📡 WebSocket Client Layer"
        WM["websocket.Main\nMonolithic Swing + WS Client"]
    end

    subgraph "🌐 External Systems"
        HTTPBin["HTTPBin Server\nlocalhost:8080"]
        NodeWS["Node.js WS Server\nlocalhost:1337"]
    end

    PP <-->|reads/updates| PV
    PP -->|calls action()| PM
    PP ..|implements| EL
    PP -->|subscribes| EE

    PM -->|stores state in| VM
    PM -->|keyed by| MP
    PM -->|emits via| EE
    PM -->|delegates HTTP| HBS
    EE -->|notifies| EL

    HBS -->|HTTP POST /post| HTTPBin

    WM -->|WebSocket ws://| NodeWS
```

---

## 2. Component Diagram — MVP Module

```mermaid
flowchart LR
    subgraph "com package — MVP Application"
        M[Main.java\nEntry Point]

        subgraph "presentation package"
            V[PocView\nPassive View]
            P[PocPresenter\nActive Presenter]
        end

        subgraph "model package"
            PM[PocModel\nDomain Model]
            EE[EventEmitter]
            EL{EventListener\ninterface}
            HBS[HttpBinService]
            MP[ModelProperties\nenum]
            VD[ViewData\nstub]
        end

        subgraph "poc package"
            VM["ValueModel&lt;T&gt;"]
        end
    end

    M -->|"new PocView()"| V
    M -->|"new EventEmitter()"| EE
    M -->|"new PocModel(emitter)"| PM
    M -->|"new PocPresenter(v,m,e)"| P

    P -->|"view.button / view.textArea etc."| V
    P -->|"model.action()"| PM
    P -->|"subscribe(lambda)"| EE
    P -.->|"implements via lambda"| EL

    PM -->|"EnumMap keys"| MP
    PM -->|"stores 13x"| VM
    PM -->|"emit()"| EE
    PM -->|"post(data)"| HBS

    EE -->|"notify"| EL
```

---

## 3. Component Diagram — WebSocket Module

```mermaid
flowchart TB
    subgraph "websocket package — WebSocket Client Application"
        WM["websocket.Main\n(God Class)"]

        subgraph "Inner Classes"
            CE[WebsocketClientEndpoint\n@ClientEndpoint]
            MSG[Message\ntarget + content]
            SR[SearchResult\nPerson Data POJO]
        end

        subgraph "Static UI Fields"
            UI["JFrame + JTextArea\n+ 10x JTextField\n+ 3x JRadioButton"]
        end
    end

    subgraph "External"
        NWS["Node.js WebSocket Server\nws://localhost:1337"]
    end

    WM -->|"creates"| CE
    WM -->|"manages"| UI
    CE -->|"@OnOpen @OnClose @OnMessage"| NWS
    CE -->|"extract(json)"| MSG
    CE -->|"toSearchResult(json)"| SR
    CE -->|"update static fields"| UI
```

---

## 4. Full System Context Diagram

```mermaid
flowchart TB
    subgraph "Development Machine"

        subgraph "Java Application (websocket_swing)"
            subgraph "MVP App (com.Main)"
                A1[PocView\nAllegro Form UI]
                A2[PocPresenter]
                A3[PocModel]
                A4[HttpBinService]
            end

            subgraph "WebSocket App (websocket.Main)"
                B1[Allegro Form UI\nStatic Swing Fields]
                B2[WebsocketClientEndpoint]
            end
        end

        subgraph "Docker Container"
            HTTPBin["🐳 kennethreitz/httpbin\nPort 8080:80\nEcho HTTP POST"]
        end

        subgraph "Node.js Process"
            NodeWS["📦 WebsocketServer.js\nPort 1337\nBroadcast WS Server"]
        end

        subgraph "Optional"
            VueClient["🌐 Vue.js Client\nnode-vue-client\n(Browser App)"]
        end
    end

    User1([👤 User\nMVP App]) -->|"form input"| A1
    A1 <-->|"Swing events"| A2
    A2 <-->|"action() / events"| A3
    A3 -->|"HTTP POST\n/post JSON"| HTTPBin
    HTTPBin -->|"JSON echo response"| A3

    User2([👤 User\nWS App]) -->|"view data"| B1
    B2 <-->|"WebSocket\nws://localhost:1337"| NodeWS
    NodeWS <-->|"WebSocket"| VueClient
    B2 -->|"update UI fields"| B1
```

---

## 5. Dependency Graph

```mermaid
flowchart LR
    Main --> PocView
    Main --> EventEmitter
    Main --> PocModel
    Main --> PocPresenter

    PocPresenter --> PocView
    PocPresenter --> PocModel
    PocPresenter --> EventEmitter
    PocPresenter --> ValueModel

    PocModel --> ValueModel
    PocModel --> ModelProperties
    PocModel --> EventEmitter
    PocModel --> HttpBinService

    EventEmitter --> EventListener

    HttpBinService --> javax.json[javax.json\nstreaming API]
    HttpBinService --> java.net.HttpURLConnection[java.net\nHttpURLConnection]

    WsMain[websocket.Main] --> javax.websocket[javax.websocket\nTyrus 1.15]
    WsMain --> javax.json
    WsMain --> javax.swing[javax.swing]
    javax.websocket --> TyrusClient[Tyrus Standalone\nClient 1.15]
```

---

## 6. Data Flow Diagram

```mermaid
flowchart LR
    subgraph "User Input"
        FI[Form Fields\nKeystrokes]
        GB[Gender\nRadio Button]
        BTN[Anordnen\nButton Click]
    end

    subgraph "Binding Layer (PocPresenter)"
        DL[DocumentListener]
        CL[ChangeListener]
        AL[ActionListener]
    end

    subgraph "Model State (PocModel)"
        VM["EnumMap\nModelProperties → ValueModel&lt;T&gt;"]
    end

    subgraph "Serialisation (PocModel.action)"
        HM["HashMap&lt;String,String&gt;\n13 entries"]
        JSON[JSON Object\njavax.json streaming]
    end

    subgraph "HTTP Transport (HttpBinService)"
        REQ[HTTP POST\nlocalhost:8080/post]
        RES[HTTP 200\nJSON response body]
    end

    subgraph "Event Bus"
        EV[EventEmitter.emit\nString payload]
    end

    subgraph "View Update (PocPresenter lambda)"
        TA[textArea.setText\nresponse]
        CLR[Clear all fields]
        RST[Reset gender to Female]
    end

    FI --> DL --> VM
    GB --> CL --> VM
    BTN --> AL --> VM
    VM --> HM --> JSON --> REQ --> RES --> EV --> TA & CLR & RST
```

---

## 7. Package Structure Diagram

```mermaid
flowchart TB
    subgraph "swing/src/main/java"
        subgraph "com package"
            CM[Main.java]
            subgraph "poc package"
                VMCLS["ValueModel.java"]
                subgraph "model package"
                    EE[EventEmitter.java]
                    EL[EventListener.java]
                    HBS[HttpBinService.java]
                    MP[ModelProperties.java]
                    PM[PocModel.java]
                    VD[ViewData.java]
                end
                subgraph "presentation package"
                    PP[PocPresenter.java]
                    PV[PocView.java]
                end
            end
        end

        subgraph "websocket package"
            WM[Main.java]
        end
    end

    subgraph "node-server/src"
        WJS[WebsocketServer.js]
    end

    subgraph "Project Root"
        POM[pom.xml\nMaven Build]
        API[api.yml\nOpenAPI 3.0]
    end
```

---

## Architecture Summary

| Aspect | Details |
|--------|---------|
| **Architecture Style** | Desktop client (Swing), MVP pattern |
| **Communication Styles** | Synchronous HTTP (MVP module), WebSocket (WS module) |
| **Data Format** | JSON (both in/out) |
| **Key Patterns** | MVP, Observer/EventBus, Generic ValueObject, Enum-keyed Map |
| **External Dependencies** | HTTPBin (echo), Node.js WS Server |
| **Build** | Maven 3.x, Java 22 |
| **Identified Risks** | No input validation, EDT blocking, hardcoded URLs, zero tests |
