---
name: mermaid-diagrams
description: Provides correct Mermaid syntax for generating diagrams in code analysis documentation. Use this skill when creating UML class diagrams, sequence diagrams, flowcharts, component diagrams, or any other visual diagrams. Ensures syntactically correct and well-formatted Mermaid code.
---

# Mermaid Diagrams Skill

This skill provides Mermaid syntax reference for generating diagrams in GenInsights documentation.

## Diagram Types

### 1. Class Diagram

```mermaid
classDiagram
    class ClassName {
        +String publicField
        -int privateField
        #double protectedField
        +publicMethod() void
        -privateMethod() String
        #protectedMethod(param) int
    }
    
    class AnotherClass {
        +field: Type
        +method(): ReturnType
    }
    
    ClassName <|-- SubClass : extends
    ClassName *-- ComposedClass : contains
    ClassName o-- AggregatedClass : has
    ClassName --> AssociatedClass : uses
    ClassName ..> DependencyClass : depends
    ClassName ..|> InterfaceName : implements
```

**Relationship Types:**
| Symbol | Meaning |
|--------|---------|
| `<\|--` | Inheritance (extends) |
| `*--` | Composition (contains, lifecycle bound) |
| `o--` | Aggregation (has, independent lifecycle) |
| `-->` | Association (uses) |
| `..>` | Dependency (depends on) |
| `..\|>` | Realization (implements) |

**Visibility Modifiers:**
| Symbol | Meaning |
|--------|---------|
| `+` | Public |
| `-` | Private |
| `#` | Protected |
| `~` | Package/Internal |

### 2. Sequence Diagram

```mermaid
sequenceDiagram
    participant C as Client
    participant S as Service
    participant D as Database
    
    C->>S: request(data)
    activate S
    S->>D: query(id)
    activate D
    D-->>S: result
    deactivate D
    
    alt success
        S-->>C: response(200)
    else error
        S-->>C: error(500)
    end
    deactivate S
    
    loop retry 3 times
        C->>S: retryRequest()
    end
    
    opt optional step
        S->>S: internalProcess()
    end
    
    Note over C,S: This is a note
    Note right of D: Database note
```

**Arrow Types:**
| Symbol | Meaning |
|--------|---------|
| `->>` | Solid line with arrowhead (sync call) |
| `-->>` | Dashed line with arrowhead (response) |
| `--)` | Solid line with open arrow (async) |
| `--)` | Dashed line with open arrow |
| `-x` | Solid line with X (lost message) |

### 3. Flowchart (BPMN-style)

```mermaid
flowchart TD
    Start([Start]) --> A[Process Step]
    A --> B{Decision?}
    B -->|Yes| C[Action 1]
    B -->|No| D[Action 2]
    C --> E[[Subprocess]]
    D --> E
    E --> F[(Database)]
    F --> G>Output]
    G --> End([End])
    
    subgraph "Subprocess Detail"
        E1[Step 1] --> E2[Step 2]
    end
```

**Node Shapes:**
| Syntax | Shape | Use For |
|--------|-------|---------|
| `[text]` | Rectangle | Process/Action |
| `([text])` | Stadium | Start/End |
| `{text}` | Diamond | Decision |
| `[[text]]` | Subroutine | Subprocess |
| `[(text)]` | Cylinder | Database |
| `((text))` | Circle | Connector |
| `>text]` | Flag | Output |
| `{{text}}` | Hexagon | Preparation |

**Direction:**
| Code | Direction |
|------|-----------|
| `TD` or `TB` | Top to Bottom |
| `BT` | Bottom to Top |
| `LR` | Left to Right |
| `RL` | Right to Left |

### 4. Component Diagram (using flowchart)

```mermaid
flowchart TB
    subgraph "Presentation Layer"
        UI[Web UI]
        API[REST API]
    end
    
    subgraph "Business Layer"
        SVC[Services]
        BL[Business Logic]
    end
    
    subgraph "Data Layer"
        REPO[Repositories]
        DB[(Database)]
    end
    
    UI --> API
    API --> SVC
    SVC --> BL
    BL --> REPO
    REPO --> DB
```

### 5. State Diagram

```mermaid
stateDiagram-v2
    [*] --> Draft
    Draft --> Submitted: submit()
    Submitted --> UnderReview: assign()
    UnderReview --> Approved: approve()
    UnderReview --> Rejected: reject()
    Rejected --> Draft: revise()
    Approved --> [*]
    
    state UnderReview {
        [*] --> Reviewing
        Reviewing --> NeedsInfo: requestInfo()
        NeedsInfo --> Reviewing: provideInfo()
    }
```

### 6. Entity Relationship Diagram

```mermaid
erDiagram
    CUSTOMER ||--o{ ORDER : places
    ORDER ||--|{ ORDER_ITEM : contains
    PRODUCT ||--o{ ORDER_ITEM : "ordered in"
    
    CUSTOMER {
        int id PK
        string name
        string email
    }
    
    ORDER {
        int id PK
        int customer_id FK
        date order_date
        decimal total
    }
    
    ORDER_ITEM {
        int id PK
        int order_id FK
        int product_id FK
        int quantity
    }
    
    PRODUCT {
        int id PK
        string name
        decimal price
    }
```

**ER Relationship Notation:**
| Symbol | Meaning |
|--------|---------|
| `\|\|` | Exactly one |
| `o\|` | Zero or one |
| `}o` | Zero or more |
| `}\|` | One or more |

### 7. Pie Chart

```mermaid
pie title Code Distribution by Type
    "Services" : 35
    "Controllers" : 20
    "Repositories" : 15
    "Entities" : 15
    "Utilities" : 10
    "Config" : 5
```

## Best Practices

1. **Keep diagrams focused** - One concept per diagram
2. **Use meaningful names** - Full class/method names, not abbreviations
3. **Add notes** - Clarify complex relationships
4. **Limit size** - Max 15-20 nodes per diagram for readability
5. **Use subgraphs** - Group related components
6. **Consistent styling** - Same patterns across all diagrams

## Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| Special characters in text | Wrap in quotes: `["Text with (parens)"]` |
| Long labels | Use aliases: `participant S as ServiceName` |
| Arrow not rendering | Check arrow syntax matches diagram type |
| Subgraph not working | Ensure proper nesting and closing |

## Embedding in Markdown

Always use fenced code blocks:

~~~markdown
```mermaid
flowchart LR
    A --> B
```
~~~
