# Library Management App

Modular Java 21 project for a library management system, organized with DDD-style layers.

> [!CAUTION]
> Runtime bootstrap class (`@SpringBootApplication`) is not yet present, so the app is currently in build-first mode.

## Tech Stack

- Java 21
- Maven (multi-module build)
- Spring Boot (API module wiring)
- Spotless + Google Java Format
- Checkstyle

## Prerequisites

Install the following locally:

- JDK 21
- Maven 3.9+

Verify tools:

- `java -version`
- `mvn -version`

## Setup

1. Clone the repository.
2. Open the root directory.
3. Compile all modules from project root:

	 `mvn compile --no-transfer-progress -DskipTests`



## How to Run the App

### Once the main class is added

- From root:

	`mvn -pl library-api spring-boot:run --no-transfer-progress`

- Or package first, then run JAR (example workflow):

	`mvn -pl library-api package --no-transfer-progress`

## Developer Guide

### File Structure Summary

- `library-shared-kernel` - shared value objects and cross-cutting abstractions.
- `library-domain` - entities, domain services, domain events, domain exceptions, and inbound ports.
- `library-application` - use-case orchestration (commands, queries, application services).
- `library-infrastructure` - persistence, messaging, and technical adapters.
- `library-api` - REST controllers, DTOs, mappers, and application wiring.
- `config/checkstyle/checkstyle.xml` - linting rules used by Checkstyle.
- `.github/workflows/ci.yml` - CI pipeline definition.

### Commands to Run After Implementation

Use these before pushing changes:

- Auto-format sources:

	`mvn spotless:apply --no-transfer-progress`

- Verify formatting (same rule set as CI):

	`mvn spotless:check --no-transfer-progress`

- Run linter (same rule set as CI):

	`mvn checkstyle:check --no-transfer-progress`

- Compile all modules:

	`mvn compile --no-transfer-progress -DskipTests`

### Run All Maven Checks

Recommended single command from root:

- `mvn verify --no-transfer-progress`

Why this command:

- Runs full Maven lifecycle up to `verify`.
- Executes Spotless and Checkstyle checks bound to `verify` in parent POM.
- Includes tests when test classes are present.

## CI Overview

Current CI workflow validates:

- Build (compile, tests skipped)
- Formatting compliance (Spotless)
- Lint compliance (Checkstyle)

Coverage job template exists in CI and can be enabled later when tests and coverage thresholds are finalized.
