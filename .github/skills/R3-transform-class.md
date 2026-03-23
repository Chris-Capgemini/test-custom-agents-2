# Skill R3: Transform Class/Component Structure

**When to use:** Converting a class, component, or module file from source to target technology

**Purpose:** Systematically transform a complete file while preserving functionality and structure.

## Input

- Parsed source file structure (from Skill R1)
- Source and target technology specifications

## Steps

### 1. Create Target File

**🚨 CRITICAL: File Location**
- **NEVER modify the source file**
- **CREATE NEW FILE in `output/{target-app-name}/` directory**
- Source file location: `workspace/src/UserService.java` (READ ONLY)
- New file location: `output/angular-app/src/app/services/user.service.ts` (CREATE NEW)

**File creation:**
- Determine target file extension (.ts, .tsx, .js, .py, etc.)
- Create file in appropriate directory within `output/{target-app-name}/`
- Follow target framework's directory conventions
- Use similar name (adjust casing conventions if needed)

**Example:**
```
Source:  src/com/app/services/UserService.java  (read from here)
Target:  output/angular-app/src/app/services/user.service.ts  (create here)
```

### 2. Generate Imports

- **Map framework imports:**
  - Source: `import javax.swing.JFrame;`
  - Target: `import { Component } from '@angular/core';`

- **Convert internal imports:**
  - Update paths to match new structure
  - Use Skill R5 if structure changed significantly

- **Add necessary target imports:**
  - Framework-specific imports
  - Type imports if using TypeScript
  - Library imports for replaced functionality

### 3. Transform Class/Component Declaration

- **Convert syntax:**
  - Java: `public class UserService { }`
  - TypeScript: `export class UserService { }`

- **Map decorators/annotations:**
  - `@Service` → `@Injectable()`
  - `@Component` → `@Component({ })`
  - `@RestController` → Express router setup

- **Apply naming conventions:**
  - Keep class name consistent or adjust for conventions
  - PascalCase for classes, camelCase for instances

### 4. Transform Properties/Fields

For each field:

- **Convert with type mapping** (use Skill R2):
  ```java
  private String name;
  ```
  →
  ```typescript
  private name: string;
  ```

- **Map access modifiers:**
  - `private` → `private`
  - `public` → `public`
  - `protected` → `protected`
  - Default (Java) → `public` (TypeScript)

- **Convert initialization:**
  - `String name = "default";` → `name: string = "default";`

- **Handle fields with decorators:**
  - `@Autowired` → constructor injection
  - `@Input()` → `@Input()`
  - `@ViewChild()` → `@ViewChild()`

### 5. Transform Constructor

- **Map to target initialization:**

Java:
```java
public UserService(UserRepository repo) {
    this.userRepository = repo;
}
```

TypeScript:
```typescript
constructor(private userRepository: UserRepository) {
    // Short-hand property declaration
}
```

- **Convert parameter injection:**
  - Spring DI → Angular DI
  - Manual injection → DI framework

- **Preserve initialization logic:**
  - Move constructor body to constructor or initialization method

### 6. Transform Methods

For each method:

- **Convert signature:**
  ```java
  public User getUserById(int id) { }
  ```
  →
  ```typescript
  getUserById(id: number): User { }
  ```

- **Map lifecycle methods:**
  - `onCreate()` → `ngOnInit()`
  - `componentDidMount()` → `ngOnInit()`
  - `onDestroy()` → `ngOnDestroy()`

- **Convert method body** (use Skill R4)

- **Preserve method modifiers:**
  - `static` → `static`
  - `async` → `async`
  - `abstract` → `abstract`

### 7. Add Target-Specific Boilerplate

- **Add exports:**
  ```typescript
  export class UserService { }
  ```

- **Add decorators:**
  ```typescript
  @Injectable({
    providedIn: 'root'
  })
  ```

- **Add interface implementations:**
  - `implements OnInit`
  - `extends BaseClass`

- **Add metadata:**
  - Component selector, template, styles
  - Module declarations

## Output

Complete, syntactically correct file in target language/framework.

## Example Transform

### Source (Java Spring):
```java
package com.app.service;

import com.app.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepo;
    
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
    
    public User findById(int id) {
        return userRepo.findById(id).orElse(null);
    }
}
```

### Target (TypeScript/Angular):
```typescript
import { Injectable } from '@angular/core';
import { User } from '../models/user.model';
import { UserRepository } from '../repositories/user.repository';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private userRepo: UserRepository) {}
  
  findById(id: number): User | null {
    return this.userRepo.findById(id) || null;
  }
}
```
