# Skill R2: Map Data Type Between Languages

**When to use:** Converting type declarations or annotations between source and target languages

**Purpose:** Accurately translate data types while preserving semantics and nullability.

## Steps

1. **Identify source type category:**
   - Primitive (int, string, bool, float, etc.)
   - Collection (List, Array, Set, Map, etc.)
   - Custom/User-defined (classes, interfaces)
   - Generic/Parameterized types
   - Special types (Optional, Promise, Observable, etc.)

2. **Apply mapping rules based on category:**

### Primitive Mappings

| Source (Java) | Target (TypeScript) |
|---------------|---------------------|
| int, Integer | number |
| long, Long | number |
| float, Float | number |
| double, Double | number |
| boolean, Boolean | boolean |
| String | string |
| char, Character | string |
| byte, Byte | number |
| void | void |

| Source (Python) | Target (JavaScript/TS) |
|-----------------|------------------------|
| int | number |
| float | number |
| str | string |
| bool | boolean |
| None | null or undefined |

### Collection Mappings

| Source | Target |
|--------|--------|
| List&lt;T&gt;, ArrayList&lt;T&gt; | Array&lt;T&gt; or T[] |
| Set&lt;T&gt; | Set&lt;T&gt; |
| Map&lt;K,V&gt;, HashMap&lt;K,V&gt; | Map&lt;K,V&gt; or Record&lt;K,V&gt; |
| T[] (Java array) | Array&lt;T&gt; or T[] |
| list[T] (Python) | Array&lt;T&gt; or T[] |
| dict[K,V] (Python) | Record&lt;K,V&gt; or Map&lt;K,V&gt; |

### Nullable/Optional Types

| Source | Target |
|--------|--------|
| T? (Kotlin) | T \| null or T \| undefined |
| Optional&lt;T&gt; (Java) | T \| null or T \| undefined |
| T? (C#) | T \| null or T \| undefined |
| Optional[T] (Python) | T \| None → T \| null |

### Generic Types

- Preserve type parameters: `Container<T>` → `Container<T>`
- Translate bounds: `<T extends Base>` → `<T extends Base>`
- Multiple bounds: `<T extends A & B>` → `<T extends A>` (may need intersection type)

### Special Types

| Source | Target |
|--------|--------|
| Future&lt;T&gt;, CompletableFuture&lt;T&gt; | Promise&lt;T&gt; |
| Observable&lt;T&gt; | Observable&lt;T&gt; (RxJS) |
| Stream&lt;T&gt; | Array&lt;T&gt; or Observable&lt;T&gt; |
| Optional&lt;T&gt; | T \| null |

3. **Handle special cases:**
   - Date/Time types → Date or library types (moment, date-fns)
   - BigDecimal/BigInteger → number or string (for precision)
   - File/Path → string or File API
   - Custom enums → enum or const union types

4. **Return target type with proper syntax**

## Output

Return the correctly formatted type in target language syntax.

## Examples

**Example 1:**
- Source: `List<User>`
- Target: `Array<User>` or `User[]`

**Example 2:**
- Source: `Map<String, Integer>`
- Target: `Map<string, number>` or `Record<string, number>`

**Example 3:**
- Source: `Optional<CompletableFuture<List<String>>>`
- Target: `Promise<Array<string>> | null`

**Example 4:**
- Source: `HashMap<Long, List<Employee>>`
- Target: `Map<number, Employee[]>` or `Record<number, Employee[]>`
