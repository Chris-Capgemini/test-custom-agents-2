# Arc42 Architecture Insights (Code Analysis)

This lightweight document captures four high-value insights from static analysis of the Allegro PoC codebase.

## 1) Event-driven coordination is the integration backbone
- The Java Swing app wires UI updates through a custom `EventEmitter` and presenter bindings (`swing/src/main/java/com/poc/presentation/PocPresenter.java`).
- The same event-driven pattern appears on the frontend/backend boundary via WebSocket broadcasts (`node-server/src/WebsocketServer.js`).

## 2) The data flow is intentionally simple but tightly coupled
- User input is collected in Swing model fields (`PocModel`) and sent as a flat key/value map to `HttpBinService`.
- The model currently serializes every field through `toString()`, which keeps implementation simple but couples behavior to non-null field assumptions.

## 3) The Node WebSocket server acts as a relay, not a domain service
- `WebsocketServer.js` accepts client messages and broadcasts payloads to all connected clients without validation or routing logic.
- This minimizes server complexity and keeps domain behavior in clients, but shifts trust and validation to the edge.

## 4) The Vue client embeds business test data and workflows in-component
- `Search.vue` contains the search dataset, filtering behavior, selection state, and WebSocket messaging in a single component.
- This accelerates prototyping, while indicating a clear modernization path: separate data/services from view logic for maintainability.
