# 🧱 Error Response Core Module

**Module name:** `error-response-core`  
**Version:** `1.0.0`  
**Parent project:** ITA Challenges Backend  
**Java version:** 21  
**Spring Boot version:** 3.0.6

---

## 📘 Overview

`error-response-core` is a **shared Spring Boot library** that provides a consistent, standardized way to handle and serialize REST API exceptions across all ITA Challenge microservices.

It centralizes error handling logic into a reusable component, ensuring all services return the same structured JSON format when errors occur — improving maintainability, observability, and client-side debugging.

---

## 🧩 Module Contents

### **Main Components**

| Package                                       | Class | Description |
|-----------------------------------------------|--------|-------------|
| `com.itachallenge.errorcore.builder`          | **`ErrorResponseBuilder`** | Core utility for building structured `APIErrorResponse` objects. Handles argument validation, type mismatches, constraint violations, and general exceptions. |
| `com.itachallenge.errorcore.dto`              | **`APIErrorResponse`** | DTO representing the unified error payload returned to clients (includes timestamp, status, message, error details, path). |
|                                               | **`FieldErrorDto`** | DTO for detailed field-level validation errors (`field`, `objectName`, `message`). |
| `com.itachallenge.errorcore.exceptionhandler` | **GlobalExceptionHandler** | RestControllerAdvice managing validation, type mismatch, and generic exceptions. Will be registered as standalone bean in microservices. |

### **Supporting Resources**

| File                                     | Purpose |
|------------------------------------------|----------|
| `src/main/resources/core-messages.properties` | Contains localized message templates for error and validation responses (e.g., `validation.type_mismatch`, `validation.constraint`). |

---

## ⚙️ Features

✅ Standardized JSON error responses across microservices  
✅ Full support for:
- `ConstraintViolationException`
- `MethodArgumentNotValidException`
- `MethodArgumentTypeMismatchException`
- `ResponseStatusException`
- Generic and unexpected exceptions

✅ Localization-ready messages via `MessageSource` and `core-message.properties`  
✅ Unit-tested
✅ 100% independent — no persistence or service dependencies

---

## 🧩 Example Error Response

```json
{
  "timestamp": "2025-10-22T10:15:30.012Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Parameter 'age' has invalid value 'abc'. Expected type: Integer.",
  "errors": [
    {
      "objectName": "UserController",
      "field": "age",
      "message": "Parameter 'age' has invalid value 'abc'. Expected type: Integer."
    }
  ],
  "path": "/api/users"
}
```
---

## 🚀 Integration Guide

### 1. Include the module in your microservice

In your service’s `build.gradle`:

```groovy
dependencies {
    implementation project(":error-response-core")
}
```
Ensure the module is declared in your root settings.gradle:

```groovy
include(":error-response-core", ":user-service", ":challenge-service")
```

### 2. Ensure Spring register the pacakge's beans:

In your microservice spring App class, add:

```java
@Import(ErrorHandlingConfig.class)
```
That’s it! All framework exceptions in the service will now be intercepted by the GlobalExceptionHandler, and formatted with ErrorResponseBuilder.

If you need to handler locally defined exception, you can create a local Exception Handler (e.g. UserExceptionHandler) and inject ErrorResponseBuilder to build structured and consistent error messages:

```java
@RestControllerAdvice
    @RequiredArgsConstructor
    public class UserExceptionHandler {
        private final ErrorResponseBuilder responseBuilder; 
    }
```

To override the way in which the GlobalExceptionHandler handles certain exception, use @Order(Ordered.HIGHEST_PRECEDENCE) on your local handler and explicitly handles the target exception in your local handler.


### 3. Messages and Localization

You can override the default core-message.properties in your microservice by adding one under:

```css
src/main/resources/message.properties
```
Spring will automatically merge and prioritize your local file.

---

## 🧪 Testing

To run all unit and integration tests with coverage:

```
./gradlew clean test jacocoTestReport
```

The coverage report is available at:
```
build/reports/jacoco/test/html/index.html
```
---

## 📊 SonarQube Integration

This module enforces a minimum 70% line coverage threshold through Jacoco and integrates seamlessly with SonarQube.

The configuration is already compatible with CI/CD workflows (e.g. GitHub Actions).

---

## 🛠️ Development Notes

- The module disables bootJar since it’s not a standalone Spring Boot app — it’s a shared library.
- SonarQube quality gate compliance is enforced with Jacoco reports.
- Uses Spring Boot 3.0.6 and Jakarta Validation API 3.1.0.
- Tested with JUnit 5, Mockito, AssertJ, and Spring Test.
- This library is versioned and updated in sync with other ITA Challenge microservices.

---

## 📦 Versioning

Version	Description	Compatible Spring Boot
-
1.0.0	Initial release with unified error handling	3.0.x








