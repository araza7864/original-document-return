# Verification

Run `mvn clean verify` from `origdocreturn-domain/` for validation tests and WAR packaging.

For MySQL integration tests, set `RUN_DB_TESTS=true` with `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, and `DB_PASSWORD` pointing to an isolated test database. The test user needs permission to create the two workflow tables and read/write test records.

The nine tests cover:

- Valid form data, missing/blank values, draft-to-review state, and HTML escaping.
- Submission through review and approval, including persisted reviewer comments and four audit entries.
- Rejection and return-to-data-entry persistence.
- Lookup of a missing request and rejection of repeated approval.

The workflow integration tests were verified against an isolated local MySQL 8 instance. Frontend servlet deployment and Docker startup are separate from these tests. Notifications remain console-only and role-based authentication is not implemented.
