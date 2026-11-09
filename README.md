# Original Document Return System

A Java web application for a document-return workflow: data entry, review, approval or rejection, and an audit trail of request state changes. Developed as a three-person George Mason University software engineering project.

## Stack

Java 21, Jakarta Servlets, Maven, MySQL, and Apache Tomcat 11. Docker configuration and GitHub Actions provide repeatable build and verification paths.

## Run with Docker

Requires Docker with Compose. Copy `.env.example` to `.env` and replace both example passwords with your own local values, then:

```bash
docker compose up --build
```

Open `http://localhost:8080/OrigDocReturn/`. MySQL uses a project-specific Docker volume. Use synthetic records for demonstrations. Run `docker compose down` to stop the services; the database volume is retained.

## Build and run without Docker

Install JDK 21+, Maven 3.9+, MySQL 8+, and Tomcat 11.

1. Create an empty MySQL database and a user with permission to create tables and read/write records in that database.
2. Copy `origdocreturn-domain/src/main/resources/db.properties.example` to `db.properties` in the same folder and fill in your local connection details. Alternatively set `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, and `DB_PASSWORD` in the Tomcat environment. Environment variables take precedence.
3. Build and deploy:

```bash
cd origdocreturn-domain
mvn clean verify
```

Deploy `target/OrigDocReturn.war` into Tomcat's `webapps/` directory, start Tomcat, and open `http://localhost:8080/OrigDocReturn/`. Startup creates the workflow tables.

## Try the workflow

1. Submit a synthetic request from the data-entry form.
2. Open the review screen and send it to approval, or return it for correction.
3. Open the approval screen and approve or reject the request.
4. State changes and audit records are persisted in MySQL.

## Verification

`mvn clean verify` runs five validation/HTML-escaping tests and builds the WAR. To also run four MySQL workflow integration tests, set `RUN_DB_TESTS=true` and point the five `DB_*` variables above to an **isolated test database**. These tests create synthetic records. GitHub Actions provisions its own MySQL service and runs all nine tests.

See [TESTING.md](TESTING.md) for the checked workflows and limits.

## Structure

- `origdocreturn-domain/src/main/java/edu/gmu/cs321/`: domain models, servlets, workflow services, database access.
- `origdocreturn-domain/src/main/webapp/`: request, review, and approval screens.
- `origdocreturn-domain/src/test/`: validation and MySQL integration tests.
- `.gitlab-ci.yml`: the original coursework CI reference, retained for attribution/history.

## Prototype limits

Workflow roles are represented by separate screens and servlets, but this version does not implement authenticated role enforcement. Notifications are logged to the console; they are not sent as real email. Do not deploy it with real applicant data without adding authentication, authorization, and transactional workflow controls.

Local credentials and database configuration are excluded from source control. The original team members' code comments are retained.
