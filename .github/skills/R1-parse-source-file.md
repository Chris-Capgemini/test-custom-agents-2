# Skill R1: Parse Source File Structure

**When to use:** Beginning analysis of any source file

**Purpose:** Extract structured information from a source code file to understand its components and dependencies.

## Steps

1. **Read the entire file content**
   - Load complete file to understand full context

2. **Identify file type**
   - Determine: class, component, service, utility, configuration, test, or other
   - Note the file extension and framework indicators

3. **Extract imports/dependencies**
   - List all import statements at the top
   - Identify internal vs external dependencies
   - Note any dynamic imports or lazy loading

4. **List all exports**
   - Identify exported classes, functions, constants, types
   - Note if default export or named exports
   - Document visibility (public, exported, internal)

5. **For each export, catalog:**
   - Name and type (class, function, interface, etc.)
   - Properties/fields with types
   - Methods/functions with:
     - Parameter names and types
     - Return types
     - Purpose/behavior
   - Dependencies used within this export

6. **Identify special patterns:**
   - Decorators (e.g., @Component, @Injectable)
   - Annotations (e.g., @Override, @Autowired)
   - Lifecycle methods (e.g., ngOnInit, componentDidMount, onCreate)
   - Event handlers
   - State management patterns

7. **Return structured summary**
   - Create a markdown outline or data structure with findings

## Output Format

```
File: [filename]
Type: [class|component|service|utility|config|test]

Imports:
- [framework imports]
- [internal imports]
- [external library imports]

Exports:
  [ExportName1]:
    - Type: [class|function|const|type]
    - Properties: [list with types]
    - Methods: [list with signatures]
    - Dependencies: [what it uses]
    - Special: [decorators, lifecycle hooks]
  
  [ExportName2]:
    ...

Notes:
- [Any special considerations or patterns]
```

## Example

```
File: UserService.java
Type: service

Imports:
- java.util.List
- com.app.models.User
- org.springframework.stereotype.Service

Exports:
  UserService:
    - Type: class
    - Properties: 
      - userRepository: UserRepository
    - Methods:
      - getUserById(int id): User
      - getAllUsers(): List<User>
      - saveUser(User user): void
    - Dependencies: UserRepository, User model
    - Special: @Service annotation, @Autowired on constructor
```
