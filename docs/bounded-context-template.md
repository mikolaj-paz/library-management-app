# Bounded Context Template

Use this template to scaffold a new bounded context in this repository.

## Naming

- Context folder: lowercase business term, for example: `catalog`, `patron`, `lending`.
- Module names:
  - `{context}-domain`
  - `{context}-application`
  - `{context}-infrastructure`
- Package root must stay: `com.example.library.{context}.*`

## Layer Order (Outer -> Inner)

1. Infrastructure: input and output adapters.
2. Application: application services and application ports.
3. Domain: business model and domain services.

## Role Packages

- Infrastructure: `in`, `out`, `common`, `config`
- Application: `port.in`, `port.out`, `service`, `common`
- Domain: `model`, `service`, `common`

## Placeholder Structure

Create directories in both `src/main/java` and `src/test/main/java` and add `.gitkeep` in every empty directory.

```text
{context}/
  pom.xml
  {context}-domain/
    pom.xml
    src/main/java/com/example/library/{context}/domain/
      model/.gitkeep
      service/.gitkeep
      common/.gitkeep
    src/test/main/java/com/example/library/{context}/domain/
      model/.gitkeep
      service/.gitkeep
      common/.gitkeep
  {context}-application/
    pom.xml
    src/main/java/com/example/library/{context}/application/
      port/in/.gitkeep
      port/out/.gitkeep
      service/.gitkeep
      common/.gitkeep
    src/test/main/java/com/example/library/{context}/application/
      port/in/.gitkeep
      port/out/.gitkeep
      service/.gitkeep
      common/.gitkeep
  {context}-infrastructure/
    pom.xml
    src/main/java/com/example/library/{context}/infrastructure/
      in/.gitkeep
      in/web/.gitkeep
      out/.gitkeep
      out/persistence/.gitkeep
      out/messaging/.gitkeep
      common/.gitkeep
      config/.gitkeep
    src/test/main/java/com/example/library/{context}/infrastructure/
      in/.gitkeep
      in/web/.gitkeep
      out/.gitkeep
      out/persistence/.gitkeep
      out/messaging/.gitkeep
      common/.gitkeep
      config/.gitkeep
```

## Checklist

1. `.gitkeep` exists in all empty placeholder folders.
2. Ports are defined in `application.port.in` and `application.port.out`.
3. Infrastructure implements ports and does not leak into domain.
4. Domain has no framework imports.
5. Dependency direction remains inward.
