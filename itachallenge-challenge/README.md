
## ITA Challenge

* Para habilitar / deshabilitar el registro Consul, modificar el valor de la propiedad `spring.cloud.consul.enabled` en el fichero `bootstrap.yml` (true/false)

##### Swagger

- http://localhost:8762/swagger-ui/index.html
- http://localhost:8762/api-docs

##### Spring Boot Actuator

- http://localhost:8762/actuator/health (debe responder {"status":"UP"})
- http://localhost:8762/actuator/auditevents
- http://localhost:8762/actuator/beans
- http://localhost:8762/actuator/conditions
- http://localhost:8762/actuator/configprops
- http://localhost:8762/actuator/env
- http://localhost:8762/actuator/heapdump (genera volcado de heap para descarga)
- http://localhost:8762/actuator/httptrace
- http://localhost:8762/actuator/info
- http://localhost:8762/actuator/loggers
- http://localhost:8762/actuator/metrics
- http://localhost:8762/actuator/mappings
- http://localhost:8762/actuator/scheduledtasks
- http://localhost:8762/actuator/threaddump

## Submissions and Gamification Modules Overview

The `itachallenge-challenge` service has been structured to host emerging functional domains as internal modules:
`submission` and `gamification`.

### Modularization & Growth Strategy
This microservice follows a "Feature-Separation" pattern to ensure these modules can be easily extracted into
independent microservices in the future:

- **Separation of Concerns:** Business logic, repositories, and documents are isolated in standalone root packages:
  `com.itachallenge.submission` and `com.itachallenge.gamification`.
- **API Consistency:** Controllers and DTOs remain within the `itachallenge-challenge` infrastructure to provide a
unified entry point for the API.
- **Data Access:** High-performance queries are implemented via MongoDB Aggregations, with results mapped to specific
classes in the `repository.projection` subpackage.

For detailed implementation rules, see the global [9. Data Access Patterns Guidelines]
(https://github.com/IT-Academy-BCN/ita-challenges-backend/blob/main/GUIDELINES.md#9-mongodb-data-access-patterns).

### Official Solutions (Mentor Solutions)
- Represent the official solution for each challenge.
- Part of the challenge content domain (title, description, difficulty, languages, etc.).
- Remain in the existing `challenge` packages.
- Not related to user progress, scoring, or the submission workflow.

### User Submissions
- Represent the code submitted by users when solving a challenge.
- Core domain logic is now isolated under: com.itachallenge.challenge.submission
- Designed for future extraction into a dedicated microservice or shared library.
