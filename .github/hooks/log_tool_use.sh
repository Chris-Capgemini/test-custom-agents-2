#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
LOG_FILE="$ROOT_DIR/hook_tools_used.log"

if command -v python3 >/dev/null 2>&1; then
  python3 - "$LOG_FILE" <<'PY'
import json
import sys
from datetime import datetime, timezone

log_file = sys.argv[1]
raw = sys.stdin.read().strip()
ts = datetime.now(timezone.utc).isoformat(timespec="seconds")


def to_text(value):
    if value is None:
        return ""
    if isinstance(value, str):
        return value.replace("\n", " ").strip()
    if isinstance(value, (int, float, bool)):
        return str(value)
    return json.dumps(value, ensure_ascii=True, separators=(",", ":"))


def get_path(data, path):
    cur = data
    for key in path:
        if isinstance(cur, dict) and key in cur:
            cur = cur[key]
        else:
            return None
    return cur


def first_value(data, candidates):
    for path in candidates:
        val = get_path(data, path)
        if val not in (None, ""):
            return to_text(val)
    return "unknown"


def flatten(obj, prefix="", out=None):
    if out is None:
        out = []
    if isinstance(obj, dict):
        for key, value in obj.items():
            child = f"{prefix}.{key}" if prefix else str(key)
            flatten(value, child, out)
    elif isinstance(obj, list):
        for idx, value in enumerate(obj):
            child = f"{prefix}[{idx}]"
            flatten(value, child, out)
    else:
        out.append((prefix, obj))
    return out

payload = {}
if raw:
    try:
        payload = json.loads(raw)
    except Exception:
        payload = {"raw": raw}

agent_name = first_value(payload, [
    ["agentName"],
    ["agent", "name"],
    ["sourceAgent", "name"],
    ["context", "agent", "name"],
    ["session", "agent", "name"],
])

tool_name = first_value(payload, [
    ["toolName"],
    ["tool", "name"],
    ["toolUse", "name"],
    ["toolInvocation", "name"],
    ["context", "tool", "name"],
])

payload_text = json.dumps(payload, ensure_ascii=True)
lower_tool = tool_name.lower()
is_subagent_invocation = (
    "subagent" in lower_tool
    or lower_tool in {"task", "runsubagent", "search_subagent"}
    or any(token in payload_text.lower() for token in ["runsubagent", "search_subagent", "agentname", "subagent"])
)

line = f"{ts} | agent={agent_name} | tool={tool_name}"

if is_subagent_invocation:
    detail_candidates = [
        ["toolInput", "prompt"],
        ["toolInvocation", "arguments", "prompt"],
        ["arguments", "prompt"],
        ["prompt"],
        ["toolInput", "query"],
        ["query"],
        ["toolInput", "description"],
        ["description"],
        ["toolInput", "agentName"],
        ["agentName"],
        ["toolInput", "model"],
        ["model"],
    ]

    details = []
    for path in detail_candidates:
        value = get_path(payload, path)
        if value not in (None, ""):
            key = ".".join(path)
            details.append(f"{key}={to_text(value)}")

    token_keywords = ("token", "context", "window", "budget", "usage", "reserve", "remaining", "max")
    token_entries = []
    for key, value in flatten(payload):
        lowered = key.lower()
        if any(term in lowered for term in token_keywords):
            rendered = to_text(value)
            if rendered:
                token_entries.append(f"{key}={rendered}")

    if not details:
        details.append("details=not-available")
    if not token_entries:
        token_entries.append("token_context=not-available")

    line = (
        f"{line}"
        f" | details=[{' ; '.join(details)}]"
        f" | token_context=[{' ; '.join(token_entries)}]"
    )

with open(log_file, "a", encoding="utf-8") as handle:
    handle.write(line + "\n")
PY
else
  TS="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  printf '%s | agent=unknown | tool=unknown | details=python3-not-found\n' "$TS" >> "$LOG_FILE"
fi
