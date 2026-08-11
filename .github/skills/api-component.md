# API-Backed Component Guide

## Goal

Generate a complete Angular component and template that fetches and displays data from backend services.

## Rules

- Only generate or adapt `{component}.component.ts` and `{component}.component.html`.
- Keep naming conventions for components, methods, and data models from the target project.
- Use `.scss` in the component's `styleUrls` (or `.css` if that's the target project's convention).
- Import services from the target project's API/service package.
- Import data models from the target project's models/DTO package.
- Use `@Input()` with complete data models/DTOs, not decomposed primitive fields.
- Use `@Output()` for outbound events that communicate with parent components.
- Pass complex models to child components via route parameters and handle the missing-data case.
- Subscribe to route or input parameters in `ngOnInit`.
- Initialize component properties with field initializers or safe defaults.
- Replace any dummy data or placeholders with real service calls.
- Keep code type-safe and fully implemented.
- Bind component state to HTML template properties and event handlers.
- Use reactive forms for screens that contain input forms.
- Build reactive forms explicitly with Angular's `FormBuilder`, `FormGroup`, or `FormControl` (not implicit object state).
- Bind form validation state to the template so actions can be enabled/disabled based on form validity.
- Add loading state and error handling for async operations.
- Handle TypeScript strict mode with default values and proper typing instead of unsafe type assertions.

## Reactive form pattern

- Initialize the form before wiring subscriptions.
- Keep service subscriptions scoped to the component lifecycle and unsubscribe on destroy.
- Use route or input data to patch form values instead of bypassing the form model.
- For live-search screens, debounce rapid input changes with `debounceTime()` to avoid excessive backend requests.

### Example shape

```typescript
searchForm = this.formBuilder.group({
	customerNumber: [''],
	changedAfter: [null],
});

isLoading = false;
errorMessage = '';
results: CustomerDto[] = [];
```

### Example flow

```typescript
ngOnInit(): void {
	this.activatedRoute.paramMap.subscribe(() => {
		this.searchForm.patchValue({ customerNumber: '' });
	});
}

search(): void {
	if (this.searchForm.invalid) {
		return;
	}

	this.isLoading = true;
	this.errorMessage = '';

	this.customerService.searchCustomers(this.searchForm.getRawValue()).subscribe({
		next: (results) => {
			this.results = results;
			this.isLoading = false;
		},
		error: () => {
			this.errorMessage = 'Search failed';
			this.isLoading = false;
		},
	});
}
```

### Template expectations

- Bind the form with `[formGroup]="searchForm"`.
- Bind form controls with `formControlName`.
- Disable buttons when `searchForm.invalid || isLoading`.
- Render validation messages and service errors inline on the form.
- Bind result rows from component state in the template (do not compute or transform in the template).

## Search and filter screens

- If the source behavior suggests live/incremental filtering, use `debounceTime()` to prevent rapid backend requests per keystroke.
- Preserve explicit button-driven search if the source behaves that way (do not convert to auto-search without being asked).
