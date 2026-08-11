# Static Mockup Guide

## Goal

Convert a UI layout or specification into static HTML5 markup suitable for design prototyping or later integration into a framework.

## Rules

- Output pure HTML5 with the target CSS framework (Bootstrap, Tailwind, Material, or other).
- Do not include Angular bindings, template expressions, JSX, or other framework-specific syntax.
- Do not wrap output in `html` or `body` tags; return the component fragment.
- Preserve the source layout hierarchy and visual structure.
- Map source UI components to semantic HTML5 elements and target CSS framework classes.
- Translate data-display components (tables, lists, grids) into static HTML with representative example data.
- Translate navigation/tab components into semantic structures using the target CSS framework's patterns.
- Translate form components into `<form>`, `<input>`, `<select>`, etc. without bindings or validation scripts.
- Use realistic placeholder data instead of empty fields.
- Capture meaningful source behavior (e.g., visible/hidden sections) as static HTML when feasible—avoid over-engineering.

## No bindings rule

This output is static text and markup only. Do not use:
- Angular syntax (`{{ }}`, `*ngIf`, `[property]`, `(event)`, etc.)
- Vue/React syntax
- Handlebars or other template syntax
- JavaScript event handlers like `onclick`
- Form validation JavaScript

## Framework-specific patterns

- For Bootstrap-based mockups, use Bootstrap grid classes and component patterns.
- For Tailwind-based mockups, use Tailwind utility classes.
- For Material-based mockups, use semantic material component structure.
- Adapt to whichever CSS framework/component library the target project uses.
