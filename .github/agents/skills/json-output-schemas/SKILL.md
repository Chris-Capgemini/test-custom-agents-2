---
name: json-output-schemas
description: Provides JSON schemas for intermediate analysis files used by GenInsights agents. Use this skill when creating or reading JSON files in the .geninsights/analysis/ folder to ensure consistent data structures.
---

# JSON Output Schemas Skill

This skill defines the JSON schemas for all intermediate analysis files created by GenInsights agents.

## Output Location

All JSON files go in: `.geninsights/analysis/`

## 1. Analysis Results Schema (analysis_results.json)

Created by: **documentor-agent**

```json
{
  "analysis_metadata": {
    "timestamp": "2026-02-05T10:30:00Z",
    "total_files_analyzed": 47,
    "languages_detected": ["java", "xml"]
  },
  "files": [
    {
      "file_path": "src/main/java/com/example/OrderService.java",
      "file_name": "OrderService.java",
      "language": "java",
      "classification": "business",
      "summary": "Service class handling order processing operations",
      "description": "Manages the complete order lifecycle including creation, validation, payment processing, and fulfillment",
      "key_responsibilities": [
        "Order creation and validation",
        "Payment processing coordination",
        "Inventory reservation"
      ],
      "methods": [
        {
          "name": "createOrder",
          "visibility": "public",
          "return_type": "Order",
          "parameters": ["OrderRequest request"],
          "purpose": "Creates a new order from the given request",
          "complexity": "medium"
        }
      ],
      "dependencies": [
        "PaymentService",
        "InventoryService",
        "OrderRepository"
      ],
      "annotations": ["@Service", "@Transactional"],
      "lines_of_code": 245
    }
  ]
}
```

## 2. Business Rules Schema (business_rules.json)

Created by: **business-rules-agent**

```json
{
  "extraction_metadata": {
    "timestamp": "2026-02-05T10:45:00Z",
    "total_rules_extracted": 31,
    "total_workflows_identified": 4
  },
  "business_rules": [
    {
      "id": "BR-001",
      "name": "Order Minimum Value",
      "category": "validation",
      "description": "Orders must have a minimum total value of $10",
      "condition": "order.total >= 10.00",
      "action": "Allow order to proceed",
      "exception": "Reject order with error message",
      "source_file": "src/main/java/com/example/OrderValidator.java",
      "source_method": "validateMinimumOrder",
      "source_line": 45,
      "priority": "high",
      "related_rules": ["BR-002", "BR-003"]
    },
    {
      "id": "BR-002",
      "name": "Discount Calculation",
      "category": "calculation",
      "description": "Apply 10% discount for orders over $100",
      "formula": "if (total > 100) discount = total * 0.10",
      "source_file": "src/main/java/com/example/PricingService.java",
      "source_method": "calculateDiscount",
      "source_line": 78,
      "priority": "medium"
    }
  ],
  "rule_categories": {
    "validation": 12,
    "calculation": 8,
    "authorization": 5,
    "workflow": 4,
    "constraint": 2
  },
  "workflows": [
    {
      "id": "WF-001",
      "name": "Order Processing Workflow",
      "description": "Complete flow from order creation to fulfillment",
      "steps": [
        {"step": 1, "action": "Validate order", "decision": false},
        {"step": 2, "action": "Check inventory", "decision": true},
        {"step": 3, "action": "Process payment", "decision": true},
        {"step": 4, "action": "Reserve inventory", "decision": false},
        {"step": 5, "action": "Confirm order", "decision": false}
      ],
      "entry_point": "OrderService.createOrder()",
      "actors": ["Customer", "System", "Payment Gateway"]
    }
  ]
}
```

## 3. Code Assessment Schema (code_assessment.json)

Created by: **code-assessment-agent**

```json
{
  "assessment_metadata": {
    "timestamp": "2026-02-05T11:00:00Z",
    "files_reviewed": 47,
    "overall_health_score": 72
  },
  "summary": {
    "total_issues": 25,
    "by_severity": {
      "critical": 1,
      "high": 4,
      "medium": 12,
      "low": 8
    },
    "by_category": {
      "security": 2,
      "performance": 5,
      "maintainability": 10,
      "reliability": 8
    }
  },
  "issues": [
    {
      "id": "ISS-001",
      "severity": "critical",
      "category": "security",
      "title": "SQL Injection Vulnerability",
      "description": "User input directly concatenated into SQL query",
      "file_path": "src/main/java/com/example/UserRepository.java",
      "line_number": 45,
      "code_snippet": "String query = \"SELECT * FROM users WHERE name = '\" + name + \"'\";",
      "recommendation": "Use parameterized queries or prepared statements",
      "effort_to_fix": "low",
      "references": ["OWASP SQL Injection"]
    }
  ],
  "technical_debt": [
    {
      "id": "TD-001",
      "title": "Legacy authentication mechanism",
      "description": "Custom authentication should be replaced with Spring Security",
      "affected_files": ["AuthService.java", "AuthFilter.java"],
      "estimated_effort": "3 days",
      "priority": "medium",
      "business_impact": "Security risk, maintenance burden"
    }
  ],
  "improvement_suggestions": [
    {
      "id": "IMP-001",
      "title": "Add caching layer",
      "description": "Frequently accessed data should be cached",
      "affected_components": ["ProductService", "CategoryService"],
      "expected_benefit": "50% reduction in database load",
      "effort": "medium"
    }
  ],
  "metrics": {
    "average_complexity": 8.5,
    "max_complexity": 25,
    "files_above_threshold": 3,
    "test_coverage_estimate": "medium",
    "documentation_level": "low"
  }
}
```

