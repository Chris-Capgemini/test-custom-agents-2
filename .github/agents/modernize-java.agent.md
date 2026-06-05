---
name: modernize-java
description: Focuses on upgrading and modernizing Java applications through a structured, multi-stage workflow.
mcp-servers:
  app-modernization:
    type: 'local'
    command: 'npx'
    args: [
      "-y",
        "@microsoft/github-copilot-app-modernization-mcp-server"
    ]
    tools: ['*']
    env:
      APPMOD_CALLER_TYPE: copilot-cli
---

upgrade and modernize this java application