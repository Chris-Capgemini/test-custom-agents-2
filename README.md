
# 🖥️ Java Swing Project Setup Guide

Follow these steps to get your Java Swing project up and running:

---

## 🔧 Prerequisites

- **Java SDK**: Version **21 LTS** (or newer)
- **Maven**: Version **3.8+**
- **IDE**: IntelliJ IDEA (recommended)
- **Docker**: Docker Desktop or Rancher Desktop

---

## ⚡ Modernisation Notes (Java 21)

This project has been updated to **Java 21 LTS**:

| Area | Before | After |
|------|--------|-------|
| Java version | 22 | **21 LTS** |
| WebSocket API | `javax.websocket` (Tyrus 1.x) | `jakarta.websocket` (Tyrus 2.x) |
| JSON Processing | `javax.json` | `jakarta.json` (Eclipse Parsson) |
| HTTP client | `HttpURLConnection` (deprecated) | `java.net.http.HttpClient` (Java 11+) |
| Data classes | Mutable inner classes | **Records** (`Message`, `SearchResult`) |
| Switch | Legacy `switch` statement | **Enhanced switch** expression |
| Maven build | No `sourceDirectory` configured | Explicit `sourceDirectory` + JUnit 5 |

---

## 🚀 Getting Started

### 1. Set Java SDK in IntelliJ

> **Path:** `File > Project Structure > Project`

- Set the **Project SDK** to **Java 21 LTS**

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