## 4. Architecture Analysis Schema (architecture_analysis.json)

Created by: **architecture-agent**

```json
{
  "analysis_metadata": {
    "timestamp": "2026-02-05T11:15:00Z",
    "architecture_style": "Layered Monolith"
  },
  "layers": [
    {
      "name": "Presentation",
      "components": ["OrderController", "ProductController", "UserController"],
      "responsibilities": ["REST API endpoints", "Request/Response handling"],
      "dependencies": ["Business Layer"]
    },
    {
      "name": "Business",
      "components": ["OrderService", "ProductService", "PaymentService"],
      "responsibilities": ["Business logic", "Orchestration"],
      "dependencies": ["Data Access Layer"]
    },
    {
      "name": "Data Access",
      "components": ["OrderRepository", "ProductRepository", "UserRepository"],
      "responsibilities": ["Database operations", "Data mapping"],
      "dependencies": ["Database"]
    }
  ],
  "components": [
    {
      "name": "OrderService",
      "layer": "Business",
      "type": "Service",
      "responsibilities": ["Order processing", "Payment coordination"],
      "dependencies": ["PaymentService", "InventoryService", "OrderRepository"],
      "dependents": ["OrderController"],
      "interfaces_provided": ["createOrder", "cancelOrder", "getOrderStatus"],
      "interfaces_required": ["processPayment", "reserveInventory"]
    }
  ],
  "patterns_detected": [
    {
      "pattern": "Repository Pattern",
      "description": "Data access abstraction through repository interfaces",
      "locations": ["*Repository.java"],
      "benefit": "Separation of data access from business logic"
    },
    {
      "pattern": "Service Layer",
      "description": "Business logic encapsulated in service classes",
      "locations": ["*Service.java"],
      "benefit": "Reusable business operations"
    }
  ],
  "external_dependencies": [
    {
      "name": "PostgreSQL",
      "type": "Database",
      "connection": "JDBC",
      "used_by": ["*Repository"]
    },
    {
      "name": "Stripe API",
      "type": "External Service",
      "connection": "REST",
      "used_by": ["PaymentService"]
    }
  ]
}
```

## 5. Capability Mapping Schema (capability_mapping.json)

Created by: **capability-mapping-agent**

```json
{
  "mapping_metadata": {
    "timestamp": "2026-02-05T11:30:00Z",
    "total_capabilities": 12,
    "total_files_mapped": 47
  },
  "capability_hierarchy": {
    "name": "E-Commerce Platform",
    "children": [
      {
        "name": "Order Management",
        "children": [
          {"name": "Order Creation", "files": 3},
          {"name": "Order Fulfillment", "files": 2},
          {"name": "Order Tracking", "files": 2}
        ]
      },
      {
        "name": "Product Management",
        "children": [
          {"name": "Catalog Management", "files": 4},
          {"name": "Inventory Management", "files": 3}
        ]
      },
      {
        "name": "Customer Management",
        "children": [
          {"name": "Authentication", "files": 3},
          {"name": "Profile Management", "files": 2}
        ]
      }
    ]
  },
  "capabilities": [
    {
      "id": "CAP-001",
      "name": "Order Creation",
      "description": "Ability to create and validate new orders",
      "parent": "Order Management",
      "files": [
        "src/main/java/com/example/OrderService.java",
        "src/main/java/com/example/OrderValidator.java",
        "src/main/java/com/example/OrderController.java"
      ],
      "business_rules": ["BR-001", "BR-002", "BR-003"],
      "coverage": "full"
    }
  ],
  "unmapped_files": [
    {
      "file": "src/main/java/com/example/utils/StringUtils.java",
      "reason": "Utility class, no direct business capability"
    }
  ],
  "coverage_summary": {
    "files_with_capability": 42,
    "files_without_capability": 5,
    "coverage_percentage": 89.4
  }
}
```

