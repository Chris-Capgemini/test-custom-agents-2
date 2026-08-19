# GenInsights — File Analysis Summary

**Repository:** test-custom-agents-2  
**Analysis Date:** 2025-01-01  
**Total Files Analyzed:** 14

---

## Repository Map

```
test-custom-agents-2/
├── pom.xml                                          Maven build descriptor
├── api.yml                                          OpenAPI 3.0.1 specification
├── node-server/
│   ├── package.json                                 Node.js dependencies (minimal)
│   ├── package-lock.json                            Lockfile (npm v3)
│   └── src/
│       └── WebsocketServer.js                       ⭐ WebSocket relay server
├── node-vue-client/
│   ├── package.json                                 Vue 2 app dependencies
│   ├── babel.config.js                              Babel configuration
│   ├── public/
│   │   └── index.html                               SPA entry HTML
│   └── src/
│       ├── main.js                                  Vue app bootstrap
│       ├── App.vue                                  Root Vue component
│       └── components/
│           └── Search.vue                           ⭐ Main search/transfer UI
└── swing/
    └── src/main/java/
        ├── websocket/
        │   └── Main.java                            ⭐ Swing UI + WS client (variant 1)
        └── com/poc/
            ├── Main.java                            (referenced in README, not found)
            ├── ValueModel.java                      Generic field wrapper
            ├── model/
            │   ├── ModelProperties.java             Enum of form field names
            │   ├── PocModel.java                    ⭐ MVP Model
            │   ├── HttpBinService.java              ⭐ HTTP POST service
            │   ├── EventEmitter.java                Simple pub/sub emitter
            │   ├── EventListener.java               Event listener interface
            │   └── ViewData.java                    Empty placeholder class
            └── presentation/
                ├── PocView.java                     ⭐ MVP View (Swing UI)
                └── PocPresenter.java                ⭐ MVP Presenter
```

---

## File-by-File Analysis

| File | Language | Category | Lines | Key Responsibilities | Issues |
|---|---|---|---|---|---|
| `pom.xml` | XML/Maven | Technical | 66 | Build config, dependencies | 7 outdated deps, no plugin version pinning |
| `api.yml` | YAML/OpenAPI | Technical | 97 | REST API contract for /post endpoint | Missing `format` on date fields, no auth definition |
| `node-server/package.json` | JSON | Technical | 5 | NPM manifest | Missing name/version/scripts, outdated `websocket` dep |
| `node-server/src/WebsocketServer.js` | JavaScript | Mixed | 68 | WebSocket relay, HTTP server bootstrap | 5 code bugs, security: no origin check |
| `node-vue-client/package.json` | JSON | Technical | 46 | NPM manifest, ESLint config | 9 outdated deps, deprecated `babel-eslint` |
| `node-vue-client/babel.config.js` | JavaScript | Technical | 5 | Babel preset | OK |
| `node-vue-client/public/index.html` | HTML | Technical | 18 | SPA shell | IE=edge meta tag (unnecessary) |
| `node-vue-client/src/main.js` | JavaScript | Technical | 8 | Vue 2 bootstrap | `productionTip` deprecated in Vue 3 |
| `node-vue-client/src/App.vue` | Vue SFC | Mixed | 47 | Root layout, header | Avenir proprietary font |
| `node-vue-client/src/components/Search.vue` | Vue SFC | Business | 177 | Person search, result selection, WS send | 7 bugs/issues, hardcoded data, no error handling |
| `swing/src/.../websocket/Main.java` | Java | Mixed | 457 | Swing UI + WS client endpoint + JSON parsing | 5 bugs, `javax.*` deprecated APIs, verbose parser |
| `swing/src/.../com/poc/ValueModel.java` | Java | Technical | 18 | Generic value wrapper | OK |
| `swing/src/.../com/poc/model/ModelProperties.java` | Java | Business | 18 | Field name enum | OK |
| `swing/src/.../com/poc/model/PocModel.java` | Java | Business | 49 | MVP model, data aggregation, HTTP dispatch | NPE risk on null fields |
| `swing/src/.../com/poc/model/HttpBinService.java` | Java | Technical | 38 | HTTP POST to external service | Legacy API, resource leak, hardcoded URL |
| `swing/src/.../com/poc/model/EventEmitter.java` | Java | Technical | 21 | Simple observable event bus | OK |
| `swing/src/.../com/poc/model/EventListener.java` | Java | Technical | 5 | Functional interface | OK |
| `swing/src/.../com/poc/model/ViewData.java` | Java | Technical | 4 | Empty class — dead code | Delete |
| `swing/src/.../com/poc/presentation/PocView.java` | Java | Business | 203 | Swing form layout | Double panel.add() bug |
| `swing/src/.../com/poc/presentation/PocPresenter.java` | Java | Business | 113 | MVP presenter, bindings, event handling | Exception handling, debug prints |

---

## Language Distribution

| Language | Files | Percentage |
|---|---|---|
| Java | 10 | 50% |
| JavaScript / Vue SFC | 6 | 30% |
| XML / YAML / JSON | 4 | 20% |

## Category Distribution

| Category | Files | Notes |
|---|---|---|
| Business Logic | 6 | Search.vue, Main.java, PocModel, PocPresenter, PocView, ModelProperties |
| Technical/Infrastructure | 10 | Build files, HTTP service, event bus, bootstrap files |
| Mixed | 3 | WebsocketServer.js, websocket/Main.java, App.vue |
