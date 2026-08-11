# Review Checklist

- Verify every inserted table exists in the migrated DDL.
- Verify every inserted column exists with the same name.
- Verify Oracle functions and syntax are fully removed or replaced.
- Verify comments are preserved.
- Verify check constraints are not marked `DEFERRABLE`.
- Verify auto-increment strategy is internally consistent.
- Verify sequence-only triggers were removed only after the replacement identity or `nextval(...)` strategy was applied.
- Verify logic-bearing triggers were either converted explicitly or called out as remaining work.
- Verify the output is likely to execute without relation or column lookup failures.
