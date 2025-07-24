# ITA JWT-CORE MODULE

`jwt-core` is a shared library module designed to centralize all JWT (JSON Web Token) logic across ITA Challenge microservices.

## Features

- 🔑 **JWT Token Generation**: Provides utilities to generate access tokens with configurable claims and expiration times.
- ✅ **JWT Validation**: Offers token validation methods to check signature integrity and expiration.
- 👤 **User Roles Handling**: Includes support for role extraction and token regeneration when switching user roles.
- 🧪 **Test Coverage**: Comes with unit tests to ensure reliability and ease of maintenance.
- 🚫 **No BootJar**: This module is a pure library and not a Spring Boot application.

## Usage

Add the dependency to your Spring Boot microservice in `build.gradle`:

```groovy
implementation(project(":jwt-core"))

testImplementation project(':jwt-core')
```

## Project structure
```markdown
jwt-core/
├── src/main/java/com/itachallenge/jwtcore/
│   ├── service/      # JWTService and interfaces
├── src/test/         # Unit tests
├── build.gradle      # Gradle module configuration
└── readme.md         # This text file           
```
## Development notes

- The module disables bootJar since it’s not a standalone Spring Boot app.
- SonarQube quality gate compliance with minimum test coverage is enforced.
- This library is versioned and updated in sync with other ITA Challenge services.