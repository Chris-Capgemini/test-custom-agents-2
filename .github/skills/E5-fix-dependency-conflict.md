# Skill E5: Fix Dependency Version Conflict

**When to use:** Package manager reports version conflicts or incompatible dependencies

**Purpose:** Resolve dependency tree conflicts to enable successful installation.

## Input

- Dependency conflict error output
- Package manager type (npm, yarn, pip, maven, etc.)
- List of conflicting packages and versions

## Steps

### 1. Parse Conflict Error

Extract from error message:
- Package names involved
- Required versions by each dependent
- Current installed version (if any)
- Which packages are requesting incompatible versions

**Common error patterns:**

**npm:**
```
ERESOLVE unable to resolve dependency tree
Found: package-a@1.0.0
Could not resolve dependency:
peer package-b@"^2.0.0" from package-c@3.0.0
```

**yarn:**
```
error Found incompatible versions:
  package-a@1.0.0 (required by x, required ^1.0.0)
  package-a@2.0.0 (required by y, required ^2.0.0)
```

**pip:**
```
ERROR: package-a 1.0.0 has requirement package-b>=2.0.0, 
but you'll have package-b 1.5.0 which is incompatible.
```

### 2. Research Compatible Versions

For each package in conflict:

**Check package documentation:**
- Official website
- GitHub repository
- npm/PyPI package page
- CHANGELOG or release notes

**Check peer dependencies:**
```bash
npm info package-name peerDependencies
```

**Check available versions:**
```bash
npm view package-name versions
```

### 3. Decide Resolution Strategy

Choose appropriate strategy:

**A. Update to Latest Compatible**
- Use newest version that satisfies all dependencies
- Safest option if major versions align

**B. Lock Specific Version**
- Force exact version that works
- Use when you know specific version is stable

**C. Update Dependent Package**
- Update the package causing constraint
- If newer version has compatible requirements

**D. Use Resolution Override**
- Force package manager to use specific version
- Last resort when other methods fail

### 4. Apply Fix by Package Manager

#### npm / Node.js

**Method 1: Update package.json versions**

```json
{
  "dependencies": {
    "package-a": "^2.0.0",  // Update to compatible version
    "package-b": "^3.0.0"
  }
}
```

**Method 2: Use overrides (npm 8.3+)**

```json
{
  "overrides": {
    "package-a": "2.5.0"  // Force specific version
  }
}
```

**Method 3: Use legacy peer deps flag**

```bash
npm install --legacy-peer-deps
```

**Method 4: Install with force**

```bash
npm install --force  # Not recommended, may break things
```

#### yarn

**Use resolutions:**

```json
{
  "resolutions": {
    "package-a": "2.5.0"  // Force version across all deps
  }
}
```

**Or update and reinstall:**

```bash
yarn upgrade package-a@^2.0.0
```

#### pip / Python

**Method 1: Update requirements.txt**

```
package-a==2.5.0  # Exact version
package-b>=2.0.0,<3.0.0  # Version range
```

**Method 2: Install specific version**

```bash
pip install package-a==2.5.0
```

**Method 3: Use constraint file**

Create `constraints.txt`:
```
package-a==2.5.0
```

Install with:
```bash
pip install -c constraints.txt -r requirements.txt
```

#### Maven / Java

**Add dependency management:**

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>package-a</artifactId>
      <version>2.5.0</version>
    </dependency>
  </dependencies>
</dependencyManagement>
```

**Or exclude transitive dependency:**

```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>package-b</artifactId>
  <version>1.0.0</version>
  <exclusions>
    <exclusion>
      <groupId>com.example</groupId>
      <artifactId>package-a</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

### 5. Clear Dependency Cache

Remove cached dependencies:

**npm:**
```bash
rm -rf node_modules
rm package-lock.json
npm cache clean --force
npm install
```

**yarn:**
```bash
rm -rf node_modules
rm yarn.lock
yarn cache clean
yarn install
```

**pip:**
```bash
pip cache purge
pip install -r requirements.txt --force-reinstall
```

### 6. Reinstall Dependencies

After updating configuration:

```bash
# npm
npm install

# yarn
yarn install

# pip
pip install -r requirements.txt

# maven
mvn clean install
```

### 7. Verify Application Works

After resolving conflict:

1. **Run build** to ensure compilation succeeds
2. **Run tests** if available
3. **Start application** to verify runtime
4. **Test key features** that use conflicting packages
5. **Check for console warnings** about deprecated APIs

### 8. Document Resolution

Add comment in package file:

```json
{
  "dependencies": {
    "package-a": "2.5.0"  // Fixed at 2.5.0 to resolve conflict with package-b
  }
}
```

## Common Conflict Scenarios

### Scenario 1: Peer Dependency Conflict

**Error:**
```
package-a@3.0.0 requires peer package-b@^2.0.0
but package-b@1.5.0 is installed
```

**Fix:**
```bash
npm install package-b@^2.0.0
```

### Scenario 2: Multiple Versions of Same Package

**Error:**
```
Found: lodash@4.17.0 (from project)
Found: lodash@3.10.0 (from old-package)
```

**Fix using overrides:**
```json
{
  "overrides": {
    "lodash": "4.17.21"  // Use latest version everywhere
  }
}
```

### Scenario 3: Incompatible Major Versions

**Error:**
```
package-x needs react@^16.0.0
package-y needs react@^18.0.0
```

**Fix:** Update package-x to version that supports React 18, or downgrade package-y.

```bash
npm install package-x@latest  # Check if newer version supports React 18
```

## Output

- Resolved dependency tree
- All packages installed successfully
- Application builds and runs
- No version conflict errors

## Examples

### Example 1: Angular Material Version Conflict

**Error:**
```
Package "@angular/material" has a peer dependency on "@angular/cdk@^17.0.0"
but "@angular/cdk@16.0.0" is installed
```

**Fix:**
```bash
npm install @angular/cdk@^17.0.0
```

Or update both:
```bash
npm install @angular/material@^17.0.0 @angular/cdk@^17.0.0
```

### Example 2: TypeScript Version Conflict

**Error:**
```
Found: typescript@4.9.0
Required: typescript@~5.2.0 by @angular/compiler-cli@17.0.0
```

**Fix package.json:**
```json
{
  "devDependencies": {
    "typescript": "~5.2.0"  // Update to required version
  }
}
```

**Reinstall:**
```bash
rm -rf node_modules package-lock.json
npm install
```

### Example 3: Transitive Dependency Conflict

**Error:**
```
package-a depends on package-c@1.x
package-b depends on package-c@2.x
```

**Fix using overrides:**
```json
{
  "dependencies": {
    "package-a": "^1.0.0",
    "package-b": "^2.0.0"
  },
  "overrides": {
    "package-c": "2.5.0"  // Force v2 everywhere, ensure package-a can handle it
  }
}
```

### Example 4: Python Dependency Conflict

**Error:**
```
ERROR: django 3.0.0 has requirement sqlparse>=0.2.2, 
but you'll have sqlparse 0.1.0 which is incompatible.
```

**Fix requirements.txt:**
```
django==3.0.0
sqlparse>=0.2.2  # Update to satisfy django's requirement
```

**Reinstall:**
```bash
pip install -r requirements.txt --upgrade
```
