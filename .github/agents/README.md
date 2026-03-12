# GenInsights Custom Agents

This folder contains custom GitHub Copilot agents for comprehensive source code analysis and documentation generation. These agents replicate and enhance the functionality of the GenInsights Analyzer tool using GitHub Copilot's agent capabilities.

## Quick Start

### Prerequisites

- VS Code with GitHub Copilot extension installed
- GitHub Copilot subscription (Pro, Pro+, Business, or Enterprise)
- A repository to analyze

### Using the Agents

1. **Open your repository** in VS Code

2. **Copy the agents and skills folders** to your target repository:
   ```
   Copy .github/agents/ to your-repo/.github/agents/
   Copy .github/skills/ to your-repo/.github/skills/
   ```

3. **Open GitHub Copilot Chat** (Ctrl+Shift+I or Cmd+Shift+I)

4. **Select the orchestrator agent** from the agents dropdown at the bottom of the chat

5. **Start your analysis** by typing:
   - "Run a full analysis" - Complete analysis with all agents
   - "Analyze the business rules" - Focus on business logic
   - "Review code quality" - Focus on code assessment
   - "Generate architecture documentation" - Focus on architecture

## Available Skills

Skills are reusable capabilities that agents can use. They are stored in `.github/skills/`:

| Skill | Purpose |
|-------|---------|
| `discover-files` | Discovers and categorizes all relevant files (source, config, DB, test, docs). Includes shell scripts for deterministic file discovery. |
| `geninsights-logging` | Provides standardized logging format for START/PROGRESS/COMPLETED entries |
| `mermaid-diagrams` | Reference for correct Mermaid syntax for all diagram types |
| `arc42-template` | Complete arc42 template structure and guidance |
| `json-output-schemas` | JSON schemas for all intermediate analysis files |

### File Discovery Scripts

The `discover-files` skill includes shell scripts for reliable file discovery:

**On Windows (PowerShell):**
```powershell
.\.github\skills\discover-files\discover.ps1
```

**On Linux/Mac (Bash):**
```bash
./.github/skills/discover-files/discover.sh
```

These scripts output a categorized JSON file at `.geninsights/discovered_files.json`.

## Available Agents

### Orchestrator Agent
**File:** `orchestrator-agent.agent.md`

The master coordinator that orchestrates all other agents. Use this agent when you want a comprehensive, multi-step analysis.

**Commands:**
- "Run full analysis" - Executes all agents in sequence
- "Analyze business rules" - Focuses on business logic extraction
- "Review code quality" - Focuses on code assessment
- "Generate architecture docs" - Focuses on architecture visualization

### Specialized Agents

| Agent | File | Purpose |
|-------|------|---------|
| **Documentor** | `documentor-agent.agent.md` | Analyzes source files, extracts summaries, business logic, and method details |
| **Business Rules** | `business-rules-agent.agent.md` | Extracts business rules, validations, and domain logic |
| **Code Assessment** | `code-assessment-agent.agent.md` | Reviews code quality, identifies issues and technical debt |
| **UML** | `uml-agent.agent.md` | Generates class, sequence, and use case diagrams |
| **BPMN** | `bpmn-agent.agent.md` | Creates business process workflow diagrams |
| **Architecture** | `architecture-agent.agent.md` | Creates component and layer architecture diagrams |
| **Capability Mapping** | `capability-mapping-agent.agent.md` | Maps code to business capabilities |
| **Arc42** | `arc42-agent.agent.md` | Synthesizes all outputs into arc42 architecture documentation |

### All-in-One Agent
**File:** `geninsights-all-in-one.agent.md`

A standalone agent that combines all capabilities into a single agent without requiring orchestration. Use this when you want a simpler, single-agent experience.

## Output Structure

All agents write their outputs to a `.geninsights/` folder:

```
.geninsights/
├── agent-work-log.md           # Activity log from all agents
├── analysis/
│   ├── analysis_results.json   # File-by-file analysis
│   ├── business_rules.json     # Extracted business rules
│   ├── code_assessment.json    # Code quality review
│   ├── architecture_analysis.json
│   ├── capability_mapping.json
│   ├── uml_analysis.json
│   └── bpmn_workflows.json
├── docs/
│   ├── file-analysis-summary.md
│   ├── business-rules.md
│   ├── code-assessment-report.md
│   ├── uml-diagrams.md
│   ├── bpmn-workflows.md
│   ├── architecture-diagrams.md
│   └── capability-mapping.md
└── arc42/
    └── architecture-documentation.md
```

## Diagram Format

All diagrams are generated using **Mermaid** syntax embedded in Markdown. This ensures:
- Diagrams render directly in GitHub, VS Code, and most Markdown viewers
- No external tools required (no PlantUML server needed)
- Easy to edit and version control

