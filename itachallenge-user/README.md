# 🧩 ITA User Microservice

The **ITA User Microservice** manages user information, authentication data, and user-related interactions (favorites, bookmarks, etc.).  
It connects to **MongoDB** for persistent storage and **Redis** for caching and quick data access.

---

## ⚙️ Consul Registration

To enable or disable Consul service registration, modify the following property in the `bootstrap.yml` file:

```
spring.cloud.consul.enabled: true # or false
```



## 🍃 MongoDB Initialization

To initialize the local MongoDB database, follow these steps:

### 1. Secure MongoDB by enabling authentication in your mongod.conf:

```
security:
    authorization: enabled
```

### 2. Connect as the root user:

```
mongosh --port 27017 --authenticationDatabase "admin" -u "rootMongoDb" -p
```

### 3. Switch to the admin database:

```
use admin
```

### 4. Create an administrative user with permissions for the users database:

```
db.createUser({
    user: "admin_user",
    pwd: "mypassword",
    roles: [
        { role: "dbOwner", db: "users" }
    ]
});
```

### 5. Connect using the new user:

```
mongosh --port 27017 -u admin_user --authenticationDatabase "admin" -p
```

### 6. Create the users collection:

```
db.createCollection("users");
```

### 7. Import test data from users.json:

```
mongoimport --db=users --username admin_user --authenticationDatabase admin --password mypassword --collection=users --jsonArray --file=users.json
```

---

## 🧱 Redis Local Configuration

### 🪟 Windows

#### 1. Install Redis and add it to your system PATH.

#### 2. Create a configuration file redis-ITA.conf with the following content:

```
requirepass <<password>>
rename-command CONFIG ITA_CONFIG
```

`requirepass`: enforces authentication

`rename-command`: provides additional security by renaming sensitive commands

#### 3. Start Redis using your custom configuration:

```
redis-server c:/path/to/redis-ITA.conf
```

### 🐧 macOS / Linux

#### 1. Edit the default redis.conf file in the installation directory.

#### 2. Add authentication:

```
requirepass <<password>>
```

#### 3. Restart Redis:

```
sudo systemctl restart redis
```

---

## 💾 Importing Data into Redis

Use the Redis CLI to import your test data:

```
redis-cli -h localhost -p 6379 < usersRedis.txt
```

## ⚠️ Important

Ensure you are in the same directory as the usersRedis.txt file or use the full path:

- cd /path/to/my_project/
- redis-cli -h localhost -p 6379 < usersRedis.txt
- Verify that your Redis server is running and reachable at the specified host and port.

For more details, see the Redis Import Guide.

---

## 👤 Data Model

### UserDocument

The UserDocument represents a user entity within the system.
It is stored in the users collection in MongoDB and includes both core and custom user data.

| **Field**                | **Type**       | **Description**                                                   |
|--------------------------|----------------|-------------------------------------------------------------------|
| `uuid`                   | `UUID`         | Unique identifier for the user (primary key in MongoDB).          |
| `username`               | `String`       | GitHub username used for authentication.                          |
| `role`                   | `Role (enum)`  | User role (`ADMIN`, `USER`, etc.), used for access control.       |
| `favoriteChallenges`     | `Set<UUID>`    | Set of challenge IDs marked as favorites.                         |
| `bookmarkChallenges`     | `Set<UUID>`    | Set of challenge IDs bookmarked by the user.                      |
| `points`                 | `Integer`      | Number of points earned from solving challenges.                  |


### Notes

- Annotated with @Document(collection = "users") for MongoDB mapping.
- @Indexed(unique = true) ensures unique usernames.
- Lombok annotations generate getters, setters, and boilerplate methods.
- uuid is mapped to MongoDB’s _id field.

---

## 📊 Spring Boot Actuator Endpoints

The following endpoints are available for system monitoring and diagnostics:

| **Endpoint**                 | **Description**                                   |
|------------------------------|---------------------------------------------------|
| `/actuator/health`           | Health check (should return `{"status": "UP"}`)  |
| `/actuator/auditevents`      | Audit events                                     |
| `/actuator/beans`            | Loaded beans overview                            |
| `/actuator/conditions`       | Auto-configuration report                        |
| `/actuator/configprops`      | Configuration properties                         |
| `/actuator/env`              | Environment variables                            |
| `/actuator/heapdump`         | Heap dump (downloadable)                         |
| `/actuator/httptrace`        | HTTP request trace                               |
| `/actuator/info`             | Application information                          |
| `/actuator/loggers`          | Logger configuration                             |
| `/actuator/metrics`          | Performance metrics                              |
| `/actuator/mappings`         | Request mappings                                 |
| `/actuator/scheduledtasks`   | Scheduled tasks overview                         |
| `/actuator/threaddump`       | Thread dump report                               |


---

## 🔄 Interactions Module (New - Sprint 15)

As part of Sprint 15, a new module named interactions was introduced inside the User microservice.
This module handles user-specific actions such as favourites and bookmarks, separating them from the Challenge microservice.

### 📦 Package Overview

```
src/main/java/com/itachallenge/user/interactions/
├── controller/
│ ├── bookmark/
│ │ └── BookmarkController.java
│ └── favourite/
│ └── FavouriteController.java
│
├── service/
│ ├── bookmark/
│ │ ├── BookmarkService.java
│ │ └── IBookmarkService.java
│ └── favourite/
│ ├── FavouriteService.java
│ └── IFavouriteService.java
│
├── repository/
│ ├── bookmark/
│ │ └── BookmarkRepository.java
│ └── favourite/
│ └── FavouriteRepository.java
│
├── common/
│ ├── dto/
│ │ ├── ErrorResponse.java
│ │ ├── PageResponse.java
│ │ └── ToggleRequest.java
│ ├── error/
│ │ └── InteractionsGlobalHandler.java
│ └── validation/
│ └── ValidationUtils.java
│
└── events/
├── contract/
│ ├── EventMetadata.java
│ ├── InteractionEvent.java
│ ├── UserBookmarkedToggledV1.java
│ └── UserFavouriteChangedV1.java
└── publisher/
├── InteractionEventPublisher.java
├── StubEventPublisher.java
├── UserBookmarkEventPublisher.java
└── UserFavouriteEventPublisher.java
```

### 🧩 Purpose

- Decouple user actions from the Challenge domain.
- Establish a foundation for future event-driven communication (e.g., via Kafka).
- Maintain current API behavior (AS-IS) while allowing scalable future development.
- Improve maintainability and ensure clear domain ownership.

### 🧱 Current State

- Classes are placeholders (// TODO) — no business logic implemented yet.
- Includes minimal placeholder tests to maintain SonarQube coverage.
- No new endpoints or DB interactions have been introduced.
- The README was updated to reflect the new module and structure.

### 🚀 Next Steps

- Implement logic migration for /favourites and /bookmarks.
- Add event publishing to synchronize metrics with the Challenge microservice.
- Expand test coverage with integration and unit tests once logic is implemented.

### ✅ Summary

The User Microservice now provides:

- Secure connections to MongoDB and Redis.
- A clearly defined UserDocument model.
- A modular Interactions layer for handling favourites and bookmarks.
- A modern, scalable architecture aligned with future event-driven integrations.