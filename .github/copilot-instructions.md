# Library Management App — Copilot Instructions

## 1. Architecture Overview

Hexagonal (Ports & Adapters) + DDD. Five Maven modules with a strict one-way dependency chain:

```
library-shared-kernel → library-domain → library-application → library-infrastructure → library-api
```

- `library-shared-kernel` — zero dependencies; pure Java primitives
- `library-domain` — zero framework dependencies; pure domain model
- `library-application` — orchestrates use cases; depends only on domain ports
- `library-infrastructure` — Spring Data JPA + SQLite adapters; implements domain ports
- `library-api` — Spring Boot REST entry point; delegates to application services

**Hard rule**: never import Spring, JPA, or any framework class in `library-shared-kernel` or `library-domain`.

Base package: `com.example.library`

## 2. Package Layout

| Module | Sub-packages |
|--------|-------------|
| `library-shared-kernel` | `shared` |
| `library-domain` | `domain.model`, `domain.factory`, `domain.service`, `domain.event`, `domain.exception`, `domain.port.in`, `domain.port.out` |
| `library-application` | `application.service`, `application.command`, `application.query` |
| `library-infrastructure` | `infrastructure.persistence.entity`, `infrastructure.persistence.mapper`, `infrastructure.persistence`, `infrastructure.messaging`, `infrastructure.adapter`, `infrastructure.config` |
| `library-api` | `api.rest.controller`, `api.rest.dto`, `api.rest.mapper`, `api.rest` |

## 2A. Bounded Context Folder Blueprint (Lending + Future Contexts)

Each bounded context must be structured as three modules under one context root:

```
{context}/
	{context}-domain/
	{context}-application/
	{context}-infrastructure/
```

For this repository, keep package root naming in the form:
- `com.example.library.{context}.domain.*`
- `com.example.library.{context}.application.*`
- `com.example.library.{context}.infrastructure.*`

Layer responsibilities from outer to inner:
- Infrastructure layer (outer): input adapters and output adapters.
- Application layer (inner): application services and application ports.
- Domain layer (inner): business model (aggregates/entities/value objects) and domain services.

Role-based package names for every bounded context:
- `infrastructure`: `in`, `out`, `common` (example: `in.web`, `out.persistence`).
- `application`: `port.in`, `port.out`, `service`, `common`.
- `domain`: `model`, `service`, `common`.

Notes for the current codebase:
- Existing domain structures such as `domain.copy`, `domain.loan`, `domain.reservation`, and `domain.factory` are valid.
- New development should place use-case entry contracts in `application.port.in` and driven contracts in `application.port.out`.

## 3. Shared Kernel — Base Types

All in `com.example.library.shared`. Use exactly these base types:

| Type | Rule |
|------|------|
| `Entity<ID>` | Extend for all entities and aggregate roots. Provides ID-based `equals`/`hashCode`. |
| `AggregateRoot<ID> extends Entity<ID>` | Extend for aggregate roots. Call `registerEvent(e)` inside business methods. Only application services call `pullDomainEvents()`. |
| `DomainEvent` | Implement as a Java **record** with `Instant occurredOn()`. Never as a plain class. |
| `DomainException` | Extend for every domain-specific runtime exception. |
| `Repository<T extends AggregateRoot<ID>, ID>` | Extend in every `port/out` repository interface. Provides `findById` and `save`. |
| `DomainService` | Empty marker interface. Every domain service class implements it. Enables ArchUnit verification. |

No `ValueObject` marker — Java records already enforce immutability and structural equality.

## 4. DDD Conventions

### Value Objects
- Implemented as Java **records** — immutable by default, structural equality for free.
- Place in `domain/model/`.

### Factories
- Factories are **separate classes** in `domain/factory/` — never static factory methods on the aggregate.
- Each factory has a meaningful method name expressing Ubiquitous Language: `open(...)`, `place(...)`, `create(...)`.
- Factory methods set initial status and register the corresponding domain event.

### Aggregates
- Business method guards throw a `DomainException` subclass; never return null or silently fail.
- Aggregates register events; they do NOT publish them.
- Event causal chain: `ApplicationService` → calls `DomainService` or `Aggregate method` → `registerEvent()` → `ApplicationService.pullDomainEvents()` → publish via `NotificationPort`.

### Domain Events
- Implemented as Java **records** implementing `DomainEvent` with `Instant occurredOn()`.
- Raised inside aggregate business methods or factory methods; never outside the domain layer.

### Domain Exceptions
- One exception class per distinct failure reason, extending `DomainException`.
- Place in `domain/exception/`.

### Domain Services
- Implement the `DomainService` marker interface.
- Place in `domain/service/`.
- Used for logic that spans multiple aggregates or doesn't naturally belong to one.

### Repositories (port/out)
- Interfaces live in `domain/port/out/`.
- All extend `Repository<T, ID>` from the shared kernel.
- Add query methods only when needed by a specific use case.

### Application Services
- Implement the corresponding `port/in` use case interface.
- Inject only `port/out` interfaces — never JPA repositories or Spring beans directly.
- After every state-changing operation: call `aggregate.pullDomainEvents()` and publish via `NotificationPort`.

