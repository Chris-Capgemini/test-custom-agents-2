```markdown
# Skill O1: Create Agent Handoff Document

**When to use:** Any agent that needs to write a structured output file for the next agent in the pipeline to consume.

**Purpose:** Ensure consistent, machine-readable handoff files that downstream agents can reliably parse and validate.

---

## The Handoff Contract

Each handoff document must satisfy three guarantees:
1. **Existence** — the file is written to `output/` before the producing agent finishes
2. **Structure** — the file has all required sections so the consuming agent can parse it
3. **Status field** — the final line of the file declares whether it is COMPLETE or PARTIAL

---

## Steps

### 1. Choose the Correct Output Path

All handoff files go in `output/`:

| Producing Agent | Output File |
|-----------------|-------------|
| Analysis Agent | `output/analysis-report.md` |
| Migration Agent | `output/migration-report.md` |
| Validation Agent | `output/validation-report.md` |
| Orchestrator | `output/orchestration-state.md`, `output/migration-summary.md` |

**Never write handoff files:**
- ❌ In workspace root
- ❌ In source directories
- ❌ Alongside source code

### 2. Write Required Sections First

Before filling in content, create the file with all section headers:

```markdown
# [Report Name]

## Section 1: [Mandatory Section]
(pending)

## Section 2: [Mandatory Section]
(pending)

...

## Status
PENDING
```

This guarantees the file exists and has the required skeleton even if the agent is interrupted.

### 3. Fill In Each Section Progressively

Replace `(pending)` with actual content as you complete each phase of work.

**Principle:** A partially-complete handoff document is better than no document. Consuming agents can read partial data and decide how to proceed.

### 4. Required Metadata Header

Every handoff document must start with:

```markdown
# [Report Name]

**Producing Agent:** [agent name]
**Date:** [current date/time]
**Source Technology:** [if applicable]
**Target Technology:** [if applicable]
```

### 5. Required Status Footer

Every handoff document must end with exactly one of these:

```markdown
## Status
COMPLETE
```

```markdown
## Status
PARTIAL — reason: [brief explanation of what is missing]
```

```markdown
## Status
FAILED — reason: [what went wrong, what consuming agent should do]
```

### 6. Finalize and Confirm

After writing the file:
1. Re-read the first 10 lines to verify it can be found
2. Confirm the `## Status` section is at the end
3. Report to orchestrator: "✅ [File] written to output/[filename]"

---

## Handoff Document Templates

### Analysis Report Template

```markdown
# Migration Analysis Report

**Producing Agent:** Analysis Agent
**Date:** {date}
**Source Technology:** {SOURCE_TECH}
**Target Technology:** {TARGET_TECH}
**Total Source Files:** {N}

## 1. Workspace Structure
...

## 2. Dependencies
...

## 3. Architecture
...

## 4. File Catalog
...

## 5. Type Mappings
...

## 6. Migration Order
...

## 7. Risk Assessment
...

## 8. Recommended Target Project Name
...

## 9. Setup Commands
...

## Status
COMPLETE
```

### Migration Report Template

```markdown
# Migration Report

**Producing Agent:** Migration Agent
**Date:** {date}
**Source Technology:** {SOURCE_TECH}
**Target Technology:** {TARGET_TECH}

## Target Project Location
`output/{target-app-name}/`

## Build Command
...

## Dev Server Command
...

## Migrated Files
...

## Dependencies Installed
...

## Unmigrated Files
...

## Notes for Validation Agent
...

## Status
COMPLETE
```

### Validation Report Template

```markdown
# Validation Report

**Producing Agent:** Validation Agent
**Date:** {date}
**Target Technology:** {TARGET_TECH}
**Target App Location:** `output/{target-app-name}/`

## Build Status
**Result:** ✅ PASS / ❌ FAIL / ⚠️ PARTIAL

## Runtime Status
**Result:** ✅ STARTS / ❌ FAILS TO START / ⏭️ SKIPPED

## Fixes Applied
...

## Known Issues
...

## Files Needing Manual Review
...

## Final Assessment
**Overall Status:** ✅ SUCCESS / ⚠️ PARTIAL / ❌ FAILED

## Status
COMPLETE
```

---

## Error Scenarios

**If you run out of time before completing a section:**
1. Write what you have in the section
2. Mark incomplete sections with: `⚠️ INCOMPLETE — time limit reached`
3. Set status to: `PARTIAL — reason: time limit, sections {X, Y} incomplete`

**If a critical error occurs:**
1. Still write the handoff document
2. Document the error clearly
3. Set status to: `FAILED — reason: [what happened]`
4. The Orchestrator will read this and decide whether to retry

**Never leave a handoff file missing.** A failed document is better than no document.

---

## Examples

### Example: Analysis Agent finishes successfully

```markdown
# Migration Analysis Report

**Producing Agent:** Analysis Agent
**Date:** 2026-02-26
**Source Technology:** Java Swing
**Target Technology:** React 18
**Total Source Files:** 34

## 1. Workspace Structure
src/
├── Main.java
├── ui/
│   ├── MainFrame.java
│   └── UserPanel.java
└── services/
    └── UserService.java

**Entry Points:**
- `src/Main.java` — application entry point

[... rest of sections ...]

## Status
COMPLETE
```

### Example: Migration Agent with some best-effort files

```markdown
## Status
PARTIAL — reason: 3 files (UserPanel.java, ComplexDialog.java, ReportGenerator.java)
were migrated as best-effort with TODO comments due to complex GUI logic.
All other 31 files migrated completely.
```
```
