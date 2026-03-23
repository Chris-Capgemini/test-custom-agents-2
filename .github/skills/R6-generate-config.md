# Skill R6: Generate Configuration File

**When to use:** Creating build/framework configuration files for target project

**Purpose:** Set up proper configuration for the target technology stack.

## Input

- Target framework type (Angular, React, Vue, Express, etc.)
- Project requirements (build output, dependencies, special features)
- Source project settings (for reference)

## 🚨 CRITICAL: File Location

**ALL configuration files MUST be created in:** `output/{target-app-name}/`

**NEVER create config files in:**
- ❌ Source project directories
- ❌ Workspace root
- ❌ Alongside source files

**Examples:**
- `output/angular-app/angular.json` ✅
- `output/react-app/package.json` ✅
- `output/express-api/tsconfig.json` ✅
- `angular.json` (in workspace root) ❌

---

## Steps

### 1. Identify Configuration Format Needed

Determine what config files are needed:

| Framework | Config Files |
|-----------|-------------|
| **Angular** | `angular.json`, `tsconfig.json`, `package.json` |
| **React** | `package.json`, `tsconfig.json`, `webpack.config.js` (if not CRA) |
| **Vue** | `vue.config.js`, `package.json`, `tsconfig.json` |
| **Node/Express** | `package.json`, `tsconfig.json`, `nodemon.json` |
| **Python/Flask** | `requirements.txt`, `setup.py`, `config.py` |
| **Next.js** | `next.config.js`, `package.json`, `tsconfig.json` |

### 2. Use Framework's Standard Template

Start with framework defaults:
- Use CLI-generated templates as base
- Don't create from scratch
- Extend, don't replace

### 3. Add Project-Specific Settings

#### Entry Points
```json
{
  "main": "src/main.ts",
  "index": "dist/index.js"
}
```

#### Output Directories
```json
{
  "outDir": "dist",
  "outputPath": "dist/app-name"
}
```

#### Source Directories
```json
{
  "sourceRoot": "src",
  "root": ""
}
```

#### Asset Paths
```json
{
  "assets": [
    "src/assets",
    "src/favicon.ico"
  ]
}
```

### 4. Configure Dependencies

**Production Dependencies:**
```json
{
  "dependencies": {
    "@angular/core": "^17.0.0",
    "rxjs": "^7.8.0",
    "tslib": "^2.6.0"
  }
}
```

**Development Dependencies:**
```json
{
  "devDependencies": {
    "@angular/cli": "^17.0.0",
    "typescript": "~5.2.0"
  }
}
```

### 5. Add Build Scripts/Commands

```json
{
  "scripts": {
    "start": "ng serve",
    "build": "ng build",
    "test": "ng test",
    "lint": "ng lint"
  }
}
```

### 6. Set Compiler/Transpiler Options

**TypeScript Config:**
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
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true
  }
}
```

**Babel Config (if needed):**
```json
{
  "presets": [
    "@babel/preset-env",
    "@babel/preset-typescript"
  ]
}
```

### 7. Configure Path Aliases (Optional)

```json
{
  "compilerOptions": {
    "baseUrl": "src",
    "paths": {
      "@app/*": ["app/*"],
      "@models/*": ["app/models/*"],
      "@services/*": ["app/services/*"]
    }
  }
}
```

### 8. Validate Syntax

- Use JSON schema validation if available
- Check for:
  - Valid JSON (no trailing commas, proper quotes)
  - Required fields present
  - Correct data types
  - Valid version strings

## Output

Complete, valid configuration file(s) ready to use.

## Examples

### Angular - package.json
```json
{
  "name": "migrated-app",
  "version": "1.0.0",
  "scripts": {
    "ng": "ng",
    "start": "ng serve",
    "build": "ng build",
    "watch": "ng build --watch",
    "test": "ng test"
  },
  "private": true,
  "dependencies": {
    "@angular/animations": "^17.0.0",
    "@angular/common": "^17.0.0",
    "@angular/compiler": "^17.0.0",
    "@angular/core": "^17.0.0",
    "@angular/forms": "^17.0.0",
    "@angular/platform-browser": "^17.0.0",
    "@angular/platform-browser-dynamic": "^17.0.0",
    "@angular/router": "^17.0.0",
    "rxjs": "~7.8.0",
    "tslib": "^2.3.0",
    "zone.js": "~0.14.0"
  },
  "devDependencies": {
    "@angular-devkit/build-angular": "^17.0.0",
    "@angular/cli": "^17.0.0",
    "@angular/compiler-cli": "^17.0.0",
    "typescript": "~5.2.0"
  }
}
```

### React - package.json
```json
{
  "name": "migrated-app",
  "version": "1.0.0",
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-scripts": "5.0.1"
  },
  "devDependencies": {
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "typescript": "^5.2.0"
  }
}
```

### Express - package.json
```json
{
  "name": "migrated-api",
  "version": "1.0.0",
  "main": "dist/index.js",
  "scripts": {
    "start": "node dist/index.js",
    "dev": "nodemon src/index.ts",
    "build": "tsc",
    "watch": "tsc -w"
  },
  "dependencies": {
    "express": "^4.18.0",
    "cors": "^2.8.5"
  },
  "devDependencies": {
    "@types/express": "^4.17.0",
    "@types/node": "^20.0.0",
    "nodemon": "^3.0.0",
    "ts-node": "^10.9.0",
    "typescript": "^5.2.0"
  }
}
```

## Common Configuration Patterns

### Environment-Specific Config
```json
{
  "configurations": {
    "production": {
      "optimization": true,
      "outputHashing": "all",
      "sourceMap": false
    },
    "development": {
      "optimization": false,
      "sourceMap": true
    }
  }
}
```

### Linting Config (.eslintrc.json)
```json
{
  "extends": [
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended"
  ],
  "parser": "@typescript-eslint/parser",
  "plugins": ["@typescript-eslint"],
  "root": true
}
```
