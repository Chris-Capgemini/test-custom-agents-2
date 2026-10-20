# GenInsights Analysis — Output Index
## Allegro PoC (websocket_swing)

> **Generated:** 2025-01-01  
> **Agent:** GenInsights All-in-One Agent  
> **Repository:** Chris-Capgemini/test-custom-agents-2  
> **Analysis Root:** `/analysis_output/`

---

## 📋 Output Files

### 🗂️ Analysis Data (JSON)

| File | Description |
|------|-------------|
| [`analysis_results.json`](./analysis_results.json) | Full file-by-file analysis with methods, dependencies, business rules per file |
| [`business_rules.json`](./business_rules.json) | 18 extracted business rules + 3 workflow definitions |
| [`code_assessment.json`](./code_assessment.json) | 14 code issues + 6 technical debt items + metrics (health score: 62/100) |
| [`architecture_analysis.json`](./architecture_analysis.json) | 4 layers, 6 components, design patterns, architectural decisions |
| [`capability_mapping.json`](./capability_mapping.json) | 4 business domains, 12 capabilities, 4 identified gaps |
| [`uml_analysis.json`](./uml_analysis.json) | Catalogue of 12 diagrams (class, sequence, use-case, component, BPMN) |

### 📄 Documentation (Markdown)

| File | Description |
|------|-------------|
| [`file-analysis-summary.md`](./file-analysis-summary.md) | Comprehensive per-file analysis with dependency tree |
| [`business-rules.md`](./business-rules.md) | 18 business rules organised by category |
| [`bpmn-workflows.md`](./bpmn-workflows.md) | 4 BPMN-style workflow diagrams (Mermaid) |
| [`code-assessment-report.md`](./code-assessment-report.md) | Code quality report with issues, debt, priority matrix |
| [`uml-diagrams.md`](./uml-diagrams.md) | 7 UML diagrams: class (×3), sequence (×3), use-case (×1) |
| [`architecture-diagrams.md`](./architecture-diagrams.md) | 7 architecture diagrams: layers, components, context, data flow, packages |
| [`capability-mapping.md`](./capability-mapping.md) | Business capability mapping with coverage analysis |
| [`architecture-documentation.md`](./architecture-documentation.md) | **Arc42 complete architecture documentation (12 sections)** |

### 📝 Logs

| File | Description |
|------|-------------|
| [`agent-work-log.md`](./agent-work-log.md) | Phase-by-phase activity log |

---

## 🔑 Key Findings

### System at a Glance

| Attribute | Value |
|-----------|-------|
| **System Name** | Allegro PoC |
| **Architecture Pattern** | MVP (Model-View-Presenter) |
| **Language** | Java 22 |
| **Build** | Maven |
| **UI Framework** | Java Swing |
| **Source Files** | 11 Java files |
| **Total LOC** | ~670 |
| **Test Coverage** | 0% |
| **Health Score** | 62 / 100 (Grade: C+) |

### Critical Issues (Fix First)

1. 🔴 **Hardcoded URLs** — `http://localhost:8080` and `ws://localhost:1337/` cannot be configured
2. 🔴 **NullPointerException risk** — null model fields crash on `.toString()` in `PocModel.action()`
3. 🟠 **EDT Blocking** — HTTP call blocks the Swing UI thread (freezes window)
4. 🟠 **Silent exception crash** — `IOException` re-thrown as `RuntimeException` on EDT
5. 🟠 **Duplicate `panel.add(textArea)`** — layout bug in both `PocView` and `websocket/Main`

### Architecture Highlights

- ✅ Clean MVP separation in `com.poc` module
- ✅ Type-safe EnumMap + `ValueModel<T>` data model
- ✅ Custom pub-sub EventEmitter for model-to-presenter notification
- ⚠️ `websocket.Main` is a monolithic God-class (not MVP)
- ⚠️ ~95% duplicate UI code between the two Swing apps
- ⚠️ Zero test coverage

### Business Capabilities

| Domain | Capabilities | Coverage |
|--------|-------------|----------|
| Customer Data Management | 4 | 3 Full, 1 Partial |
| UI / Presentation | 3 | 3 Full |
| Real-Time Communication | 3 | 3 Full |
| Event-Driven State Management | 2 | 2 Full |
| **Input Validation** | — | ❌ GAP |
| **Error Handling UI** | — | ❌ GAP |

---

## 📊 Diagrams Quick Reference

| Diagram | Location |
|---------|----------|
| MVP Class Diagram | [`uml-diagrams.md` §1](./uml-diagrams.md) |
| WebSocket Class Diagram | [`uml-diagrams.md` §2](./uml-diagrams.md) |
| Form Submission Sequence | [`uml-diagrams.md` §4](./uml-diagrams.md) |
| WebSocket Message Sequence | [`uml-diagrams.md` §5](./uml-diagrams.md) |
| Bootstrap Sequence | [`uml-diagrams.md` §6](./uml-diagrams.md) |
| Use Case Diagram | [`uml-diagrams.md` §7](./uml-diagrams.md) |
| Layer Architecture | [`architecture-diagrams.md` §1](./architecture-diagrams.md) |
| Component (MVP) | [`architecture-diagrams.md` §2](./architecture-diagrams.md) |
| System Context | [`architecture-diagrams.md` §4](./architecture-diagrams.md) |
| Data Flow | [`architecture-diagrams.md` §6](./architecture-diagrams.md) |
| Form Submission BPMN | [`bpmn-workflows.md` §WF-001](./bpmn-workflows.md) |
| WebSocket BPMN | [`bpmn-workflows.md` §WF-002](./bpmn-workflows.md) |
