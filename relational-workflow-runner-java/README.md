# Relational Workflow Runner — Java/Maven

Manifest-driven Oracle workflow runner.

## Requirements

- Java 21+
- Maven 3.9+
- Docker + Docker Compose for local Oracle

## Local Oracle

The Docker setup creates two separate accounts:

- `SYSTEM` uses `ORACLE_ADMIN_PASSWORD` and is only for database administration.
- `WORKFLOW` uses `ORACLE_PASSWORD` and is the application/schema account used by the runner.

Create your local environment file:

```bash
cp .env.example .env
```

The development defaults are:

```text
ORACLE_ADMIN_PASSWORD=oracle
ORACLE_USER=workflow
ORACLE_PASSWORD=workflow
ORACLE_HOST=localhost
ORACLE_PORT=1521
ORACLE_SERVICE=FREEPDB1
```

Start Oracle:

```bash
docker compose up -d oracle
```

Docker Compose reads `.env` automatically. Java processes do not, so export the same runner variables into your shell before `--run`:

```bash
set -a
source .env
set +a
```

`ORACLE_ADMIN_PASSWORD` is ignored by the Java runner; it only exists for Docker initialization.

### Existing development volume

The Oracle image creates `APP_USER` only when the database volume is initialized. If you already started an older version of this project with a different application user, reset the local development database once:

```bash
docker compose down -v
docker compose up -d oracle
```

This deletes the local Docker database volume. Do not use that command on a database containing data you need to keep.

You can wait for Oracle to become healthy with:

```bash
docker compose ps
```

## Run

Dry-run does not require database credentials:

```bash
java Run.java --dry-run --manifest packages/example/manifest/manifest.csv
```

Execute against Oracle:

```bash
set -a
source .env
set +a
java Run.java --run --manifest packages/example/manifest/manifest.csv
```

Optional sequence bounds are preserved:

```bash
java Run.java --run --manifest packages/example/manifest/manifest.csv --from 100 --through 240
```

`Run.java` builds `target/relational-workflow-runner.jar` with Maven on first use and then launches it. Developers can use Maven directly:

```bash
mvn test
mvn package
```

## Included example package

`packages/example/` exercises all four artifact kinds:

- `SQL` creates `EXAMPLE_PEOPLE`
- `LOAD_CSV` loads three sample rows
- `VERIFY` succeeds only when the expected rows are present
- `EXPORT` writes `packages/example/output/people_export.csv`

Run the complete example:

```bash
java Run.java --dry-run --manifest packages/example/manifest/manifest.csv

set -a
source .env
set +a
java Run.java --run --manifest packages/example/manifest/manifest.csv

cat packages/example/output/people_export.csv
```

Expected export:

```text
PERSON_ID,FULL_NAME,EMAIL
1,Ada Lovelace,ada@example.test
2,Grace Hopper,grace@example.test
3,Alan Turing,alan@example.test
```

## Tests

The unit suite does not require Oracle. It covers manifest parsing and ordering, duplicate sequence rejection, package-root path containment, and CSV quoting/escaping behavior:

```bash
mvn test
```
