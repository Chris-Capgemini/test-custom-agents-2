# Implementation Guide

## Goal

Convert Java validator classes into TypeScript validators that integrate with the target application's validation framework.

## Core rules

- Implement the target project's validation interface (e.g., `Validator`, `IValidator`, or similar).
- Add validation messages through the target framework's validation context or message collection API.
- Convert Java static property identifiers into plain string property paths (e.g., `FIELD_NAME` → `'fieldName'`).
- Maintain property-path information as string arrays: `['myAttribute']` or `['parent', 'child']`.
- Access TypeScript object fields directly using standard property access and optional chaining.
- Convert `== null` and `!= null` checks into JavaScript truthiness checks with optional chaining (`?.`).
- Preserve class and method comments, maintaining original language/terminology.

## Helper mapping rules

- Use the target project's validation message key system (constants, enums, or i18n keys).
- Convert Java `Predicate` logic into JavaScript arrow functions.
- Reuse existing utilities from the target project (e.g., `DateUtil`, `CommonUtils`, `ValidationUtils`, etc.) instead of reimplementing.
- For date comparisons, use the target project's temporal/date handling library.

## Nested object pattern

- Convert nested Java property access into optional chaining.
- Emit property paths as string arrays reflecting the navigated fields.
- Reference message keys using the target framework's key system.

### Example

```typescript
localContext.propertyPath = ['person', 'birthDate'];

if (!betterTO?.person?.birthDate) {
	globalContext.addError(
		ValidationMessageKeys.MISSING_BIRTH_DATE,
		localContext
	);
}
```

## Output checklist

- Validator implements the target framework's interface correctly.
- Property paths are strings or string arrays, not Java constants.
- Optional chaining protects all nested property access.
- Existing utility methods are reused rather than duplicated.
- Comments and domain terminology are preserved from the source.
