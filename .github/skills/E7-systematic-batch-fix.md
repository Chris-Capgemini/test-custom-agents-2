# Skill E7: Systematic Error Batch Fix

**When to use:** After build, multiple errors appear that need organized resolution

**Purpose:** Efficiently handle many errors by grouping and prioritizing fixes.

## Input

- Complete build output with all errors
- Error log or error list
- File paths and line numbers

## Steps

### 1. Collect All Errors

**Capture complete error list:**
- Run build and save full output
- Count total number of errors
- Don't stop at first error - need full picture

**Organize errors:**
```
Total Errors: 47

By File:
- src/services/user.service.ts: 8 errors
- src/models/user.model.ts: 3 errors
- src/components/dashboard.component.ts: 12 errors
...
```

### 2. Group Errors by Type/Pattern

**Categorize by error type:**

**Import/Module Errors:**
- `Cannot find module 'X'`
- `Module not found`
- Count: X errors

**Type Errors:**
- `Type 'X' is not assignable to type 'Y'`
- `Property 'X' does not exist on type 'Y'`
- Count: Y errors

**Syntax Errors:**
- `Unexpected token`
- `';' expected`
- Count: Z errors

**Configuration Errors:**
- Build config issues
- Missing files
- Count: W errors

**Example grouping:**
```markdown
## Error Groups

### Group 1: Missing Imports (15 errors)
- Cannot find module '@angular/core' (3 occurrences)
- Cannot find module './user.model' (8 occurrences)
- Cannot find module 'rxjs/operators' (4 occurrences)

### Group 2: Type Mismatches (12 errors)
- Type 'null' not assignable to 'User' (5 occurrences)
- Property 'id' does not exist on type '{}' (7 occurrences)

### Group 3: Undefined Methods (8 errors)
- Property 'size' does not exist on type 'any[]' (8 occurrences)

### Group 4: Missing Properties (6 errors)
- Property 'email' missing in type (6 occurrences)

### Group 5: Configuration (6 errors)
- Cannot find file 'main.ts' (1 occurrence)
- Missing tsconfig option (5 occurrences)
```

### 3. Prioritize Error Groups

**Priority 1: Blocking Errors (fix first)**
- Configuration errors that prevent build from starting
- Missing files/modules that cause cascade failures
- Syntax errors in commonly imported files

**Priority 2: High-Frequency Errors (big impact)**
- Same error repeated many times
- Fixing once solves many instances
- Errors in base classes/services

**Priority 3: Low-Complexity Errors (quick wins)**
- Simple fixes (e.g., adding missing properties)
- Type annotations
- Import path corrections

**Priority 4: Complex Errors (fix last)**
- Require architectural changes
- Need research
- Low frequency but complex

**Prioritized plan:**
```markdown
## Fix Order

**Round 1: Configuration (Priority 1)**
1. Fix tsconfig.json
2. Add missing main.ts
Estimated fixes: 6 errors → 41 remaining

**Round 2: Missing Imports Framework (Priority 1+2)**
1. Install @angular/core
2. Install rxjs
Estimated fixes: 7 errors → 34 remaining

**Round 3: Internal Import Paths (Priority 2)**
1. Fix relative paths to models
2. Fix service import paths
Estimated fixes: 8 errors → 26 remaining

**Round 4: Collection Methods (Priority 2+3)**
1. Replace .size() with .length globally
Estimated fixes: 8 errors → 18 remaining

**Round 5: Type Annotations (Priority 3)**
1. Add  | null to nullable types
2. Add missing interface properties
Estimated fixes: 11 errors → 7 remaining

**Round 6: Complex Issues (Priority 4)**
1. Manually review remaining 7 errors
```

### 4. Fix Each Group Systematically

For each group in priority order:

**Step 4a: Select appropriate skill**
- Missing import → **Skill E1**
- Type mismatch → **Skill E2**
- Undefined property → **Skill E3**
- Build config → **Skill E4**
- Dependency conflict → **Skill E5**
- Missing feature → **Skill E6**