### JPA Entities
- `*JpaEntity` classes live only in `infrastructure/persistence/entity/`.
- Never extend or reference domain classes.
- MapStruct mappers (`*JpaMapper`) in `infrastructure/persistence/mapper/` handle the translation.

### Stub Adapters (Anti-Corruption Layer)
- Used for bounded contexts that are out of scope (e.g. Catalog, Users).
- Implement the relevant `port/out` interface with an in-memory stub.
- Place in `infrastructure/adapter/`.

## 5. Ubiquitous Language — Polish → English

| Polish | English |
|--------|---------|
| Wypożyczenie | Loan |
| Egzemplarz | BookCopy |
| Rezerwacja | Reservation |
| Czytelnik | Patron |
| IdentyfikatorWypożyczenia | LoanId |
| IdentyfikatorEgzemplarza | CopyId |
| IdentyfikatorCzytelnika | PatronId |
| IdentyfikatorRezerwacji | ReservationId |
| OkresWypożyczenia | LoanPeriod |
| StatusEgzemplarza | CopyStatus |
| StatusRezerwacji | ReservationStatus |
| StatusWypożyczenia | LoanStatus |
| KsiążkaWypożyczona | BookBorrowed |
| KsiążkaZwrócona | BookReturned |
| EgzemplarzZarezerwowany | CopyReserved |
| RezerwacjaAnulowana | ReservationCancelled |
| TerminZwrotuPrzekroczony | LoanOverdue |
| SprawdzenieLimitówWypożyczeń | LoanLimitPolicy (domain service) |
| SprawdzenieDostępnościEgzemplarza | CopyAvailabilityChecker (domain service) |
| ObsługaRezerwacji | ReservationService (domain service) |

Always use the English names in code. Use the Polish terms only when communicating with domain experts.

## 6. Naming Conventions

### Methods
- **No `get`/`set` prefixes.** Accessor methods are named after the field they return: `id()`, `status()`, `period()`.
- **Mutating methods use Ubiquitous Language verbs** that express the business intent: `returnCopy()`, `cancel()`, `markOverdue()` — never `setStatus(...)`.
- Command/query methods on use case interfaces follow the same rule: `borrowBook(...)`, `reserveCopy(...)`, not `executeBorrow(...)` or `performReservation(...)`.

## 7. SOLID & Design Principles

- **SRP**: each class has one reason to change — aggregates model behaviour, JPA entities model persistence, mappers handle translation.
- **OCP**: extend behaviour by adding new use case implementations or new adapters, not by modifying existing domain classes.
- **LSP**: subtypes of `Entity`, `AggregateRoot`, and `DomainException` must be substitutable without breaking invariants.
- **ISP**: port interfaces are narrow and use-case-specific; avoid fat interfaces.
- **DIP**: all cross-layer dependencies point inward via interfaces (`port/in`, `port/out`); no concrete infrastructure class is ever referenced from the domain or application layer.

## 8. Visibility Rules

Use package-private visibility by default.

- Package-private classes are preferred when the class is only used inside its module/package role.
- Public classes are required only when another module/layer needs access.

Practical boundaries:
- Classes in `application.port.*` must be public (consumed by adapters and application services).
- Classes in `application.service` must be public if infrastructure calls them directly.
- Classes in `infrastructure.out` may stay package-private and be exposed only via implemented ports.
- Classes in `domain.service` may stay package-private if invoked through a public application port/service.

Dependency injection notes:
- Configuration is the outermost layer and can wire classes from all layers.
- If a framework requires classpath scanning, classes discovered by scanning may need public visibility.

## 9. .gitkeep Policy For Context Scaffolding

When creating a new bounded context or pre-creating package roles:

- Create placeholder directories in both `src/main/java` and `src/test/main/java`.
- Add `.gitkeep` to every empty placeholder directory.
- Remove `.gitkeep` from a directory once the first real class is added there.

Minimum placeholder set per context:
- Domain: `domain/model`, `domain/service`, `domain/common`.
- Application: `application/port/in`, `application/port/out`, `application/service`, `application/common`.
- Infrastructure: `infrastructure/in`, `infrastructure/out`, `infrastructure/common`, `infrastructure/config`.

## 10. Architecture Checklist For New Contexts

Before starting implementation in a new bounded context:

1. Create `{context}-domain`, `{context}-application`, and `{context}-infrastructure` modules.
2. Create role-based directories for each layer and add `.gitkeep` placeholders.
3. Keep dependency direction inward: infrastructure -> application -> domain.
4. Define use-case ports in `application.port.in` and driven ports in `application.port.out`.
5. Keep framework imports out of domain code.
6. Keep package root as `com.example.library.{context}.*`.

## 11. Build & Verify

```bash
./mvnw spotless:apply          # fix formatting (Google Java Format)
./mvnw verify                  # compile + checkstyle + all tests
./mvnw -pl library-api spring-boot:run   # run the application
```

Run `spotless:apply` then `verify` before every push.
