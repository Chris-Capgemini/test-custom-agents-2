# arc42 Documentation (Starter)

This document is the starting point for arc42-based project documentation.

## 1. Introduction and Goals
- **System:** `test-custom-agents-2`
- **Primary Goal:** Demonstrate and test custom agent workflows alongside a multi-technology sample application (Java Swing, Node server, Vue client, and API test assets).

## 2. Constraints
- Mixed technology stack maintained in one repository.
- Existing project structure and run instructions in root `README.md` must remain valid.

## 3. Context and Scope
- **Java Swing app:** `swing/src/main/java`
- **Node WebSocket server:** `node-server/`
- **Vue client:** `node-vue-client/`
- **Generated analysis/test artifacts:** `analysis_output/`

## 4. Solution Strategy
- Keep each technology area independently runnable.
- Use repository documentation as the central entry point.
- Add architecture and quality insights incrementally in this arc42 document.

## 5. Building Block View (High-Level)
- **UI Layer:** Java Swing UI + Vue UI
- **Backend Layer:** Node WebSocket server + HTTP integrations
- **Support Artifacts:** API test generator output and supporting docs

## 6. Next Documentation Steps
1. Add component-level diagrams for Swing, Node server, and Vue client.
2. Add key runtime scenarios (startup, request/response, message flow).
3. Add deployment and quality sections (risks, technical debt, decisions).
