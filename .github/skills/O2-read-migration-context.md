```markdown
# Skill O2: Read and Validate Migration Context

**When to use:** Any agent that needs to read a prior agent's handoff document before starting its own work.

**Purpose:** Ensure an agent correctly reads, validates, and extracts the information it needs from an upstream agent's output file before proceeding.

---

## When This Skill is Needed

| Agent | Must Read |
|-------|-----------|
| Migration Agent | `output/analysis-report.md` |
| Validation Agent | `output/migration-report.md` |
| Orchestrator (any step) | `output/orchestration-state.md` |

---

## Steps

### 1. Check File Existence

Before reading anything, verify the handoff file exists:

```
Does output/[expected-file].md exist?
```

**If NO:**
- Do NOT proceed with your main task
- Report: "[Your Agent Name] cannot proceed — `output/[filename]` not found. The [producing agent name] must run first."
- Stop.

**If YES:** Continue to Step 2.

### 2. Read the Full File

Read the complete contents of the handoff file. Do not skim — the file may contain critical information in any section.

### 3. Check the Status Footer

Find the `## Status` section at the end of the file.

| Status Value | Meaning | What to Do |
|-------------|---------|-----------|
| `COMPLETE` | All sections present, fully populated | ✅ Proceed normally |
| `PARTIAL — reason: ...` | Some sections incomplete | ⚠️ Read warning, proceed with caution — note gaps |
| `FAILED — reason: ...` | Producing agent encountered a critical error | ❌ Stop. Report failure to orchestrator. |
| Missing / not present | File may be corrupt or truncated | ⚠️ Treat as PARTIAL, note the issue |

### 4. Extract Required Sections

Extract the specific fields your agent needs:

#### For Migration Agent reading `output/analysis-report.md`:

| Field | Section | How to Use |
|-------|---------|-----------|
| Target project name | Section 8 | Name the output directory |
| Setup commands | Section 9 | Initialize target project |
| Migration order | Section 6 | Order in which to migrate files |
| File catalog | Section 4 | Complexity and type per file |
| Type mappings | Section 5 | Apply during code transformation |
| Risk assessment | Section 7 | Be prepared for specific challenges |
| Architecture pattern | Section 3 | Choose correct target structure |

#### For Validation Agent reading `output/migration-report.md`:

| Field | Section | How to Use |
|-------|---------|-----------|
| Target project location | Target Project Location | Where to run build commands |
| Build command | Build Command | First build to run |
| Dev server command | Dev Server Command | Start command for runtime check |
| Migrated files list | Migrated Files | Know what was migrated |
| Best-effort files | Migrated Files (status column) | Focus error search here |
| Notes for Validation Agent | Notes section | Known problem areas |
| Dependencies installed | Dependencies Installed | Verify expected packages |

#### For Orchestrator reading state files:

| Field | File | How to Use |
|-------|------|-----------|
| Current step status | `output/orchestration-state.md` | Know which agent to invoke next |
| Analysis completion | `output/analysis-report.md` → Status | Gate before invoking Migration Agent |
| Migration completion | `output/migration-report.md` → Status | Gate before invoking Validation Agent |
| Validation completion | `output/validation-report.md` → Status | Gate before writing final summary |

### 5. Validate Completeness of Required Fields

For each field you extracted, check:
- Is it populated (not `(pending)` or empty)?
- Does it contain expected content (not just a header)?

**If a required field is missing or empty:**

- Note it explicitly: "⚠️ Context warning: Section [X] is empty in [filename]. Proceeding with best-effort."
- If critical (e.g., migration order missing): note this limits your ability to do ordered work
- Do NOT stop — make reasonable assumptions where possible

### 6. Summarize What You Read

Before starting your main task, output a brief summary of key context extracted:

```
📖 [Agent Name]: Context loaded from [filename]
  - [Key fact 1]
  - [Key fact 2]
  - [Key fact 3]
  - Status: [COMPLETE/PARTIAL/FAILED]
```

Example for Migration Agent:
```
📖 Migration Agent: Context loaded from output/analysis-report.md
  - 34 source files identified (Java Swing application)
  - Target project name: react-app
  - Migration order: 34 files, models first
  - 3 high-risk files noted
  - Status: COMPLETE
```

---

## Handling Edge Cases

### Partial Analysis Report (incomplete type mappings)

If type mappings section is empty:
- Continue migration using generic/conservative type mappings
- Use skill R2 (`multi-agent/skills/R2-map-data-types.md`) as fallback for each type
- Note in your migration report that type mappings were derived independently

### Partial Migration Report (some files missing from list)

If the migrated files list is incomplete:
- Scan `output/{target-app-name}/` directory directly to discover what was actually migrated
- Use discovered files as ground truth
- Note the discrepancy in your validation report

### Missing Section Headers

If a section is missing entirely (not just empty):
- Flag it: "⚠️ Section [N] missing from analysis-report.md"
- Search the rest of the file for the relevant content under alternative headings
- If truly absent, proceed without it and document the assumption made

### Large Files

If the handoff file is very long:
- Read working from top to bottom
- Do NOT skip sections
- Use grep/search to locate specific sections if needed

---

## Quick Reference: What Each Agent Needs

```
Analysis Agent needs from: nothing (reads workspace directly)
  └─ writes: output/analysis-report.md

Migration Agent needs from: output/analysis-report.md
  └─ Must verify: Sections 6(order), 5(types), 8(name), 9(setup), 3(arch)
  └─ writes: output/migration-report.md

Validation Agent needs from: output/migration-report.md
  └─ Must verify: target location, build command, notes section
  └─ writes: output/validation-report.md

Orchestrator needs from: output/*-report.md (all three)
  └─ Must verify: Status field of each
  └─ writes: output/migration-summary.md, output/orchestration-state.md
```
```
