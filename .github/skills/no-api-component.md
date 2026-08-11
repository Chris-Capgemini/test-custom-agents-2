# No-API Component Guide

## Goal

Generate a complete Angular component when all data comes from inputs or routing and the component should not call APIs directly.

## Rules

# No-API Component Guide

## Goal

Generate an Angular component when all data arrives through `@Input()` properties or route parameters and the component should NOT call backend services.

## Rules

- Keep component naming and data model naming conventions consistent with the target project.
- Use `.scss` or `.css` for styling (match the target project's convention).
- Import data models from the target project's models/DTOs package.
- Accept data via `@Input()` decorated properties using complete DTO/model objects.
- Pass models to child or routed components through route parameters or component props.
- Guard against missing or null input models gracefully.
- Subscribe to route changes or input property changes in `ngOnInit`.
- Keep code type-safe with proper TypeScript typing.
- Implement all HTML elements; remove placeholder or example data.
- Initialize component properties safely with defaults where needed.
- Do not add any backend API calls or service injections unless explicitly requested.
