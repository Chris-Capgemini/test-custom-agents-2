# Skill E3: Fix Undefined Property/Method

**When to use:** Runtime or compile error indicates missing property or method on an object

**Purpose:** Fix member access errors by using correct API for target framework.

## Input

- Error message
- Object/class type being accessed
- Property/method name that's missing
- Code context

## Steps

### 1. Parse Error Message

Extract:
- Object type
- Missing member name
- Operation attempted
- Location in code

**Common error patterns:**
- `Property 'X' does not exist on type 'Y'`
- `'X' is not a function`
- `Cannot read property 'X' of undefined`
- `undefined is not an object (evaluating 'obj.X')`

### 2. Identify Object Type

Determine what object is being accessed:
- Class instance
- Framework API object
- Standard library object
- Custom type

### 3. Check Target Framework Documentation

Research if member exists in target framework:
- Search official documentation
- Check API reference
- Look for migration guides
- Check version compatibility

### 4. Apply Fix Based on Issue Type

#### Issue A: Method Renamed

Framework changed method names between source and target.

**Common renamings:**

| Source (Java) | Target (TypeScript/JS) |
|---------------|------------------------|
| `.length()` | `.length` (property) |
| `.size()` | `.length` or `.size` |
| `.isEmpty()` | `.length === 0` or `!value` |
| `.add(item)` | `.push(item)` |
| `.remove(index)` | `.splice(index, 1)` |
| `.contains(item)` | `.includes(item)` |
| `.charAt(i)` | `[i]` or `.charAt(i)` |
| `.substring(a,b)` | `.slice(a,b)` |

**Fix:**
```typescript
// Before:
const len = list.size();

// After:
const len = list.length;
```

#### Issue B: Property vs Method

Source used method, target uses property (or vice versa).

**Fix:**
```typescript
// Before (method):
const count = items.count();

// After (property):
const count = items.length;
```

#### Issue C: API Changed

Method signature or behavior changed.

**Example: Observable patterns**
```typescript
// Before (RxJS 5):
observable.map(x => x * 2);

// After (RxJS 6+):
observable.pipe(map(x => x * 2));
```

**Fix:**
```typescript
import { map } from 'rxjs/operators';

observable.pipe(
  map(x => x * 2)
);
```

#### Issue D: Method Removed/Deprecated

Feature no longer exists in target framework.

**Find equivalent:**
- Check migration guides
- Search for alternative functions
- Implement custom helper if needed

**Example:**
```typescript
// Before (jQuery):
$('#id').show();

// After (vanilla JS):
document.getElementById('id').style.display = 'block';

// Or (modern):
document.getElementById('id')?.classList.remove('hidden');
```

#### Issue E: Null/Undefined Object

Object itself is null/undefined.

**Fix with null safety:**
```typescript
// Before:
const value = obj.property;  // Error if obj is null

// After (optional chaining):
const value = obj?.property;

// Or (null check):
const value = obj ? obj.property : undefined;
```

#### Issue F: Missing Type Definition

TypeScript doesn't know about the property.

**Fix:**
```typescript
// Option 1: Install type definitions
npm install --save-dev @types/library-name

// Option 2: Declare type
interface MyType {
  existingProp: string;
  missingProp?: any;  // Add missing property
}

// Option 3: Type assertion
(obj as any).missingProp;  // Last resort
```

### 5. Common Framework-Specific Fixes

#### Angular Specifics

**Lifecycle methods:**
```typescript
// Implement interface
export class MyComponent implements OnInit {
  ngOnInit() {  // Now recognized
    // ...
  }
}
```

**Dependency Injection:**
```typescript
// Inject service
constructor(private myService: MyService) {}
// Now this.myService is available
```

#### React Specifics

**State/Props:**
```typescript
// Before (class component):
this.state.value

// After (functional component):
const [value, setValue] = useState();
```

#### DOM APIs

```typescript
// Before (old API):
element.getAttribute('class');

// After (modern):
element.className;
// or
element.classList;
```

### 6. Collection Operation Mappings

**Array operations:**
```typescript
// Java → JavaScript/TypeScript
list.get(0)          → list[0]
list.add(item)       → list.push(item)
list.remove(index)   → list.splice(index, 1)
list.contains(item)  → list.includes(item)
list.size()          → list.length
list.isEmpty()       → list.length === 0
list.clear()         → list.length = 0 or list = []

// Java Stream → JS Array methods
list.stream()
  .map(...)          → list.map(...)
  .filter(...)       → list.filter(...)
  .forEach(...)      → list.forEach(...)
  .findFirst()       → list.find(...)
  .collect()         → // No need, already array
```

**Map operations:**
```typescript
// Java → JavaScript/TypeScript
map.get(key)         → map.get(key) or obj[key]
map.put(key, val)    → map.set(key, val) or obj[key] = val
map.containsKey(key) → map.has(key) or key in obj
map.remove(key)      → map.delete(key) or delete obj[key]
map.size()           → map.size or Object.keys(obj).length
map.keySet()         → Array.from(map.keys()) or Object.keys(obj)
map.values()         → Array.from(map.values()) or Object.values(obj)
```

### 7. Rebuild and Verify

After applying fix:
1. Save file
2. Compile/build
3. Verify error is resolved
4. Test that functionality works

## Output

Fixed code with correct property/method access for target framework.

## Examples

### Example 1: Collection Method

**Error:**
```
Property 'size' does not exist on type 'any[]'.
```

**Code:**
```typescript
const count = users.size();
```

**Fix:**
```typescript
const count = users.length;
```

### Example 2: String Method

**Error:**
```
Property 'contains' does not exist on type 'string'.
```

**Code:**
```typescript
if (name.contains("John")) { }
```

**Fix:**
```typescript
if (name.includes("John")) { }
```

### Example 3: Null Safety

**Error:**
```
Cannot read property 'name' of undefined
```

**Code:**
```typescript
const userName = user.name;
```

**Fix:**
```typescript
const userName = user?.name;
// or
const userName = user ? user.name : null;
```

### Example 4: Observable Pattern

**Error:**
```
Property 'map' does not exist on type 'Observable<User>'.
```

**Code:**
```typescript
return this.http.get<User>('/api/user').map(data => data);
```

**Fix:**
```typescript
import { map } from 'rxjs/operators';

return this.http.get<User>('/api/user').pipe(
  map(data => data)
);
```

### Example 5: Lifecycle Hook

**Error:**
```
Property 'ngOnInit' does not exist on type 'MyComponent'.
```

**Code:**
```typescript
export class MyComponent {
  ngOnInit() { }
}
```

**Fix:**
```typescript
import { OnInit } from '@angular/core';

export class MyComponent implements OnInit {
  ngOnInit() { }
}
```
