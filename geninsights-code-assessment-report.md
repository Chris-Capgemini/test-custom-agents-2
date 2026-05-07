# GenInsights — Comprehensive Code Assessment Report

**Repository:** test-custom-agents-2  
**Analysis Date:** 2025-01-01  
**Analyst:** GenInsights All-in-One Agent  
**Scope:** Full repository — node-server/, node-vue-client/, swing/ (Java/Maven)

---

## Executive Summary

This codebase is a **WebSocket-based PoC** connecting a Vue.js browser frontend to a Java Swing desktop client, via a Node.js WebSocket relay server. While the proof-of-concept logic is functional, the project carries **significant technical debt** across all three sub-systems:

- **Vue 2** has reached **End-of-Life** (December 2023); migration to Vue 3 is mandatory.
- **Java `javax.*` APIs** (WebSocket, JSON) must be replaced with `jakarta.*` equivalents for Java 22 compatibility and modern runtime support.
- **Maven dependency versions** span a decade of drift — from a 2013-era JSON implementation to a mismatched Tyrus version combination.
- **Node.js server** uses a non-standard, low-maintenance WebSocket library with a documented bug in its close-event handler.
- **Security** posture is weak: no origin validation, all traffic unencrypted, hardcoded localhost URLs.

**Overall Health Score: 38 / 100**

---

## Table of Contents

