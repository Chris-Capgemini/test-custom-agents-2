# Skill E4: Resolve Build Configuration Error

**When to use:** Build process fails due to configuration issues

**Purpose:** Fix build system configuration to enable successful compilation.

## Input

- Build error output
- Build tool being used (webpack, tsc, Angular CLI, etc.)
- Configuration files involved

## Steps

### 1. Identify Error Category

Parse build output to determine issue type:

**A. Missing file/directory**
- `Cannot find file 'X'`
- `Entry module not found: 'X'`

**B. Invalid syntax in config**
- `Unexpected token`
- `JSON parse error`
- `Invalid configuration`

**C. Incompatible options**
- `Option 'X' cannot be used with 'Y'`
- `Conflicting configuration`

**D. Missing plugin/loader**
- `Cannot find loader 'X'`
- `Plugin 'X' not found`

**E. Wrong path/pattern**
- `No matching files for pattern 'X'`
- `Failed to resolve 'X'`

**F. Version incompatibility**
- `Requires version X but got Y`
- `Incompatible peer dependency`

### 2. Read Configuration File

Identify which config file has the issue:
- `angular.json` (Angular)
- `webpack.config.js` (Webpack)
- `tsconfig.json` (TypeScript)
- `package.json` (npm)
- `vite.config.js` (Vite)
- `rollup.config.js` (Rollup)

Read the file to understand current settings.

### 3. Apply Fix Based on Error Type

#### Fix A: Missing File

**Update file path in config:**
```json
{
  "main": "src/main.ts"  // Verify this file exists
}
```

**Create missing file if needed:**
- Check if file should exist
- Create minimal valid file
- Or update config to point to correct file

**Example tsconfig.json:**
```json
{
  "files": [
    "src/main.ts",  // Ensure this exists
    "src/polyfills.ts"
  ]
}
```

#### Fix B: Invalid Syntax

**Common JSON errors:**
- Trailing commas
- Unquoted keys
- Single quotes instead of double
- Missing brackets/braces

**Fix:**
```json
// Before (invalid):
{
  "name": "app",
  "version": "1.0.0",  // ← Remove trailing comma before }
}

// After (valid):
{
  "name": "app",
  "version": "1.0.0"
}
```

**Use JSON validator:**
- VS Code has built-in validation
- Or use online JSON validator

#### Fix C: Incompatible Options

**Check documentation for valid combinations:**

**Example TypeScript:**
```json
{
  "compilerOptions": {
    "module": "ES2022",
    "target": "ES2022"  // Must be compatible with module
  }
}
```

**Common valid combinations:**
- `module: "ES2022"` + `target: "ES2022"`
- `module: "CommonJS"` + `target: "ES2020"`
- `module: "ESNext"` + `target: "ESNext"`

#### Fix D: Missing Plugin/Loader

**Install missing package:**

```bash
# For webpack loaders:
npm install --save-dev ts-loader
npm install --save-dev css-loader style-loader

# For plugins:
npm install --save-dev html-webpack-plugin
npm install --save-dev copy-webpack-plugin
```

**Add to config:**
```javascript
// webpack.config.js
module.exports = {
  module: {
    rules: [
      {
        test: /\.ts$/,
        use: 'ts-loader'  // Now installed
      }
    ]
  }
};
```

#### Fix E: Wrong Path/Pattern

**Fix glob patterns:**
```json
{
  "include": [
    "src/**/*"  // Recursive pattern
  ],
  "exclude": [
    "node_modules",
    "dist"
  ]
}
```

**Common patterns:**
- `**/*` - All files recursively
- `*.ts` - All .ts files in current dir
- `src/**/*.ts` - All .ts files under src
- `**/*.spec.ts` - All test files

**Fix file paths:**
```json
{
  "compilerOptions": {
    "outDir": "./dist",  // Use correct relative path
    "rootDir": "./src"
  }
}
```

#### Fix F: Version Incompatibility

**Update package versions:**

```json
// package.json
{
  "devDependencies": {
    "@angular/compiler-cli": "^17.0.0",  // Update to compatible version
    "typescript": "~5.2.0"  // TypeScript version must be compatible
  }
}
```

**Check compatibility:**
- Read framework documentation for version requirements
- Check package peer dependencies
- Use version ranges wisely

**Reinstall:**
```bash
npm install
```

### 4. Common Build Configuration Fixes

#### Angular CLI (angular.json)

```json
{
  "projects": {
    "app-name": {
      "architect": {
        "build": {
          "options": {
            "outputPath": "dist/app-name",  // Verify path
            "index": "src/index.html",      // File must exist
            "main": "src/main.ts",          // Entry point must exist
            "tsConfig": "tsconfig.app.json" // Config must exist
          }
        }
      }
    }
  }
}
```

#### TypeScript (tsconfig.json)

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "ES2022",
    "lib": ["ES2022", "dom"],
    "outDir": "./dist",
    "rootDir": "./src",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,  // Skip lib checking if needed
    "forceConsistentCasingInFileNames": true
  },
  "include": ["src/**/*"],
  "exclude": ["node_modules", "dist"]
}
```

#### Webpack (webpack.config.js)

```javascript
const path = require('path');

module.exports = {
  entry: './src/index.ts',  // Verify path
  output: {
    path: path.resolve(__dirname, 'dist'),  // Absolute path
    filename: 'bundle.js'
  },
  resolve: {
    extensions: ['.ts', '.js']  // File extensions to resolve
  },
  module: {
    rules: [
      {
        test: /\.ts$/,
        use: 'ts-loader',
        exclude: /node_modules/
      }
    ]
  }
};
```

### 5. Clear Build Cache

Sometimes cached data causes issues:

```bash
# Clear Angular cache
rm -rf .angular/

# Clear TypeScript cache
rm -rf dist/

# Clear node_modules and reinstall
rm -rf node_modules
npm install

# Clear npm cache (if really stuck)
npm cache clean --force
```

### 6. Rebuild and Verify

After fixing configuration:
1. Save all config files
2. Clear cache if needed
3. Run build command
4. Verify build succeeds
5. Check output directory has expected files

### 7. Common Build Commands

```bash
# Angular
ng build
ng build --configuration production

# TypeScript
tsc
tsc --build

# Webpack
npx webpack
npx webpack --mode production

# npm scripts (defined in package.json)
npm run build
```

## Output

Working build configuration with successful compilation.

## Examples

### Example 1: Missing Main File

**Error:**
```
Cannot find file 'src/main.ts'
```

**Fix in angular.json:**
```json
{
  "architect": {
    "build": {
      "options": {
        "main": "src/main.ts"  // Verify file exists at this path
      }
    }
  }
}
```

**Create file if missing:**
```typescript
// src/main.ts
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';

platformBrowserDynamic().bootstrapModule(AppModule);
```

### Example 2: TypeScript Config Incompatibility

**Error:**
```
Option 'module' must be 'CommonJS' when 'target' is 'ES3' or 'ES5'
```

**Fix:**
```json
{
  "compilerOptions": {
    "target": "ES2020",  // Update target
    "module": "ES2020"   // Now compatible
  }
}
```

### Example 3: Missing Loader

**Error:**
```
Module parse failed: Unexpected token
You may need an appropriate loader to handle this file type
```

**Fix:**
```bash
npm install --save-dev ts-loader
```

```javascript
// webpack.config.js
module.exports = {
  module: {
    rules: [
      {
        test: /\.ts$/,
        use: 'ts-loader'
      }
    ]
  }
};
```
