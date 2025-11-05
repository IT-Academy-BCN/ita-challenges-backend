# 🧱 Error Response Core Module

**Module name:** `error-response-core`  
**Version:** `1.0.0`  
**Parent project:** ITA Challenges Backend  
**Java version:** 21  
**Spring Boot version:** 3.0.6

---

## 📘 Overview

`error-response-core` is a **shared Spring Boot library** providing a consistent, standardized way to handle and serialize REST API exceptions across all ITA Challenge microservices.

It centralizes error-handling logic into a reusable component, ensuring every service returns the same structured JSON format when errors occur — improving maintainability, observability, and client-side debugging.

---

## 🧩 Module Contents

### **Main Components**

| Package | Class                        | Description                                                                                                                                                                           |
|----------|------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `com.itachallenge.errorcore.builder` | **`ErrorResponseBuilder`**   | Core utility that builds `APIErrorResponse` objects. Interprets validation errors, type mismatches, and generic exceptions, mapping them to localized messages and HTTP status codes. |
| `com.itachallenge.errorcore.dto` | **`APIErrorResponse`**       | DTO representing the unified error payload returned to clients (ISO-8601 `timestamp`, status, error, message, path, and optional field errors).                                       |
|  | **`FieldErrorDto`**          | DTO containing per-field validation details (`field`, `objectName`, `message`).                                                                                                       |
| `com.itachallenge.errorcore.exception` | **`BaseApiException`**       | Abstract base class for service-defined user-facing exceptions — encapsulates ApiErrorInfo as a field.                                                                                |
| `com.itachallenge.errorcore.exception` | **`ApiErrorInfo`**           | Record class which encapsulates all the information needed to build error objects with i18n messages: httpStatus, messagekey (reference to messages.properties file), args to populate the message if needed .                                        |
| `com.itachallenge.errorcore.exceptionhandler` | **`GlobalExceptionHandler`** | `@RestControllerAdvice` that catches framework exceptions and delegates to `ErrorResponseBuilder`.                                                                                    |

### **Supporting Resources**

| File | Purpose |
|------|----------|
| `src/main/resources/core-messages.properties` | Default localized message templates (e.g., `validation.type_mismatch`, `error.internal`). |

---

## ⚙️ Features

✅ Unified JSON error format across all microservices  
✅ Handles framework and custom exceptions (`BaseApiException`)  
✅ Localization via Spring `MessageSource`  
✅ ISO-8601 timestamps (`Instant`) — auto-configured by Spring Boot 3.x  
✅ Fully stateless and reusable library  
✅ Comprehensive JUnit 5 / Mockito test coverage

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

### 1. Include the module

```groovy
dependencies {
    implementation project(":error-response-core")
}
```

In your root `settings.gradle`:

```groovy
include(":error-response-core", ":user-service", ":challenge-service")
```

---

### 2. Register the error-handling package

In your microservice’s main Spring application class:

```java
@Import(ErrorHandlingConfig.class)
```

All framework exceptions are now intercepted by `GlobalExceptionHandler` and serialized using `ErrorResponseBuilder`.

Shall you need to define a local handler (but think twice before doing it - in principle it shouldn't be necessary), create a local `@RestControllerAdvice` and inject `ErrorResponseBuilder`:

```java
@RestControllerAdvice
@RequiredArgsConstructor
public class UserExceptionHandler {
    private final ErrorResponseBuilder responseBuilder;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handle(UserNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(ex.getStatus())
                             .body(responseBuilder.buildError(ex, req));
    }
}
```

To override default behavior, annotate your local handler with:
```java
@Order(Ordered.HIGHEST_PRECEDENCE)
```

---

### 3. Define your custom exceptions

Custom service exceptions that extends `BaseApiException` will be automatically handled by the `GlobalExceptionHandler`.
To create subClasses of BaseApiException:

1. Write the subclass, using the ApiErrorInfo factory ".of" to hide the constructor and shield your exception against future enhancements.
2. Configure the messageKey and it's corresponding message in the local messages.properties.

```java
public class UserNotFoundException extends BaseApiException {
    public UserNotFoundException(String username) {
        super(ApiErrorInfo.of(HttpStatus.NOT_FOUND, "error.user.notFound", username));
    }
}
```

Add a message to your local `messages.properties`:

```properties
error.user.notFound=No user found with username "{0}".
```

---

### 4. Messages and Localization

To override or extend the default messages, add your own file:

```
src/main/resources/messages.properties
```

Spring automatically merges it, prioritizing local entries over the shared defaults.

---

## 🧪 Testing

Run all unit and integration tests:

```bash
./gradlew clean test jacocoTestReport
```

Coverage report:  
`build/reports/jacoco/test/html/index.html`

---

## 📊 SonarQube Integration

- Enforces minimum 70 % line coverage (Jacoco).
- Fully compatible with CI/CD pipelines (GitHub Actions).
- Quality gate passes automatically when thresholds are met.

---

## 🛠️ Development Notes

- `bootJar` is **disabled** (this is a shared library, not an executable app).
- Uses Spring Boot 3.0.6 and Jakarta Validation 3.0.2.
- `jakarta.servlet-api` is declared as `compileOnly` — the runtime provides it.
- Timestamps use `Instant` in ISO-8601; Boot 3.x automatically registers `jackson-datatype-jsr310`.
- `messageArgs` in `BaseApiException` is marked `transient` to satisfy SonarQube.
- Tested with JUnit 5, Mockito, AssertJ, and Spring Test.

---

## 📦 Versioning

| Version | Description | Spring Boot |
|----------|--------------|-------------|
| 1.0.0 | Initial release with unified error handling | 3.0.x |







