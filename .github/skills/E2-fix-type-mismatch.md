# Skill E2: Fix Type Mismatch Error

**When to use:** Compilation error indicates incompatible types

**Purpose:** Resolve type compatibility issues while preserving functionality.

## Input

- Error message (exact text)
- File path
- Line number
- Code context

## Steps

### 1. Parse Error Message

Extract information:
- Expected type
- Actual type provided
- Location (file and line)
- Operation causing mismatch

**Common error patterns:**
- `Type 'X' is not assignable to type 'Y'`
- `Argument of type 'X' is not assignable to parameter of type 'Y'`
- `Type 'X' is missing property 'Y'`
- `Type 'X' cannot be used as type 'Y'`

### 2. Read Code Context

Read surrounding code (10 lines before and after error line) to understand:
- Variable declarations
- Function signatures
- Object structures
- Type annotations

### 3. Identify Mismatch Cause

Categorize the issue:

**A. Incorrect return type**
```typescript
function getUser(): User {
  return null;  // Error: null not assignable to User
}
```

**B. Wrong parameter type**
```typescript
function process(id: number) { }
process("123");  // Error: string not assignable to number
```

**C. Missing property**
```typescript
interface User {
  name: string;
  age: number;
}
const user: User = { name: "John" };  // Error: missing 'age'
```

**D. Null/undefined issue**
```typescript
const user: User = getUser();  // Error: User | null not assignable to User
```

**E. Array vs single value**
```typescript
function process(item: string) { }
const items: string[] = ["a", "b"];
process(items);  // Error: string[] not assignable to string
```

**F. Wrong generic type**
```typescript
const list: Array<number> = ["1", "2"];  // Error: string[] not assignable to number[]
```

### 4. Apply Appropriate Fix

#### Fix A: Update Return Type

**Option 1 - Make nullable:**
```typescript
function getUser(): User | null {
  return null;  // Now valid
}
```

**Option 2 - Return proper value:**
```typescript
function getUser(): User {
  return new User();  // Return actual User
}
```

#### Fix B: Convert Parameter Type

**Option 1 - Convert at call site:**
```typescript
process(parseInt("123"));  // Convert string to number
```

**Option 2 - Update function signature:**
```typescript
function process(id: string | number) {  // Accept both
  const numId = typeof id === 'string' ? parseInt(id) : id;
}
```

#### Fix C: Add Missing Property

**Option 1 - Add property:**
```typescript
const user: User = { 
  name: "John",
  age: 0  // Add missing property
};
```

**Option 2 - Make property optional:**
```typescript
interface User {
  name: string;
  age?: number;  // Make optional
}
```

**Option 3 - Use partial:**
```typescript
const user: Partial<User> = { name: "John" };
```

#### Fix D: Handle Null/Undefined

**Option 1 - Add null check:**
```typescript
const user = getUser();
if (user) {
  // Use user safely
}
```

**Option 2 - Use non-null assertion (if certain):**
```typescript
const user = getUser()!;  // Assert it's not null
```

**Option 3 - Provide default:**
```typescript
const user = getUser() || getDefaultUser();
```

**Option 4 - Optional chaining:**
```typescript
const name = user?.name;  // Returns undefined if user is null
```

#### Fix E: Array vs Single

**Option 1 - Access first element:**
```typescript
process(items[0]);  // Pass first item
```

**Option 2 - Loop through array:**
```typescript
items.forEach(item => process(item));
```

**Option 3 - Update function to accept array:**
```typescript
function process(items: string | string[]) {
  const itemArray = Array.isArray(items) ? items : [items];
  // Process array
}
```

#### Fix F: Generic Type

**Option 1 - Convert values:**
```typescript
const list: Array<number> = ["1", "2"].map(x => parseInt(x));
```

**Option 2 - Change target type:**
```typescript
const list: Array<string> = ["1", "2"];  // Use correct type
```

### 5. Special Type Conversions

**String to Number:**
```typescript
const num = parseInt(str);
const num = parseFloat(str);
const num = Number(str);
const num = +str;
```

**Number to String:**
```typescript
const str = num.toString();
const str = String(num);
const str = `${num}`;
```

**Type Assertion (when you know better):**
```typescript
const value = someValue as TargetType;
// or
const value = <TargetType>someValue;
```

**Type Guards:**
```typescript
if (typeof value === 'string') {
  // TypeScript knows value is string here
}

if (value instanceof User) {
  // TypeScript knows value is User here
}
```

### 6. Rebuild and Verify

After fix:
1. Save file
2. Run build/compile
3. Verify error is gone
4. Check that functionality still works

### 7. If Still Fails

If type error persists:
- Re-read error message carefully
- Check if fix was applied to correct location
- Verify types are imported correctly
- Check if type definitions are installed
- Try alternative fix approach

## Output

Type-corrected code that compiles successfully.

## Examples

### Example 1: Null Assignment

**Error:**
```
Type 'null' is not assignable to type 'User'.
  const user: User = null;
```

**Fix:**
```typescript
const user: User | null = null;
// or
const user: User | undefined = undefined;
```

### Example 2: Missing Property

**Error:**
```
Property 'email' is missing in type '{ name: string; }' but required in type 'User'.
```

**Fix:**
```typescript
// Option 1: Add property
const user: User = {
  name: "John",
  email: "john@example.com"
};

// Option 2: Make optional in interface
interface User {
  name: string;
  email?: string;  // Now optional
}
```

### Example 3: Array Mismatch

**Error:**
```
Type 'string[]' is not assignable to type 'string'.
  const name: string = users.map(u => u.name);
```

**Fix:**
```typescript
const names: string[] = users.map(u => u.name);
// or if you want first one:
const name: string = users[0]?.name || '';
```

### Example 4: Function Return Type

**Error:**
```
Type 'Promise<User>' is not assignable to type 'User'.
```

**Fix:**
```typescript
// Make function async and await result
async function processUser() {
  const user: User = await fetchUser();  // await the Promise
}

// Or update return type
function getUser(): Promise<User> {
  return fetchUser();  // Return Promise<User>
}
```

### Example 5: Union Type Narrowing

**Error:**
```
Type 'string | number' is not assignable to type 'number'.
```

**Fix:**
```typescript
function process(value: string | number) {
  const numValue: number = typeof value === 'string' 
    ? parseInt(value) 
    : value;
  // Now numValue is definitely number
}
```
