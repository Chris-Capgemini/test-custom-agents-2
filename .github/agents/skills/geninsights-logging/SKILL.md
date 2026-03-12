---
name: geninsights-logging
description: Provides standardized logging format for GenInsights agents. Use this skill when logging analysis progress to the agent-work-log.md file. Ensures consistent START, PROGRESS, and COMPLETED entries across all agents.
---

# GenInsights Logging Skill

This skill provides a consistent logging format for all GenInsights analysis agents.

## Log File Location

All agents log to: `.geninsights/agent-work-log.md`

## Log Entry Types

### 1. STARTED Entry

Log when an agent begins its work:

```markdown
## [TIMESTAMP] - [agent-name] - STARTED

**Action:** [What is being started]
**Status:** 🔄 In Progress
**Skills Used:** [List skills being used, if any]

---
```

**Example:**
```markdown
## 2026-02-05 10:15:32 - documentor-agent - STARTED

**Action:** Starting code analysis
**Status:** 🔄 In Progress
**Skills Used:** discover-files, geninsights-logging

---
```

### 2. PROGRESS Entry

Log when completing significant milestones:

```markdown
## [TIMESTAMP] - [agent-name] - PROGRESS

**Milestone:** [Description of what was completed]
**Details:** [Specific findings or counts]
**Progress:** [X of Y items processed, or percentage]
**Skill Applied:** [If a skill was used for this step, mention it]

---
```

**Examples:**
```markdown
## 2026-02-05 10:18:42 - documentor-agent - PROGRESS

**Milestone:** Completed Java services analysis
**Details:** Analyzed 15 files in src/main/java/services/
**Progress:** 15 of 47 files processed
**Skill Applied:** discover-files (used file list from discovered_files.json)

---
```

```markdown
## 2026-02-05 10:28:15 - business-rules-agent - PROGRESS

**Milestone:** Extracted rules from OrderService
**Details:** Found 8 validation rules, 3 calculation formulas, 2 authorization checks
**Progress:** 13 rules found so far

---
```

```markdown
## 2026-02-05 10:38:50 - code-assessment-agent - PROGRESS

**Milestone:** Found critical security issue
**Details:** SQL injection vulnerability in UserRepository.findByName()
**Progress:** 12 of 47 files reviewed, 1 critical issue

---
```

### 3. COMPLETED Entry

Log when an agent finishes its work:

```markdown
## [TIMESTAMP] - [agent-name] - COMPLETED

**Action:** [What was completed]
**Status:** ✅ Finished
**Files Processed:** [Count]
**Output Files:** [List of files created]
**Skills Used:** [List all skills that were used during this task]

---
```

**Example:**
```markdown
## 2026-02-05 10:25:03 - documentor-agent - COMPLETED

**Action:** Code Analysis Complete
**Status:** ✅ Finished
**Files Processed:** 47
**Output Files:** analysis/analysis_results.json, docs/file-analysis-summary.md
**Skills Used:** discover-files, geninsights-logging, json-output-schemas

---
```

## Skill Usage Logging

**IMPORTANT:** Always log which skills you used during your work. This helps with:
- Traceability of how analysis was performed
- Understanding which skills are most valuable
- Debugging issues with specific skills

### When to Log Skill Usage

| Situation | Where to Log |
|-----------|--------------|
| Starting work with skills | In the STARTED entry under "Skills Used" |
| Using a skill for a specific step | In the PROGRESS entry under "Skill Applied" |
| Completing work | In the COMPLETED entry, list all skills used |

### Example Skill Usage in Log

```markdown
## 2026-02-05 10:15:32 - uml-agent - STARTED

**Action:** Starting UML diagram generation
**Status:** 🔄 In Progress
**Skills Used:** discover-files, mermaid-diagrams, json-output-schemas

---

## 2026-02-05 10:22:45 - uml-agent - PROGRESS

**Milestone:** Generated class diagram for domain layer
**Details:** Created diagram with 8 classes, 12 relationships
**Progress:** 1 of 3 diagram types completed
**Skill Applied:** mermaid-diagrams (used classDiagram syntax reference)

---

## 2026-02-05 10:35:00 - uml-agent - COMPLETED

**Action:** UML Diagram Generation Complete
**Status:** ✅ Finished
**Diagrams Created:** 5 (2 class, 2 sequence, 1 use case)
**Output Files:** docs/uml-diagrams.md, analysis/uml_analysis.json
**Skills Used:** discover-files, mermaid-diagrams, json-output-schemas

---
```

## When to Log PROGRESS

Log progress entries when:

| Agent | Log Progress When |
|-------|-------------------|
| documentor | Completing a major folder/package, every 10-20 files |
| business-rules | Completing a business domain, every 5-10 rules, finding workflows |
| code-assessment | Finding critical issues, completing review phases, every 10-15 files |
| uml | Completing each diagram type (class, sequence, use case) |
| bpmn | Completing each major workflow |
| architecture | Identifying architecture style, completing layer analysis, finding patterns |
| capability-mapping | Completing a business domain, every 10-15 files mapped |
| arc42 | Completing groups of sections (1-4, 5-7, 8-12) |

## Timestamp Format

Use ISO 8601 format or human-readable:
- `2026-02-05T10:15:32Z` (ISO)
- `2026-02-05 10:15:32` (readable)

## Log File Structure

The log file should have a header:

```markdown
# GenInsights Agent Work Log

---

[Log entries here, newest at bottom]
```

## Quick Templates

Copy and customize these templates:

**START:**
```markdown
## [TIMESTAMP] - [AGENT] - STARTED

**Action:** Starting [action]
**Status:** 🔄 In Progress

---
```

**PROGRESS:**
```markdown
## [TIMESTAMP] - [AGENT] - PROGRESS

**Milestone:** [what completed]
**Details:** [specifics]
**Progress:** [X of Y]

---
```

**COMPLETED:**
```markdown
## [TIMESTAMP] - [AGENT] - COMPLETED

**Action:** [action] Complete
**Status:** ✅ Finished
**Files Processed:** [N]
**Output Files:** [list]

---
```
