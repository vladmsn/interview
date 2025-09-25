# Auto Repair Shop DVI Coding Exercise
---
Spring Boot service for managing **vehicle inspections** and **inspection items** in a repair shop.

## Features

- CRUD for **Inspections** (aggregate) and **Inspection Items** (child)
- Simple workflow: `DRAFT → SUBMITTED` (mutations blocked after submit)
- Path-based **RBAC** (GET vs. mutate vs. delete)
- **OpenAPI/Swagger** documentation
- **Flyway** database migrations (H2 in-memory for the exercise)
---

## Prerequisites
- Maven 
- Java 21

## Running Locally
```bash
# Build
mvn clean package

# Run
java -jar target/interview-1.0-SNAPSHOT.jar
```

#### Dev urls
- Swagger UI: http://localhost:8080/swagger-ui.html

- OpenAPI JSON: http://localhost:8080/v3/api-docs

- H2 Console: http://localhost:8080/h2-console (JDBC: jdbc:h2:mem:testdb, user: sa, password: password)

---

### Domain Model

**Entities**:
- Inspection: technician's vehicle inspection report (`vin`, `status`, `note`, `recommendation`, `estimatedCost`, timestamps)
- InspectionItem: individual finding (`category`, `severity`, `note`, timestamps)

**Migrations**:
- Located in `src/main/resources/database/migration`
- Baseline migration `V1__Initial_Setup.sql` creates tables and constraints. 

---    

### API
1. Inspection Management:
   - Create, Read, Update, Delete inspections.
   - State transitions: Draft -> Submitted -> Reviewed.
2. Inspection Item Management:
   - Add, Read, Update, Delete inspection items within an inspection.

| Endpoint                          | Methods  | Description                                                    | Required Role          |
|-----------------------------------|----------|----------------------------------------------------------------|------------------------|
| `/api/v1/inspections/{id}`        | `GET`    | Get inspection by id                                           | `USER`/`STAFF`/`ADMIN` |
| `/api/v1/inspections`             | `GET`    | List inspections (pagination; optional `vin` filter)           | `USER`/`STAFF`/`ADMIN` |
| `/api/v1/inspections`             | `POST`   | Create new inspection (starts in `DRAFT`)                      | `STAFF`/`ADMIN`        |
| `/api/v1/inspections/{id}`        | `PUT`    | Update note/recommendation/estimatedCost (**only in `DRAFT`**) | `STAFF`/`ADMIN`        |
| `/api/v1/inspections/{id}`        | `DELETE` | Delete inspection (**admin-only**; hard delete while `DRAFT`)  | `ADMIN`                |
| `/api/v1/inspections/{id}/submit` | `POST`   | Transition `DRAFT → SUBMITTED`                                 | `STAFF`/`ADMIN`        |
<br/>

After `SUBMITTED`, editing or deleting items is rejected with `409 Conflict`.

<br/>

| Endpoint                                  | Methods  | Description                       | Required Role          |
|-------------------------------------------|----------|-----------------------------------|------------------------|
| `/api/v1/inspections/{id}/items`          | `GET`    | List items (pagination)           | `USER`/`STAFF`/`ADMIN` |
| `/api/v1/inspections/{id}/items`          | `POST`   | Add item (**only in `DRAFT`**)    | `STAFF`/`ADMIN`        |
| `/api/v1/inspections/{id}/items/{itemId}` | `PUT`    | Update item (**only in `DRAFT`**) | `STAFF`/`ADMIN`        |
| `/api/v1/inspections/{id}/items/{itemId}` | `DELETE` | Delete item (**only in `DRAFT`**) | `ADMIN`                |


---

### Demo Keys & Tokens (dev only)

Generate key pair (already wired for RS256):
```bash
# Private key (test-only)
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out src/test/resources/keys/private.pem
# Public key (used by server)
openssl rsa -pubout -in src/test/resources/keys/private.pem -out src/main/resources/keys/public.pem
```

***Disclaimer***: Keys and tokens are development-only and intentionally long-lived for convenience.

---
### Configuration

- **App config**: src/main/resources/application.yml
- **Test config**: src/test/resources/application-test.yml
- **H2**: jdbc:h2:mem:testdb (user sa, password password)
- **Swagger/OpenAPI** enabled by default