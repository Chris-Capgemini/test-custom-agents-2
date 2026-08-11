# SQL Migration Rules

## Core rules

- Remove schema qualifiers from object definitions unless quoting is explicitly required.
- Do not add a duplicate primary key.
- Preserve indexes, foreign keys, unique constraints, and comments.
- Remove unsupported Oracle-only constructs or rewrite them into PostgreSQL equivalents.
- Ensure no trailing comma remains at the end of `CREATE TABLE` column lists.
- Move inline comments outside table definitions when needed to keep PostgreSQL syntax valid.

## DDL and DML rules

- Keep table and column names aligned between `CREATE TABLE` and `INSERT` statements.
- Use `DELETE FROM`, not bare `DELETE table_name`.
- Replace `SEQUENCE` plus trigger auto-increment patterns with identity columns or explicit `nextval(...)` defaults as appropriate.
- Remove `DEFERRABLE` from `CHECK` constraints because PostgreSQL does not support it.

## Trigger rules

- If an Oracle trigger only assigns `sequence.NEXTVAL` during insert, remove the trigger and migrate the column to `GENERATED ALWAYS AS IDENTITY` or `DEFAULT nextval('sequence_name')`.
- If a trigger contains business logic beyond sequence assignment, convert the trigger function behavior explicitly instead of silently dropping it.
- Distinguish auto-increment triggers from logic-bearing triggers before choosing the migration strategy.
- When trigger logic is preserved, make sure any Oracle-specific syntax inside the body is rewritten for PostgreSQL.

### Trigger conversion pattern

- Oracle `:NEW.column_name` becomes `NEW.column_name` in a PostgreSQL trigger function.
- Oracle `:OLD.column_name` becomes `OLD.column_name`.
- PostgreSQL uses a separate trigger function plus `CREATE TRIGGER` statement.
- Preserve `BEFORE` versus `AFTER` timing and `FOR EACH ROW` semantics unless the source clearly indicates a statement-level alternative.

```sql
CREATE OR REPLACE FUNCTION employees_audit_fn()
RETURNS trigger AS $$
BEGIN
	IF NEW.salary < 0 THEN
		RAISE EXCEPTION 'salary must be non-negative';
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER employees_audit_trg
BEFORE INSERT OR UPDATE ON employees
FOR EACH ROW
EXECUTE FUNCTION employees_audit_fn();
```

- If the Oracle source uses compound triggers, split the behavior into separate PostgreSQL trigger functions or explicit DML logic.
- If trigger logic only enforces data integrity that PostgreSQL can express as a constraint, prefer a real constraint over procedural code.

### Trigger exception handling

- Convert `RAISE_APPLICATION_ERROR` into `RAISE EXCEPTION` with a meaningful PostgreSQL message.
- Convert `WHEN OTHERS` blocks into PostgreSQL exception handlers only when the original logic actually needs them.
- If the Oracle trigger relied on autonomous transaction behavior, call that out explicitly because PostgreSQL trigger functions do not support an equivalent drop-in pattern.

## Type and function conversion

- `NUMBER(p,0)` -> `INTEGER` or `BIGINT` based on size
- `NUMBER(p,s)` -> `NUMERIC(p,s)`
- `VARCHAR2` -> `VARCHAR`
- `DATE` -> `DATE` or `TIMESTAMP` depending on actual usage
- `SYSDATE` -> `CURRENT_TIMESTAMP` or `CURRENT_DATE`
- `SYSTIMESTAMP` -> `CURRENT_TIMESTAMP`
- `ADD_MONTHS` -> interval arithmetic
- `NVL` -> `COALESCE`
- `DBMS_OUTPUT.PUT_LINE` -> `RAISE NOTICE`

## Execution mindset

- Validate object creation and insert compatibility together.
- Preserve comments from the original SQL.
- Prefer explicit PostgreSQL constructs over loose textual rewrites.