## 6. UML Analysis Schema (uml_analysis.json)

Created by: **uml-agent**

```json
{
  "analysis_metadata": {
    "timestamp": "2026-02-05T11:45:00Z",
    "diagrams_generated": 8
  },
  "class_diagrams": [
    {
      "id": "CD-001",
      "name": "Domain Model",
      "scope": "Entity classes",
      "classes": ["Order", "OrderItem", "Product", "Customer"],
      "relationships": [
        {"from": "Order", "to": "OrderItem", "type": "composition"},
        {"from": "Order", "to": "Customer", "type": "association"},
        {"from": "OrderItem", "to": "Product", "type": "association"}
      ]
    }
  ],
  "sequence_diagrams": [
    {
      "id": "SD-001",
      "name": "Order Creation Flow",
      "actors": ["User", "OrderController", "OrderService", "PaymentService", "OrderRepository"],
      "steps": [
        {"from": "User", "to": "OrderController", "message": "POST /orders"},
        {"from": "OrderController", "to": "OrderService", "message": "createOrder(request)"},
        {"from": "OrderService", "to": "PaymentService", "message": "processPayment(order)"},
        {"from": "OrderService", "to": "OrderRepository", "message": "save(order)"},
        {"from": "OrderRepository", "to": "OrderService", "message": "Order"},
        {"from": "OrderService", "to": "OrderController", "message": "OrderResponse"},
        {"from": "OrderController", "to": "User", "message": "201 Created"}
      ]
    }
  ],
  "use_case_diagrams": [
    {
      "id": "UC-001",
      "name": "Order Management Use Cases",
      "actors": ["Customer", "Admin"],
      "use_cases": [
        {"name": "Create Order", "actor": "Customer"},
        {"name": "View Order", "actor": "Customer"},
        {"name": "Cancel Order", "actor": "Customer"},
        {"name": "Manage Orders", "actor": "Admin"}
      ]
    }
  ]
}
```

## 7. BPMN Workflows Schema (bpmn_workflows.json)

Created by: **bpmn-agent**

```json
{
  "analysis_metadata": {
    "timestamp": "2026-02-05T12:00:00Z",
    "workflows_documented": 4
  },
  "workflows": [
    {
      "id": "BPMN-001",
      "name": "Order Processing",
      "description": "End-to-end order processing workflow",
      "trigger": "Customer submits order",
      "outcome": "Order fulfilled or cancelled",
      "actors": ["Customer", "System", "Payment Gateway", "Warehouse"],
      "steps": [
        {
          "id": "S1",
          "type": "start",
          "name": "Order Received"
        },
        {
          "id": "S2",
          "type": "task",
          "name": "Validate Order",
          "actor": "System",
          "source_method": "OrderValidator.validate()"
        },
        {
          "id": "S3",
          "type": "gateway",
          "name": "Is Valid?",
          "condition": "validation.isValid()"
        },
        {
          "id": "S4",
          "type": "task",
          "name": "Process Payment",
          "actor": "Payment Gateway",
          "source_method": "PaymentService.process()"
        },
        {
          "id": "S5",
          "type": "gateway",
          "name": "Payment Successful?",
          "condition": "payment.isSuccessful()"
        },
        {
          "id": "S6",
          "type": "task",
          "name": "Reserve Inventory",
          "actor": "Warehouse",
          "source_method": "InventoryService.reserve()"
        },
        {
          "id": "S7",
          "type": "task",
          "name": "Confirm Order",
          "actor": "System",
          "source_method": "OrderService.confirm()"
        },
        {
          "id": "S8",
          "type": "end",
          "name": "Order Complete"
        }
      ],
      "flows": [
        {"from": "S1", "to": "S2"},
        {"from": "S2", "to": "S3"},
        {"from": "S3", "to": "S4", "label": "Yes"},
        {"from": "S3", "to": "S8", "label": "No (Invalid)"},
        {"from": "S4", "to": "S5"},
        {"from": "S5", "to": "S6", "label": "Yes"},
        {"from": "S5", "to": "S8", "label": "No (Failed)"},
        {"from": "S6", "to": "S7"},
        {"from": "S7", "to": "S8"}
      ],
      "error_handling": [
        {"at_step": "S4", "error": "PaymentException", "action": "Notify customer, cancel order"}
      ]
    }
  ]
}
```

## Schema Validation Tips

1. **Required fields**: `timestamp`, `id`, `name` for all entries
2. **File paths**: Always use relative paths from project root
3. **References**: Use consistent IDs (BR-XXX, ISS-XXX, etc.)
4. **Cross-references**: Link related items across schemas
5. **Arrays**: Always use arrays even for single items (for consistency)