**Step 4b: Apply fix to all instances**
- Fix one instance completely
- Apply same fix pattern to all similar errors
- Use find-and-replace for identical issues

**Step 4c: Rebuild after each group**
```bash
npm run build
```

**Step 4d: Verify errors are resolved**
- Check error count decreased
- Note any new errors that appeared
- Confirm expected fixes worked

**Step 4e: Update error tracking**
```markdown
✅ Round 1 Complete: 6 errors fixed
   Remaining: 41 errors

✅ Round 2 Complete: 7 errors fixed
   Remaining: 34 errors
```

### 5. Handle Cascade Effects

**Watch for:**
- Fixing one error reveals new errors in dependent files
- Error count may go up before going down
- New error types may appear

**If error count increases:**
- Don't panic - this is normal
- New errors are often related to same root cause
- Group new errors and prioritize

**Example cascade:**
```
Before: 20 import errors (imports commented out)
Fix: Uncomment imports
After: 20 import errors gone, but 15 new type errors appear
Reason: Types are now checked but weren't before
```

### 6. Deal with Stubborn Errors

**If stuck after 3 attempts:**

**Document the error:**
```typescript
// TODO: Fix this error - type mismatch
// Error: Type 'X' is not assignable to 'Y'
// Attempted fixes: tried casting, tried changing type
// Need to research proper solution
const value: any = problematicValue;  // Temporary workaround
```

**Add to tracking:**
```markdown
## Known Issues (Deferred)

1. **File:** user.service.ts:45
   **Error:** Complex type inference issue
   **Status:** Deferred - application builds and runs
   **Workaround:** Using 'any' type temporarily
   **Plan:** Research proper typing after core migration complete
```

**Move on:**
- Don't let one error block entire migration
- Fix others first
- Come back to difficult ones later
- Sometimes fixing other errors makes stubborn ones clear

### 7. Final Cleanup

When error count is low (< 5):

**Review each remaining error individually:**
- Read error message carefully
- Understand root cause
- Apply targeted fix
- Test fix works

**Run final build:**
```bash
npm run build --verbose
```

**Verify:**
- ✅ 0 errors
- ✅ Build succeeds
- ✅ Output files generated
- ✅ No warnings (or document acceptable warnings)

### 8. Report Progress

Throughout process, provide updates:

```markdown
🔨 **Error Fixing Progress**

Initial errors: 47

Round 1 (Config):        47 → 41 (6 fixed)
Round 2 (Framework):     41 → 34 (7 fixed)
Round 3 (Imports):       34 → 26 (8 fixed)
Round 4 (Collections):   26 → 18 (8 fixed)
Round 5 (Types):         18 → 7 (11 fixed)
Round 6 (Manual):        7 → 0 (7 fixed)

✅ **All errors resolved!**
   Total fixed: 47 errors
   Build: SUCCESS
```

## Output

- All resolvable errors fixed
- Application builds successfully
- Documented any deferred issues
- Progress tracked and reported

## Example Workflow

### Initial State
```
npm run build

ERROR in src/services/user.service.ts:1:31
Cannot find module '@angular/core'

ERROR in src/services/user.service.ts:2:24
Cannot find module 'rxjs/operators'

ERROR in src/services/user.service.ts:15:5
Property 'size' does not exist on type 'any[]'

[... 44 more errors ...]

Total: 47 errors
```

### Group & Prioritize
```markdown
1. Missing @angular/core (3 files) - Priority 1
2. Missing rxjs (4 files) - Priority 1
3. Collection .size() → .length (8 instances) - Priority 2
4. Type mismatches (32 instances) - Priority 3
```

### Fix Round 1
```bash
npm install @angular/core @angular/common
npm install rxjs

npm run build
# Now: 40 errors (7 fixed)
```

### Fix Round 2
```typescript
// Find all: .size()
// Replace: .length
# Now: 32 errors (8 fixed)
```

### Fix Round 3
```typescript
// Fix type annotations one by one
# Now: 0 errors (32 fixed)
```

### Success
```
✅ Build successful!
   Fixed: 47 errors
   Time: 45 minutes
```
