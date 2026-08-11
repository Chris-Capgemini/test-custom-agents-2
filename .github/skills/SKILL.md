---
name: swing-to-angular-validation-migrator
description: Migrate Java Swing validation logic into TypeScript validators that integrate with the target application's validation framework. Converts validation predicates, property-path handling, null checks, and validator classes from Java to TypeScript. Use this whenever converting Java validation rules, Predicate logic, validator implementations, or domain-specific validation patterns from Swing/rich-client Java into Angular/TypeScript applications.
---

# Swing to Angular Validation Migrator

Generate TypeScript validators that fit the target application's existing validation framework and conventions.

## Required inputs

- Java validator class(es) or validation logic
- Target TypeScript validation framework interfaces/contracts (e.g., Validator interface, validation context pattern)
- Validation message keys or localization references from the source
- Any shared helper utilities or validation libraries used by the target project

## Workflow

1. Read [references/implementation.md](references/implementation.md).
2. Study the source validation intent, property paths, and domain terminology.
3. Convert Java validation logic (conditions, predicates, null checks) into TypeScript equivalents.
4. Map validation error messages to the target project's message key system.
5. Integrate with the target project's validation framework (context, global state, or event system).
6. Reuse existing helper utilities and common validation patterns rather than reimplementing.

## Output contract

- Output TypeScript validator(s) implementing the target application's validation interface.
- Preserve source code comments, identifier names, and terminology.
- Use the target project's validation context API and message key system.
- Reuse target project's helper utilities and date/time handling patterns.

## Reference files

- [references/implementation.md](references/implementation.md): validator-migration rules and utility mappings.
