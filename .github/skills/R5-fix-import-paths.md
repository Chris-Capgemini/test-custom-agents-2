# Skill R5: Fix Import Paths After Restructure

**When to use:** After moving files to new directory structure, imports need updating

**Purpose:** Update all import statements to reflect new file locations.

## Input

- File path (current location)
- Old structure (where files used to be)
- New structure (where files are now)
- Migration mapping (old path → new path)

## Steps

### 1. Read All Import Statements

Scan the file for:
- ES6 imports: `import { X } from './path';`
- CommonJS: `const X = require('./path');`
- TypeScript: `import type { X } from './path';`
- CSS/Asset imports: `import './styles.css';`

### 2. Categorize Each Import

For each import, determine:

**A. Framework/External Library Import**
- Examples: `'@angular/core'`, `'react'`, `'lodash'`
- Action: Verify package exists in target, usually no path change needed

**B. Relative Import (Internal)**
- Examples: `'./user.service'`, `'../models/user'`
- Action: Needs path recalculation

**C. Absolute Import (Internal)**
- Examples: `'@app/services/user'`, `'src/models/user'`
- Action: Verify path alias configuration, may need update

### 3. For Each Relative Import

**Step 3a: Identify what is being imported**
- Parse import statement
- Extract module/file name
- Determine type (model, service, component, util)

**Step 3b: Look up new location**
- Check migration mapping/strategy document
- Search output directory structure for the file
- Common patterns:
  - Models: `src/models/` or `src/app/models/`
  - Services: `src/services/` or `src/app/services/`
  - Components: `src/components/` or `src/app/components/`
  - Utils: `src/utils/` or `src/app/utils/`

**Step 3c: Calculate new relative path**

From current file: `output/app/services/auth.service.ts`
To imported file: `output/app/models/user.model.ts`

Steps:
1. Get current file directory: `output/app/services/`
2. Get target file directory: `output/app/models/`
3. Calculate relative path: `../models/user.model`

**Formula:**
- Count directory levels up: `../` for each level
- Add path down to target: `models/user.model`

**Examples:**
- Same directory: `./user.model`
- Parent directory: `../user.model`
- Sibling directory: `../models/user.model`
- Two levels up: `../../shared/utils/helper`

**Step 3d: Update import statement**

Before:
```typescript
import { User } from '../model/User';
```

After:
```typescript
import { User } from '../models/user.model';
```

### 4. Handle Path Aliases

If target uses path aliases (tsconfig paths):

```json
{
  "paths": {
    "@app/*": ["src/app/*"],
    "@models/*": ["src/app/models/*"]
  }
}
```

Can use:
```typescript
import { User } from '@models/user.model';
```

Instead of:
```typescript
import { User } from '../../models/user.model';
```

### 5. Verify Imported Symbols Still Match

Check that:
- Exported name hasn't changed
- File still exports what's being imported
- Named exports vs default exports are correct

### 6. Handle Special Cases

**Case A: File not yet migrated**
- Add a TODO comment
- Create a stub/placeholder file
- Document in tracking file

**Case B: File was merged into another**
- Update import to new combined file
- Import correct symbol

**Case C: File was split into multiple**
- Add multiple import statements
- Import from correct file

**Case D: Import removed (no longer needed)**
- Delete the import statement

### 7. Update Extension if Needed

Source might not use extensions, target might require them:
- `'./user'` → `'./user.js'` (if required)
- Check target's module resolution settings

## Output

File with all import paths correctly updated.

## Example

### Before Migration:
```typescript
// File: src/services/UserService.ts
import { User } from '../models/User';
import { ApiClient } from '../api/ApiClient';
import { formatDate } from '../utils/formatters';
```

### After Restructure to:
```
output/app/
  services/
    user.service.ts
  models/
    user.model.ts
  core/
    api-client.service.ts
  shared/
    utils/
      formatters.ts
```

### Fixed Imports:
```typescript
// File: output/app/services/user.service.ts
import { User } from '../models/user.model';
import { ApiClient } from '../core/api-client.service';
import { formatDate } from '../shared/utils/formatters';
```

## Quick Reference: Relative Path Calculation

```
Current: a/b/c/file1.ts
Target:  a/b/d/file2.ts
Path:    ../d/file2

Current: a/b/c/file1.ts
Target:  a/d/file2.ts
Path:    ../../d/file2

Current: a/b/c/file1.ts
Target:  a/b/c/d/file2.ts
Path:    ./d/file2
```
