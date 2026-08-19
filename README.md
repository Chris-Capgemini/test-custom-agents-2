
# 🖥️ Java Swing Project Setup Guide

Follow these steps to get your Java Swing project up and running:

---

## 🔧 Prerequisites

- **Java SDK**: Version **>= 22.0.1**
- **IDE**: IntelliJ IDEA (recommended)
- **Docker**: Docker Desktop or Rancher Desktop

---

## 🚀 Getting Started

### 1. Set Java SDK in IntelliJ

> **Path:** `File > Project Structure > Project`

- Set the **Project SDK** to **Java >= 22.0.1**

---

### 2. Start the HTTPBin Docker Container

Make sure Docker or Rancher is running, then execute:

```bash
docker run -p 8080:80 kennethreitz/httpbin 
```

---

### 3. 🗂️ Configure Content Root in IntelliJ

> **Menu Path:** `File > Project Structure > Modules`

1. Remove all existing **Content Root Entries**
2. + Add a new Content Root:

```text
websocket_swing/swing/src/main/java
```


---

### 4. ▶️ Run the Application

1. Run Main.java

```text
websocket_swing/swing/src/main/java/com
```

---

## 📐 Architecture Documentation

A comprehensive **arc42 architecture documentation** has been generated for this project.

📄 **File:** [`docs/arc42-architecture.md`](./docs/arc42-architecture.md)

The document covers all 12 arc42 sections:
- System context & external interfaces
- Component building blocks (Vue.js SPA, Node.js WebSocket relay, Java Swing MVP)
- Runtime scenarios (customer search → data transfer → HTTP submission)
- Deployment view & port allocation
- Crosscutting concepts (WebSocket envelope, MVP pattern, Observer pattern)
- Architectural decision records (ADR-001 through ADR-005)
- Risks, technical debt, and improvement recommendations
- Full glossary (German domain terms + technical terms)

