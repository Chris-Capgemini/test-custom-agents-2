# Skill R4: Convert Function/Method Logic

**When to use:** Transforming individual function or method bodies between languages

**Purpose:** Convert implementation logic while preserving exact behavior and intent.

## Steps

### 1. Identify Control Structures

Locate all:
- Conditionals (if, else, switch)
- Loops (for, while, do-while, foreach)
- Try-catch blocks
- Return statements

### 2. Identify Operations

Categorize operations:
- Arithmetic (`+`, `-`, `*`, `/`, `%`)
- String manipulation
- Collection operations
- Object/class operations
- Async operations

### 3. Transform Line by Line

Apply these conversions:

#### Variable Declarations

| Source | Target |
|--------|--------|
| `final String x = "hi";` | `const x = "hi";` |
| `String x = "hi";` | `let x = "hi";` |
| `int x = 5;` | `let x = 5;` |
| `var x = 5;` (Java 10+) | `const x = 5;` |

#### String Operations

| Source (Java) | Target (JS/TS) |
|---------------|----------------|
| `str.substring(0, 5)` | `str.slice(0, 5)` |
| `str.indexOf("x")` | `str.indexOf("x")` |
| `str.contains("x")` | `str.includes("x")` |
| `str.charAt(i)` | `str[i]` or `str.charAt(i)` |
| `str.length()` | `str.length` |
| `str.isEmpty()` | `!str` or `str.length === 0` |
| `String.format("%s", x)` | `` `${x}` `` (template literal) |

#### Collection Operations

| Source (Java) | Target (JS/TS) |
|---------------|----------------|
| `list.size()` | `list.length` |
| `list.get(i)` | `list[i]` |
| `list.add(item)` | `list.push(item)` |
| `list.remove(i)` | `list.splice(i, 1)` |
| `list.stream().map(x -> x*2)` | `list.map(x => x*2)` |
| `list.stream().filter(x -> x>0)` | `list.filter(x => x>0)` |
| `list.stream().forEach(x -> ...)` | `list.forEach(x => ...)` |

#### Null Checking

| Source | Target |
|--------|--------|
| `if (x != null)` | `if (x)` or `if (x !== null)` |
| `if (x == null)` | `if (!x)` or `if (x === null)` |
| `x != null ? x : "default"` | `x || "default"` or `x ?? "default"` |

#### Type Conversions

| Source | Target |
|--------|--------|
| `Integer.parseInt(str)` | `parseInt(str)` |
| `Double.parseDouble(str)` | `parseFloat(str)` |
| `String.valueOf(x)` | `String(x)` or `x.toString()` |
| `(String) obj` | `obj as string` |

#### Loops

| Source | Target |
|--------|--------|
| `for (int i=0; i<n; i++)` | `for (let i=0; i<n; i++)` |
| `for (Item item : list)` | `for (const item of list)` |
| `while (condition)` | `while (condition)` |

#### Exception Handling

| Source | Target |
|--------|--------|
| `try { } catch (Exception e) { }` | `try { } catch (e) { }` |
| `throw new Exception("msg")` | `throw new Error("msg")` |
| `finally { }` | `finally { }` |

#### Async Operations

| Source (Java) | Target (JS/TS) |
|---------------|----------------|
| `CompletableFuture.supplyAsync(...)` | `async () => { }` or `new Promise(...)` |
| `.thenApply(x -> ...)` | `.then(x => ...)` |
| `.join()` or `.get()` | `await promise` |

### 4. Preserve Comments and Intent

- Keep all comments
- Translate comment text if needed (e.g., Java-specific to TS-specific)
- Maintain TODO/FIXME markers

### 5. Maintain Same Control Flow

- Keep exact same logic flow
- Don't optimize or change algorithm
- Preserve edge case handling

### 6. Apply Target Language Idioms

Use target language best practices:
- Prefer `const` over `let` when possible
- Use arrow functions for callbacks
- Use template literals for string interpolation
- Use optional chaining: `obj?.prop?.method()`
- Use destructuring: `const {x, y} = obj;`

## Output

Converted function body with identical behavior.

## Example

### Source (Java):
```java
public List<String> getActiveUsernames(List<User> users) {
    List<String> result = new ArrayList<>();
    for (User user : users) {
        if (user != null && user.isActive()) {
            result.add(user.getName().toUpperCase());
        }
    }
    return result;
}
```

### Target (TypeScript):
```typescript
getActiveUsernames(users: User[]): string[] {
    const result: string[] = [];
    for (const user of users) {
        if (user && user.isActive()) {
            result.push(user.getName().toUpperCase());
        }
    }
    return result;
}
```

### Or More Idiomatic:
```typescript
getActiveUsernames(users: User[]): string[] {
    return users
        .filter(user => user?.isActive())
        .map(user => user.getName().toUpperCase());
}
```