### Supported Diagram Types

- **Class Diagrams** - Show classes, attributes, methods, and relationships
- **Sequence Diagrams** - Illustrate method calls and interactions over time
- **Flowcharts/BPMN** - Visualize business processes and workflows
- **Component Diagrams** - Show system architecture and dependencies
- **Mind Maps** - Display hierarchical relationships (e.g., capabilities)

## Language Support

These agents can analyze code in **any programming language**, including:
- Java, Kotlin, Scala
- C#, VB.NET, F#
- Python
- JavaScript, TypeScript
- Go
- Ruby, PHP
- C, C++
- Rust
- Swift
- And more...

No configuration is required - the agents automatically detect and adapt to the programming language.

## Typical Workflows

### Complete Documentation Generation

1. Select `orchestrator-agent`
2. Say: "Run a full analysis of this codebase"
3. Wait for all agents to complete
4. Review outputs in `.geninsights/` folder

### Business Rules Focus

1. Select `orchestrator-agent`
2. Say: "I need to understand the business logic and rules in this codebase"
3. Review `.geninsights/docs/business-rules.md` and `.geninsights/docs/bpmn-workflows.md`

### Code Quality Review

1. Select `orchestrator-agent`
2. Say: "Review the code quality and identify technical debt"
3. Review `.geninsights/docs/code-assessment-report.md`

### Quick Analysis (No Orchestration)

1. Select `geninsights-all-in-one` from the standalone folder
2. Say: "Analyze this codebase and create documentation"
3. The single agent handles everything

## Agent Work Log

All agents (except the orchestrator) log their activities to `.geninsights/agent-work-log.md`. This provides:
- Timestamp of each action
- Which agent performed the action
- What was analyzed or generated
- Summary of outputs created

Example log entry:
```markdown
## 2026-02-05T15:23:11Z - documentor-agent

**Action:** Code Analysis
**Files Analyzed:** 42 files
**Languages Detected:** Java, TypeScript
**Categories:** 28 Business, 10 Technical, 4 Mixed
**Output Files:**
- `.geninsights/analysis/analysis_results.json`
- `.geninsights/docs/file-analysis-summary.md`

---
```

## Customization

### Modifying Agent Behavior

Each agent is defined in a Markdown file with YAML frontmatter. You can customize:

1. **Description** - Change what the agent says it does
2. **Tools** - Add or remove tool access (`read`, `edit`, `search`, `agent`)
3. **Prompt content** - Modify the detailed instructions

### Adding New Agents

Create a new file in `.github/agents/` with the format:

```markdown
---
name: my-custom-agent
description: What this agent does
tools: ["read", "edit", "search"]
---

Your detailed agent instructions here...
```

### Adding New Skills

Create a new folder in `.github/skills/` with a `SKILL.md` file:

```markdown
---
name: my-custom-skill
description: What this skill does and when to use it
---

# My Skill Name

Instructions and reference material...
```

Skills can also include:
- Shell scripts (`.sh`, `.ps1`) for deterministic operations
- Example files and templates
- Reference documentation

### Integrating with Existing Workflows

The JSON outputs can be consumed by other tools or scripts. The structure is documented in each agent's instructions and follows consistent patterns.

## Troubleshooting

### Agent Not Appearing in Dropdown

- Ensure the file is in `.github/agents/` folder
- Check that the file has the `.agent.md` extension
- Verify the YAML frontmatter is valid
- Refresh VS Code or reopen the repository

### Diagrams Not Rendering

- Ensure Mermaid code blocks are properly formatted
- Check for syntax errors in the Mermaid code
- Try previewing in GitHub (web) for better Mermaid support

### Large Repositories

For very large codebases:
- Consider analyzing specific folders first
- Use focused analysis modes rather than full analysis
- The agents will try to prioritize important files

## Comparison with Original GenInsights Analyzer

| Feature | Original Analyzer | Custom Agents |
|---------|-------------------|---------------|
| Languages | Limited set | Any language |
| Configuration | Config files required | No configuration |
| LLM Setup | Manual API key config | Uses Copilot (no config) |
| Output Formats | JSON, PDF, HTML, AsciiDoc | JSON, Markdown |
| Diagrams | PlantUML | Mermaid |
| Execution | Python script | VS Code Copilot |
| Orchestration | Python orchestrator | Agent tool or all-in-one |

## Contributing

To improve these agents:
1. Test with different codebases
2. Note any issues or improvements
3. Update the agent instructions
4. Submit changes for review

---

*These agents are designed to work with GitHub Copilot in VS Code. They leverage the power of AI to understand and document codebases without requiring manual configuration or external tools.*
