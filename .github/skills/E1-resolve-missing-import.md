# Skill E1: Resolve Missing Import/Module

**When to use:** Compilation or runtime error indicates a module/import cannot be found

**Purpose:** Fix missing dependencies and import references.

## Input

- Error message (exact text)
- File path where error occurred
- Line number (if available)

## Steps

### 1. Parse Error to Identify Missing Module

Extract from error message:
- Module/package name
- Whether it's a file path or package name
- Import statement that failed

**Common error patterns:**
- `Cannot find module 'X'`
- `Module not found: Error: Can't resolve 'X'`
- `No module named 'X'`
- `ModuleNotFoundError: No module named 'X'`
- `TS2307: Cannot find module 'X' or its corresponding type declarations`

### 2. Determine Module Type

**A. Framework/Library (External)**
- Package names: `@angular/core`, `lodash`, `react`, `express`
- No relative path (doesn't start with `.` or `/`)

**B. Custom Module (Internal)**
- Has relative path: `./user.service`, `../models/user`
- Or absolute path: `@app/services/user`

**C. Renamed Module**
- Old name used but module was renamed during migration
- Check migration mapping/strategy

### 3. Handle External Dependencies

**Step 3a: Check if installed**
- Look in `node_modules/` or virtual environment
- Check `package.json` or `requirements.txt`

**Step 3b: Install if missing**

**For Node.js/npm:**
```bash
npm install <package-name>
# or
npm install <package-name> --save-dev  # for dev dependencies
```

**For Python/pip:**
```bash
pip install <package-name>
# or
pip install <package-name> -r requirements.txt
```

**For Java/Maven:**
Add to `pom.xml`:
```xml
<dependency>
    <groupId>group.id</groupId>
    <artifactId>package-name</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Step 3c: Verify installation**
- Check that package appears in dependencies
- Try build again

**Step 3d: Install type definitions (TypeScript)**
If error is about type declarations:
```bash
npm install --save-dev @types/<package-name>
```

### 4. Handle Internal Modules

**Step 4a: Search for file**
- Look in output directory structure
- Check common locations (models, services, components, utils)
- Search by file name

**Step 4b: Update import path**
- If file exists but path is wrong, use **Skill R5** to fix
- Calculate correct relative path
- Update import statement

**Step 4c: Check if renamed**
- Look in migration strategy document
- Check if file was renamed during migration
- Update import to use new name

**Step 4d: Check if not migrated yet**
- If file doesn't exist in output directory
- Check source directory - does it exist there?
- Options:
  1. Migrate the file now (use **Skill R3**)
  2. Create a stub/placeholder
  3. Add to pending migration list

**Step 4e: Check if merged/split**
- File might have been combined with another
- Or split into multiple files
- Update import to reference correct file

### 5. Special Cases

**Case A: Barrel imports**
Source: `import { A, B } from './index';`
- Check if index file exports those
- May need to import directly from individual files

**Case B: Default vs named imports**
Source: `import User from './user';`
Target might need: `import { User } from './user';`
- Check export statement in target file
- Adjust import accordingly

**Case C: Side-effect imports**
Source: `import './polyfills';`
- Ensure file exists
- Check if still needed in target

**Case D: Dynamic imports**
Source: `const module = await import('./dynamic');`
- Ensure path is correct
- Check webpack/bundler config supports dynamic imports

### 6. Rebuild and Verify

After making changes:
1. Save all modified files
2. Run build command
3. Check if error is resolved
4. If error persists, review what was changed

### 7. Document if Can't Resolve

If stuck after 3 attempts:
- Document the issue in `migration-report.md`
- Add TODO comment in code
- Note as "pending" and continue with migration
- Can revisit later

## Output

- Fixed import statement and/or installed dependency
- Error resolved
- Or documented as pending if unresolvable

## Examples

### Example 1: Missing External Package

**Error:**
```
Cannot find module '@angular/material'
```

**Solution:**
```bash
npm install @angular/material @angular/cdk
```

### Example 2: Wrong Internal Path

**Error:**
```
Cannot find module '../models/User'
File: src/services/user.service.ts
```

**Current structure:**
```
src/
  services/
    user.service.ts
  app/
    models/
      user.model.ts
```

**Fix import in user.service.ts:**
```typescript
// Before:
import { User } from '../models/User';

// After:
import { User } from '../app/models/user.model';
```

### Example 3: File Not Yet Migrated

**Error:**
```
Cannot find module './helper'
```

**File doesn't exist yet in output.**

**Solution:**
1. Note: "helper.ts not yet migrated"
2. Add to migration queue
3. Temporarily add stub:
```typescript
// helper.ts - STUB - TODO: Migrate properly
export const helper = () => {
  throw new Error('Not implemented');
};
```

### Example 4: Missing Type Definitions

**Error:**
```
Could not find a declaration file for module 'lodash'
```

**Solution:**
```bash
npm install --save-dev @types/lodash
```