1. [Dependency Audit](#1-dependency-audit)
2. [Security Vulnerabilities](#2-security-vulnerabilities)
3. [Code Quality Issues](#3-code-quality-issues)
4. [Deprecated Patterns & APIs](#4-deprecated-patterns--apis)
5. [Missing Modern Best Practices](#5-missing-modern-best-practices)
6. [Technical Debt Summary](#6-technical-debt-summary)
7. [Actionable Modernization Roadmap](#7-actionable-modernization-roadmap)

---

## 1. Dependency Audit

### 1.1 `pom.xml` — Java/Maven

| # | Group:Artifact | Current Version | Recommended Version | Severity | Notes |
|---|---|---|---|---|---|
| D-01 | `org.glassfish.websocket:websocket-api` | **0.2** | Remove entirely | 🔴 CRITICAL | Pre-standard, pre-release artifact from ~2012. Superseded by the JSR-356 standard (`jakarta.websocket-api`). |
| D-02 | `org.glassfish.tyrus.bundles:tyrus-standalone-client` | **1.15** | **2.2.0** | 🔴 CRITICAL | v1.x uses `javax.websocket`; v2.x migrated to `jakarta.websocket`. Mixing 1.x client with Java 22 creates classpath conflicts. |
| D-03 | `org.glassfish.tyrus:tyrus-websocket-core` | **1.2.1** | Remove / use `tyrus-standalone-client` only | 🔴 CRITICAL | Released ~2013. This artifact was superseded; the standalone client bundle already includes the core. Version mismatch with D-02 (1.15 vs 1.2.1) within the same library family. |
| D-04 | `org.glassfish.tyrus:tyrus-spi` | **1.15** | Included in `tyrus-standalone-client:2.2.0` | 🟠 HIGH | Not needed as a standalone dependency when using the bundle. |
| D-05 | `javax.json:javax.json-api` | **1.1.4** | `jakarta.json:jakarta.json-api:2.1.3` | 🟠 HIGH | `javax.json` namespace was moved to `jakarta.json` under the Jakarta EE umbrella. |
| D-06 | `org.glassfish:javax.json` | **1.0.4** | `org.eclipse.parsson:parsson:1.1.7` | 🟠 HIGH | Released 2013. The reference implementation moved to Eclipse Parsson under `jakarta.json`. |
| D-07 | `maven-compiler-plugin` | *(unversioned)* | Pin to **3.13.0** | 🟡 MEDIUM | No plugin version specified; Maven resolves to an arbitrary cached version, breaking reproducible builds. |

**Recommended `pom.xml` dependencies section:**

```xml
<properties>
    <maven.compiler.source>22</maven.compiler.source>
    <maven.compiler.target>22</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<dependencies>
    <!-- Jakarta WebSocket client (replaces javax.websocket + tyrus v1.x) -->
    <dependency>
        <groupId>org.glassfish.tyrus.bundles</groupId>
        <artifactId>tyrus-standalone-client</artifactId>
        <version>2.2.0</version>
    </dependency>

    <!-- Jakarta JSON API (replaces javax.json-api) -->
    <dependency>
        <groupId>jakarta.json</groupId>
        <artifactId>jakarta.json-api</artifactId>
        <version>2.1.3</version>
    </dependency>

    <!-- Jakarta JSON implementation (replaces org.glassfish:javax.json) -->
    <dependency>
        <groupId>org.eclipse.parsson</groupId>
        <artifactId>parsson</artifactId>
        <version>1.1.7</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
        </plugin>
    </plugins>
</build>
```

---

### 1.2 `node-vue-client/package.json` — Vue Frontend

| # | Package | Current Version | Recommended Version | Severity | Notes |
|---|---|---|---|---|---|
| D-08 | `vue` | **^2.6.10** | **^3.4.0** | 🔴 CRITICAL | Vue 2 reached **End-of-Life on December 31, 2023**. No security patches. Migrate to Vue 3. |
| D-09 | `@vue/cli-service` | **^4.0.0** | **^5.0.8** | 🟠 HIGH | v4 requires Vue 2; v5 supports Vue 3. Alternatively, migrate build to **Vite** (recommended). |
| D-10 | `@vue/cli-plugin-babel` | **^4.0.0** | **^5.0.8** (or drop with Vite) | 🟠 HIGH | Paired with cli-service v4. |
| D-11 | `@vue/cli-plugin-eslint` | **^4.0.0** | **^5.0.8** (or use flat config) | 🟠 HIGH | Paired with cli-service v4. |
| D-12 | `eslint` | **^5.16.0** | **^8.57.0** or **^9.x** | 🟠 HIGH | ESLint 5 released 2019. Versions 5–7 have known issues; v8 is current LTS; v9 is latest. |
| D-13 | `eslint-plugin-vue` | **^5.0.0** | **^9.28.0** | 🟠 HIGH | v5 only supports Vue 2. v9 supports Vue 3 composition API rules. |
| D-14 | `babel-eslint` | **^10.0.1** | **`@babel/eslint-parser` ^7.x** | 🟠 HIGH | `babel-eslint` is **officially deprecated** (2020). Maintainers published `@babel/eslint-parser` as the replacement. |
| D-15 | `vue-template-compiler` | **^2.6.10** | Remove (use `@vue/compiler-sfc` for Vue 3) | 🟡 MEDIUM | Vue 2 only. Must match `vue` version exactly. Not needed in Vue 3. |
| D-16 | `core-js` | **^3.1.2** | **^3.38.0** | 🟡 MEDIUM | Minor: 3.1.2 is old. Modern browsers need fewer polyfills anyway. |

**Recommended `package.json` (Vite + Vue 3 migration target):**

```json
{
  "name": "node-vue-client",
  "version": "0.1.0",
  "private": true,
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext .vue,.js,.ts"
  },
  "dependencies": {
    "vue": "^3.4.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0",
    "eslint": "^8.57.0",
    "eslint-plugin-vue": "^9.28.0",
    "@babel/eslint-parser": "^7.24.0"
  }
}
```

---

### 1.3 `node-server/package.json` — Node.js WebSocket Server

| # | Package | Current Version | Recommended Version | Severity | Notes |
|---|---|---|---|---|---|
| D-17 | `websocket` | **^1.0.35** | Replace with **`ws` ^8.18.0** | 🟠 HIGH | The `websocket` package has low maintenance activity, pulls in `es5-ext` (which has a controversial postinstall script), and uses a non-standard API. `ws` is the de-facto standard (140M weekly downloads, actively maintained). |
| D-18 | `debug` (transitive) | **2.6.9** | Resolved via `ws` | 🟡 MEDIUM | Inherited via `websocket` dependency. Debug 2.x is outdated; 4.x is current. Eliminating `websocket` resolves this. |

**Recommended `package.json`:**

```json
{
  "name": "node-server",
  "version": "1.0.0",
  "description": "WebSocket relay server",
  "main": "src/WebsocketServer.js",
  "scripts": {
    "start": "node src/WebsocketServer.js",
    "dev": "node --watch src/WebsocketServer.js"
  },
  "engines": {
    "node": ">=20.0.0"
  },
  "dependencies": {
    "ws": "^8.18.0"
  }
}
```

---

## 2. Security Vulnerabilities

| # | ID | File | Line | Severity | Description | Remediation |
|---|---|---|---|---|---|---|
| S-01 | No origin validation | `node-server/src/WebsocketServer.js` | 32 | 🔴 CRITICAL | `request.accept(null, request.origin)` accepts **all WebSocket origins** with no validation. Any website can connect to this server. | Validate `request.origin` against an allowlist before calling `request.accept()`. |
| S-02 | Unencrypted transport | All three sub-systems | N/A | 🔴 CRITICAL | All communication uses `ws://` and `http://` (plain text). IBAN, BIC, and personal data (names, DOB, addresses) are transmitted in cleartext. | Use `wss://` and `https://` with TLS. |
| S-03 | Hardcoded localhost URLs | `node-vue-client/src/components/Search.vue:132`, `swing/.../HttpBinService.java:11` | 132, 11 | 🟠 HIGH | Connection targets are hardcoded strings. Cannot be changed without recompiling/redeploying. Prevents staging/production deployment. | Use environment variables (`import.meta.env.VITE_WS_URL` for Vue, Java system properties or a config file for the Swing client). |
| S-04 | Sensitive test data in production code | `node-vue-client/src/components/Search.vue` | 104–124 | 🟠 HIGH | Real-format IBANs (`DE27100777770209299700`), BICs, and personal details are embedded directly in the production component as a hardcoded `search_space` array. Even if fake, this pattern invites real data to be committed to source control. | Move test fixtures to a separate `__fixtures__` or `tests/` directory. Use a mock API or local JSON file loaded at dev-time only. |
| S-05 | Resource leak — Scanner never closed | `swing/.../HttpBinService.java` | 29 | 🟠 HIGH | `new Scanner(connection.getInputStream())` is created but never closed. Under error conditions, this leaks the underlying HTTP connection. | Use try-with-resources: `try (var scanner = new Scanner(...)) { ... }` |
| S-06 | NullPointerException risk | `swing/.../PocModel.java` | 39 | 🟡 MEDIUM | `model.get(val).getField().toString()` — if any `ValueModel` field is `null` (all are initialised to `null` in the constructor), this throws a NPE at runtime during the `action()` call. | Add null guard: `String.valueOf(model.get(val).getField())` or validate fields before submission. |

---

## 3. Code Quality Issues

### 3.1 `node-server/src/WebsocketServer.js`

**ISS-01** — `var` declarations throughout  
**Lines:** 1, 5, 6, 7, 10, 18, 33, 35, 48, 49  
**Severity:** 🟡 Medium  
**Issue:** All variables use `var` (function-scoped, hoisted). Modern JavaScript uses `const` (immutable binding) and `let` (block-scoped mutable).  
**Fix:**
```js
// Before
var webSocketServer = require('websocket').server;
var messages = [];

// After
const { server: WebSocketServer } = require('ws');
// messages[] removed (see ISS-02)
```

---

**ISS-02** — Dead variable `messages`  
**Line:** 6  
**Severity:** 🟡 Medium  
**Issue:** `var messages = []` is declared and never read or written anywhere in the file. Dead code that misleads readers into thinking message history is being maintained.  
**Fix:** Remove the declaration entirely.

---

**ISS-03** — Bug: `close` event callback receives wrong parameters  
**Line:** 62–67  
**Severity:** 🔴 Critical  
**Issue:** The `close` event on a `websocket` library connection fires with `(code, description)` — integers and strings, **not** a connection object. The parameter is shadowed as `connection` which hides the outer `connection` variable. `connection.remoteAddress` will be `undefined`, logging `undefined disconnected.`  
```js
// BUGGY — 'connection' here is the close code (a number), not the connection object
connection.on('close', function (connection) {
    console.log((new Date()) + " Peer " + connection.remoteAddress + " disconnected.");
    clients.splice(index, 1);
});
```
**Fix:**
```js
connection.on('close', function (code, reason) {
    console.log(`${new Date()} Peer disconnected. Code: ${code}, Reason: ${reason}`);
    clients.splice(index, 1);
});
```

---

**ISS-04** — Unsafe `clients.splice(index)` with stale index  
**Lines:** 35, 66  
**Severity:** 🟠 High  
**Issue:** `index` is captured by closure at connection time. If a client with a lower index disconnects first, all subsequent indices shift down by 1, making the stored `index` for remaining clients incorrect. The wrong client gets removed.  
**Fix:** Use `filter` or `indexOf` at removal time:
```js
connection.on('close', (code, reason) => {
    clients = clients.filter(c => c !== connection);
    console.log(`${new Date()} Peer disconnected.`);
});
```

---

**ISS-05** — Empty HTTP server callback  
**Lines:** 10–11  
**Severity:** 🟡 Medium  
**Issue:** The HTTP server handler `function(request, response) {}` does nothing — it neither responds nor rejects HTTP requests. Non-WebSocket HTTP hits will hang indefinitely.  
**Fix:**
```js
const server = http.createServer((req, res) => {
    res.writeHead(426, { 'Content-Type': 'text/plain' });
    res.end('WebSocket connections only.');
});
```

---

### 3.2 `node-vue-client/src/components/Search.vue`

**ISS-06** — Operator precedence ambiguity in `searchPerson()`  
**Lines:** 147–153  
**Severity:** 🟠 High  
**Issue:** Mixed `&&` and `||` without explicit parentheses. Since `&&` binds tighter than `||`, `this.formdata.last && element.name...` is evaluated as a unit, but the intent is a chain of independent "any field matches" checks. The current code works accidentally, but is unreadable and fragile.  
```js
// Ambiguous
if (this.formdata.last && element.name.toLowerCase().indexOf(...)  >= 0
  || this.formdata.first && element.first.toLowerCase()...
```
**Fix — use explicit grouping and modern `includes()`:**
```js
searchPerson() {
    const q = this.formdata;
    this.search_result = this.search_space.filter(el =>
        (q.last  && el.name.toLowerCase().includes(q.last.toLowerCase()))  ||
        (q.first && el.first.toLowerCase().includes(q.first.toLowerCase())) ||
        (q.zip   && el.zip === q.zip)                                       ||
        (q.ort   && el.ort.toLowerCase().includes(q.ort.toLowerCase()))     ||
        (q.street && el.street.toLowerCase().includes(q.street.toLowerCase())) ||
        (q.hausnr && el.hausnr === q.hausnr)
    );
}
```

---

**ISS-07** — No WebSocket error handling  
**Lines:** 131–136  
**Severity:** 🟠 High  
**Issue:** The `connect()` method creates a WebSocket but registers no `onerror` or `onclose` handler. If the server is down, the UI silently fails — `sendMessage()` will throw `Cannot read property 'send' of undefined` or a `CONNECTING` state error with no feedback.  
**Fix:**
```js
connect() {
    this.socket = new WebSocket(import.meta.env.VITE_WS_URL || 'ws://localhost:1337/');
    this.socket.onopen = () => { this.status = 'connected'; };
    this.socket.onerror = (err) => { this.status = 'error'; console.error('WS error', err); };
    this.socket.onclose = () => { this.status = 'disconnected'; };
    this.socket.onmessage = ({ data }) => { /* handle incoming messages */ };
},
```

---

**ISS-08** — `sendMessage` called from watcher before socket is ready  
**Lines:** 172–175  
**Severity:** 🟠 High  
**Issue:** The `internal_content_textarea` watcher fires `sendMessage()` on every keystroke. If the WebSocket is not yet in `OPEN` state (readyState === 1), this throws. No guard exists.  
**Fix:**
```js
watch: {
    internal_content_textarea(val) {
        if (this.socket?.readyState === WebSocket.OPEN) {
            this.sendMessage(val, 'textarea');
        }
    }
}
```

---

**ISS-09** — Hardcoded test data in production component  
**Lines:** 104–124  
**Severity:** 🟠 High  
**Issue:** `search_space` with 5 persons including real-format IBANs is defined inline in `data()`. This will be shipped in every production build.  
**Fix:** Extract to `src/fixtures/persons.js` (guarded by `import.meta.env.DEV`) or replace with a real API call.

---

**ISS-10** — Type inconsistency for `zahlungsempfaenger_selected`  
**Line:** 103  
**Severity:** 🟡 Medium  
**Issue:** Initialised as `""` (string) but later assigned an object `{ iban, bic, valid_from }`. Vue 2's reactivity system handles this, but TypeScript (and Vue 3 with `<script setup>`) would flag it. It also makes serialisation unpredictable.  
**Fix:** Initialise as `null` and guard usage: `zahlungsempfaenger_selected: null`.

---

**ISS-11** — Deprecated HTML attribute `cellpadding`  
**Line:** 33  
**Severity:** 🟡 Medium  
**Issue:** `<table cellpadding=4>` uses a deprecated HTML4 presentational attribute. All modern browsers still support it, but it will fail HTML5 validators.  
**Fix:** Use CSS: `td { padding: 4px; }`.

---

**ISS-12** — `:value` on `<tr>` has no effect  
**Lines:** 46, 70  
**Severity:** 🟡 Low  
**Issue:** `:value="item"` on a `<tr>` is not a valid HTML or Vue binding for table rows. It silently does nothing.  
**Fix:** Remove `:value="item"` from both `v-for` rows. Row selection is already handled via `@click` and `:class`.

---

### 3.3 `swing/src/main/java/websocket/Main.java`

**ISS-13** — `panel.add(textArea)` called twice  
**Lines:** 221–222  
**Severity:** 🔴 Critical  
**Issue:** `textArea` is added to `panel` twice in succession. In Swing, adding a component to a container twice first removes it from its current position (since a component can only have one parent), then re-adds it. The effective result is it only appears at the position of the second `add()` call, but the code is clearly a copy-paste error.  
```java
// Line 221 — first add (will be undone by line 222)
panel.add(textArea);
// Line 222 — second add with constraints (this is the effective one)
panel.add(textArea, c);
```
**Fix:** Remove line 221.

---

**ISS-14** — Same double-add bug in `PocView.java`  
**Lines:** 188–189 (`swing/src/main/java/com/poc/presentation/PocView.java`)  
**Severity:** 🔴 Critical  
**Issue:** Identical copy-paste bug. `panel.add(textArea)` followed by `panel.add(textArea, c)`.  
**Fix:** Remove line 188.

---

**ISS-15** — Manual JSON parsing (verbose, fragile, incomplete)  
**Lines:** 312–341, 355–443 (`websocket/Main.java`)  
**Severity:** 🟠 High  
**Issue:** The `extract()` and `toSearchResult()` methods implement a manual streaming JSON parser using boolean flags for every field. This is ~130 lines of code that Jackson's `ObjectMapper` or Jakarta JSON Binding (`jsonb`) would collapse to ~10 lines. The current implementation will silently produce wrong results if field ordering changes or if arrays are encountered mid-parse.  
**Fix (using Jackson):**
```java
// Add to pom.xml: com.fasterxml.jackson.core:jackson-databind:2.17.2
ObjectMapper mapper = new ObjectMapper();
SearchResult result = mapper.readValue(json, SearchResult.class);
```

---

**ISS-16** — `HttpURLConnection` used instead of `java.net.http.HttpClient`  
**File:** `swing/.../com/poc/model/HttpBinService.java`, **Lines:** 16–36  
**Severity:** 🟠 High  
**Issue:** `HttpURLConnection` is a legacy API dating from Java 1.1. `java.net.http.HttpClient` was introduced in **Java 11** and provides a clean, async-capable, fluent API. The project targets Java 22 — there is no reason to use the legacy API.  
**Fix:**
```java
private final HttpClient httpClient = HttpClient.newHttpClient();

public String post(Map<String, String> data) throws IOException, InterruptedException {
    var jsonBody = Json.createObjectBuilder();
    data.forEach(jsonBody::add);
    var request = HttpRequest.newBuilder()
        .uri(URI.create(URL + PATH))
        .header("Content-Type", CONTENT_TYPE)
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody.build().toString()))
        .build();
    var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
}
```

---

**ISS-17** — Resource leak: `Scanner` never closed  
**File:** `HttpBinService.java`, **Line:** 29  
**Severity:** 🟠 High (also listed as S-05)  
**Issue:** `new Scanner(connection.getInputStream())` is never closed.  
**Fix:** Wrap in try-with-resources or switch to `HttpClient` (see ISS-16).

---

**ISS-18** — NPE in `PocModel.action()` when any field is null  
**File:** `PocModel.java`, **Line:** 39  
**Severity:** 🟠 High (also listed as S-06)  
**Issue:** All `ValueModel` instances are initialised with `null`. Calling `.getField().toString()` on a null field throws `NullPointerException`.  
**Fix:**
```java
// Replace line 39:
data.put(val.toString(), String.valueOf(model.get(val).getField()));
```

---

**ISS-19** — Overly broad exception catching in action listener  
**File:** `PocPresenter.java`, **Lines:** 43–48  
**Severity:** 🟡 Medium  
**Issue:** Both `IOException` and `InterruptedException` are caught separately and wrapped in `RuntimeException`, losing the exception type and providing no user feedback. The UI will silently crash.  
**Fix:**
```java
this.view.button.addActionListener(_ -> {
    try {
        model.action();
    } catch (IOException | InterruptedException e) {
        Thread.currentThread().interrupt(); // restore interrupted status if applicable
        JOptionPane.showMessageDialog(view.frame,
            "Operation failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
});
```

---

**ISS-20** — Excessive debug `System.out.println` logging  
**Files:** `PocPresenter.java` (lines 62, 73, 93), `HttpBinService.java` (lines 30, 31), `websocket/Main.java` (lines 14, 28, 37, 53)  
**Severity:** 🟡 Medium  
**Issue:** Production code uses `System.out.println` for all logging. This cannot be filtered, levelled, or redirected without code changes.  
**Fix:** Replace with **SLF4J + Logback**:
```java
// Add to pom.xml:
// org.slf4j:slf4j-api:2.0.16
// ch.qos.logback:logback-classic:1.5.12

private static final Logger log = LoggerFactory.getLogger(PocPresenter.class);
log.debug("Insert update: {}", content);
```

---

**ISS-21** — `ViewData.java` is an empty dead class  
**File:** `swing/.../com/poc/model/ViewData.java`  
**Severity:** 🟡 Low  
**Issue:** The class exists with no fields, methods, or documentation. It appears to be a placeholder that was never implemented.  
**Fix:** Either implement it or delete it.

---

**ISS-22** — `CountDownLatch` in `websocket.Main` is only partially used  
**File:** `websocket/Main.java`, **Lines:** 28, 53, 249, 276  
**Severity:** 🟡 Medium  
**Issue:** `latch` is initialised in `main()` (line 53) and counted down in `onClose()` (line 276), but `latch.await()` is called inside the `WebsocketClientEndpoint` constructor (line 249) — which blocks the Swing EDT if called from the UI thread. This can freeze the UI until the WebSocket connection closes.  
**Fix:** Connect the WebSocket on a background thread:
```java
SwingUtilities.invokeLater(() -> initUI());
new Thread(() -> new WebsocketClientEndpoint(URI.create(uri))).start();
```

---

## 4. Deprecated Patterns & APIs

| # | Pattern | Location | Java/Spec Version Deprecated | Replacement |
|---|---|---|---|---|
| DEP-01 | `javax.websocket.*` imports | `websocket/Main.java:14–21` | Moved to `jakarta.websocket.*` in Jakarta EE 9 (2020) | `import jakarta.websocket.*` |
| DEP-02 | `javax.json.*` imports | `websocket/Main.java:9–11`, `HttpBinService.java:3` | Moved to `jakarta.json.*` in Jakarta EE 9 | `import jakarta.json.*` |
| DEP-03 | `java.net.HttpURLConnection` | `HttpBinService.java:16` | Superseded by `java.net.http.HttpClient` (Java 11) | `java.net.http.HttpClient` |
| DEP-04 | `babel-eslint` parser | `node-vue-client/package.json:34` | Deprecated 2020 | `@babel/eslint-parser` |
| DEP-05 | `Vue.config.productionTip = false` | `node-vue-client/src/main.js:4` | Removed in Vue 3 | Not needed |
| DEP-06 | Vue 2 Options API component style | All `.vue` files | Not deprecated, but Vue 3 `<script setup>` is the modern default | Migrate to `<script setup>` + Composition API |
| DEP-07 | `v-on:click` verbose syntax | `Search.vue:29, 81` | Not deprecated, but `@click` is the standard shorthand | Use `@click` |
| DEP-08 | `cellpadding` HTML attribute | `Search.vue:33` | Deprecated in HTML5 | CSS `td { padding: 4px }` |
| DEP-09 | `font-family: 'Avenir'` | `App.vue:23` | Avenir is a licensed proprietary font not available by default | `font-family: system-ui, -apple-system, sans-serif` |
| DEP-10 | `var` declarations | `WebsocketServer.js` (all vars) | ES6 (2015) best practice | `const` / `let` |
| DEP-11 | `require()` CommonJS syntax | `WebsocketServer.js:1–2` | Still valid but Node 22 supports native ESM | Consider `import` / `export` with `"type": "module"` |
| DEP-12 | `new Date()` for timestamps in logs | `WebsocketServer.js:13,28,37,52` | Not deprecated; but `Date.now()` or `new Date().toISOString()` is cleaner | `new Date().toISOString()` |

---

## 5. Missing Modern Best Practices

### 5.1 No Tests — Any Layer

The entire codebase has zero automated tests (no unit, integration, or e2e tests). No test directories, no test frameworks, no test scripts.

**Recommended additions:**
- **Vue:** Vitest + Vue Test Utils for component tests
- **Node server:** Node's built-in `node:test` runner or Jest for server logic
- **Java:** JUnit 5 + Mockito for presenter/model logic

---

### 5.2 No CI/CD Pipeline

No `.github/workflows/` exists (only `.github/agents/`). There is no automated build, lint, or test on pull requests.

**Recommended:** Add workflows for:
```yaml
# .github/workflows/ci.yml
on: [push, pull_request]
jobs:
  node-vue:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: '20' }
      - run: cd node-vue-client && npm ci && npm run lint && npm run build

  node-server:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: '20' }
      - run: cd node-server && npm ci

  java:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '22', distribution: 'temurin' }
      - run: mvn verify
```

---

### 5.3 No `.nvmrc` / Node Version Lockdown

The node projects have no `.nvmrc`, `.node-version`, or `engines` field in `package.json`. Developers may run different Node versions causing inconsistent behaviour.

**Fix:** Add `node-server/package.json` engines field (shown in D-17 fix above) and create:
```
# .nvmrc
20
```

---

### 5.4 No Environment Variable Support

Both the Vue client and Java Swing app have hardcoded server addresses:
- Vue: `ws://localhost:1337/` (`Search.vue:132`)
- Java: `http://localhost:8080` (`HttpBinService.java:11`), `ws://localhost:1337/` (`websocket/Main.java:55`)

**Fix for Vue:**
```js
// vite.config.js
export default { /* ... */ }
// .env.development
VITE_WS_URL=ws://localhost:1337/
// .env.production  
VITE_WS_URL=wss://your-server.example.com/ws

// Search.vue
this.socket = new WebSocket(import.meta.env.VITE_WS_URL);
```

**Fix for Java:**
```java
// Read from system property or config file
String serverUrl = System.getProperty("server.url", "http://localhost:8080");
```

---

### 5.5 No Input Validation (IBAN, BIC, Dates)

The form in `Search.vue` accepts any text for IBAN, BIC, and date-of-birth fields without validation. The Java `Main.java` similarly applies no validation before displaying received data in text fields.

**Recommended:**
- Add IBAN checksum validation (use a library like `ibantools`)
- Use `<input type="date">` for date fields instead of free text
- Sanitise all incoming WebSocket message content before inserting into Swing text fields (XSS-equivalent injection risk)

---

### 5.6 No Reconnection Logic

The Vue WebSocket client has no reconnect-on-disconnect logic. If the Node server restarts, the frontend becomes permanently disconnected until the page is manually refreshed.

**Fix:** Add exponential backoff reconnect:
```js
connect() {
    this.socket = new WebSocket(import.meta.env.VITE_WS_URL || 'ws://localhost:1337/');
    this.socket.onclose = () => {
        this.status = 'disconnected';
        setTimeout(() => this.connect(), Math.min(1000 * 2 ** this.retries++, 30000));
    };
}
```

---

### 5.7 No `package-lock.json` in `node-vue-client`

`node-vue-client` uses `yarn.lock` but has no `package-lock.json`. Having both a `yarn.lock` and no `package-lock.json` while documenting npm commands creates confusion. Pick one package manager and commit only its lockfile.

---

## 6. Technical Debt Summary

| ID | Type | Impact | Effort | Description |
|---|---|---|---|---|
| TD-01 | Design | HIGH | HIGH | Vue 2 EOL — entire frontend must be migrated to Vue 3 |
| TD-02 | Design | HIGH | HIGH | Java `javax.*` to `jakarta.*` migration across all imports |
| TD-03 | Code | HIGH | LOW | `websocket` npm package → replace with `ws` |
| TD-04 | Code | HIGH | MEDIUM | Manual JSON parser in `websocket/Main.java` (130+ lines → ~10) |
| TD-05 | Code | HIGH | LOW | `HttpURLConnection` → `java.net.http.HttpClient` |
| TD-06 | Code | MEDIUM | LOW | Remove double `panel.add(textArea)` bug (2 files) |
| TD-07 | Code | MEDIUM | LOW | Fix WebSocket close-event parameter shadowing bug |
| TD-08 | Code | MEDIUM | LOW | Fix stale-index splice bug in client array management |
| TD-09 | Design | MEDIUM | MEDIUM | Extract hardcoded test data from production component |
| TD-10 | Test | HIGH | HIGH | Add test coverage across all three sub-systems |
| TD-11 | Documentation | MEDIUM | LOW | Add missing `start` script to node-server, add `engines` field |
| TD-12 | Code | MEDIUM | LOW | Replace `System.out.println` with SLF4J logging in Java |
| TD-13 | Design | MEDIUM | MEDIUM | Add environment variable support for all hardcoded URLs |
| TD-14 | Design | LOW | MEDIUM | Add CI/CD GitHub Actions workflows |

---

## 7. Actionable Modernization Roadmap

### Sprint 1 — Critical Bug Fixes (1–2 days)

These are bugs causing incorrect runtime behaviour **right now**:

1. **[ISS-03]** Fix `WebsocketServer.js:62` — close event parameter shadowing
2. **[ISS-04]** Fix `WebsocketServer.js:66` — stale index splice bug
3. **[ISS-13]** Remove `panel.add(textArea)` duplicate on `websocket/Main.java:221`
4. **[ISS-14]** Remove `panel.add(textArea)` duplicate on `PocView.java:188`
5. **[ISS-18]** Fix NPE in `PocModel.java:39` — use `String.valueOf()`

### Sprint 2 — Security Hardening (2–3 days)

6. **[S-01]** Add origin validation to the Node WebSocket server
7. **[S-03]** Move all hardcoded URLs to environment variables
8. **[S-04]** Extract `search_space` test data out of the production component
9. **[S-05]** Wrap `Scanner` in try-with-resources (or switch to `HttpClient`)

### Sprint 3 — Dependency Modernization (3–5 days)

10. **[D-01–D-07]** Update `pom.xml`: remove `websocket-api:0.2`, replace Tyrus 1.x+1.2.1 with `tyrus-standalone-client:2.2.0`, replace `javax.json` with `jakarta.json-api + parsson`
11. **[DEP-01–DEP-02]** Update all Java imports from `javax.websocket.*` / `javax.json.*` → `jakarta.*`
12. **[D-17]** Replace `websocket` npm package with `ws` and rewrite `WebsocketServer.js` using `ws` API
13. **[D-14]** Replace `babel-eslint` with `@babel/eslint-parser`

### Sprint 4 — Vue 3 Migration (1–2 weeks)

14. **[D-08–D-16]** Migrate `node-vue-client` from Vue 2 + Vue CLI to **Vue 3 + Vite**
15. Convert `Search.vue` and `App.vue` to `<script setup>` Composition API
16. Add `eslint-plugin-vue` v9 rules

### Sprint 5 — Quality & Observability (ongoing)

17. Add SLF4J + Logback to Java project
18. Add `java.net.http.HttpClient` to replace `HttpURLConnection`
19. Add input validation (IBAN, dates)
20. Add WebSocket reconnect logic in Vue client
21. Add unit and integration tests
22. Add GitHub Actions CI/CD workflow
