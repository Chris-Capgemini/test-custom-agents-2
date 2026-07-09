# Application Overview — Allegro Modernization PoC

## Purpose
This repository is a small modernization PoC around a customer-data form flow using Java Swing, a Node.js WebSocket server, a Vue.js client, and a mock HTTP API.

## Main Components
- **Swing WebSocket client**  
  `swing/src/main/java/websocket/Main.java`  
  Connects to `ws://localhost:1337` and updates a Swing UI from WebSocket messages.

- **Swing HTTP form client**  
  `swing/src/main/java/com/Main.java` with `com.poc.*`  
  Uses `PocView`, `PocPresenter`, `PocModel`, and `HttpBinService` to submit form data to `http://localhost:8080/post`.

- **WebSocket server**  
  `node-server/src/WebsocketServer.js`  
  Listens on port `1337` and broadcasts received UTF-8 messages to all connected clients.

- **Vue client**  
  `node-vue-client/src/components/Search.vue`  
  Provides a simple search UI, opens a WebSocket connection to `ws://localhost:1337`, and sends selected customer data.

- **API contract**  
  `api.yml`  
  Defines `POST /post` for the form payload handled by the Swing HTTP flow.

## Runtime Flow
1. Start HTTPBin on port `8080`.
2. Start the Node WebSocket server on port `1337`.
3. Run one of the Swing clients:
   - `websocket.Main` for the WebSocket-driven UI flow
   - `com.Main` for the HTTP POST flow
4. Optionally run the Vue client to send search results over WebSocket.

## Key Observation
The repository contains two separate Swing entry points, and this document clarifies that both are valid PoC flows:
- `websocket.Main` demonstrates the WebSocket integration.
- `com.Main` demonstrates the HTTP form submission flow described by `api.yml`.
