# Library Management App

A modular Java 21 library management system built with **Domain-Driven Design** and **Hexagonal Architecture**. The project is structured as a Maven multi-module build with strict layer isolation enforced at the module level.

> [!NOTE]
> The project remains in **build-first / skeleton mode** for domain implementation until the Strategist and Tactician design documents are delivered.

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 3.5.14 | Application framework (API module only) |
| Maven | 3.9.15 (via wrapper) | Build & dependency management |
| Spotless + Google Java Format | 1.35.0 | Code formatting |
| Checkstyle | — | Lint / style enforcement |
| MapStruct | 1.6.3 | DTO ↔ domain mapping |
| JUnit 5 + AssertJ + Mockito | (via Spring Boot BOM) | Testing |

---

## Architecture

The project follows Hexagonal Architecture. The domain model has zero framework dependencies and is tested in isolation.

```
┌──────────────────────────────────────┐
│           DRIVING SIDE               │
│     REST API · Scheduled Jobs        │
└────────────────┬─────────────────────┘
                 │ uses
       ┌─────────▼──────────┐
       │  Application Layer  │  ← Use Cases / CQRS
       └─────────┬──────────┘
                 │ uses
       ┌─────────▼──────────┐
       │    Domain Layer     │  ← Aggregates, Entities,
       │  (Pure Java, no FW) │    Value Objects, Domain
       │                     │    Services, Domain Events
       └─────────┬──────────┘
                 │ defines ports (interfaces)
       ┌─────────▼──────────┐
       │  Infrastructure     │  ← JPA, Messaging, Adapters
       └─────────────────────┘
```

### Module Dependency Rules

```
library-shared-kernel   →  (no library-* deps)
library-domain          →  library-shared-kernel
library-application     →  library-domain
library-infrastructure  →  library-domain, library-application
library-api             →  library-application, library-infrastructure
```

---

## Module Structure

| Module | Responsibility |
|---|---|
| `library-shared-kernel` | Shared value objects and cross-cutting primitives |
| `library-domain` | Aggregates, entities, value objects, domain services, domain events, exceptions, and inbound/outbound ports |
| `library-application` | Use-case orchestration — commands, queries, application services |
| `library-infrastructure` | Persistence (JPA), messaging adapters, Spring configuration |
| `library-api` | REST controllers, DTOs, MapStruct mappers, Spring Boot wiring |

<details>
<summary>Detailed package layout</summary>

```
library-domain/src/main/java/.../domain/
├── model/          # Aggregates, Entities, Value Objects
├── event/          # Domain Events
├── exception/      # Domain Exceptions
├── port/
│   ├── in/         # Driving ports (use-case interfaces)
│   └── out/        # Driven ports (repository / service interfaces)
└── service/        # Domain Services

library-application/src/main/java/.../application/
├── command/        # Write-side commands
├── query/          # Read-side queries
└── service/        # Application Services (implement in-ports)

library-infrastructure/src/main/java/.../infrastructure/
├── persistence/    # JPA entities, repositories, mappers
├── messaging/      # Domain event publishing
└── config/         # Spring beans, DB config

library-api/src/main/java/.../api/
├── rest/
│   ├── controller/ # Spring MVC controllers (thin adapters)
│   ├── dto/        # Request / Response DTOs
│   └── mapper/     # DTO ↔ Command/Query mappers (MapStruct)
└── config/         # Application wiring
```

</details>

---

## Prerequisites

- **JDK 21** — required; verify with `java -version`
- Maven is **not** required locally — use the included wrapper (`./mvnw` / `mvnw.cmd`)

---

## Getting Started

```bash
# Clone and enter the project
git clone <repo-url>
cd library-management-app

# Compile all modules
./mvnw compile --no-transfer-progress -DskipTests
```

---

## Running the App

> [!WARNING]
> If you encounter an error with missing **library-application** and **library-infrastructure** dependency, try running `./mvnw install` first.

```bash
# Run via Spring Boot plugin
./mvnw -pl library-api spring-boot:run --no-transfer-progress

# Or build and run the JAR
./mvnw -pl library-api package --no-transfer-progress
java -jar library-api/target/library-api-*.jar
```

If everything is set up correctly, there should be a running static page at `http://localhost:8080/`.

---

## Developer Guide

### Before Every Push

```bash
# Auto-format all sources (Google Java Format)
./mvnw spotless:apply --no-transfer-progress

# Run all checks: compile + format + lint + tests
./mvnw verify --no-transfer-progress
```

Individual checks:

```bash
./mvnw spotless:check --no-transfer-progress   # formatting
./mvnw checkstyle:check --no-transfer-progress  # lint
```

### Test Layers

| Layer | Type | Tooling |
|---|---|---|
| Domain model | Unit | JUnit 5 + AssertJ |
| Application services | Unit (mocked ports) | JUnit 5 + Mockito |
| Infrastructure / Persistence | Integration | `@DataJpaTest` |
| REST API | Slice | `@WebMvcTest` + MockMvc |
| Full flow | Acceptance | `@SpringBootTest` + RestAssured |

### Test Naming Convention

```java
// BDD-style
void should_raise_BookBorrowed_event_when_available_book_is_borrowed() { ... }
void should_throw_BookNotAvailable_when_book_is_already_on_loan() { ... }
```

---

## CI

The GitHub Actions pipeline (`.github/workflows/ci.yml`) runs on every push and pull request to `main`:

| Job | What it checks |
|---|---|
| **Build · Format · Lint** | `mvn compile -DskipTests`, Spotless, Checkstyle |
| **Test Coverage** | `mvn verify` + JaCoCo report uploaded as artifact |
