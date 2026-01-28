# ITA GITHUB-CORE MODULE

`github-core` is a shared library module designed to centralize all Github checks logic across ITA Challenge microservices.

## Features

- 👤 **Username Validation**: Checks whether the username is a valid Github username.
- ⚙️ **Encapsulated Configuration**: GitHub-specific properties reside within `GithubProperties`. By default, the module is "Zero-Config" as it includes internal fallback values.
- 🔧 **Configurability**: Although internal defaults are provided, any property can be overridden by defining it in the consumer's `application.yml` (e.g., `github.base-api-url`).
- 🔐 **Secure Authentication**: Uses system environment variables for sensitive credentials (`GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`).
- 📈 **Resilience**: Built-in support for retries (exponential backoff) and timeouts.
- ⚠️ **Exceptions**: Custom exceptions for when the Username is not found in Github or the API is unavailable.
- 🧪 **Test Coverage**: Comprehensive unit tests using MockWebServer to ensure reliability.
- 🚫 **No BootJar**: This module is a pure library and not a Spring Boot application.
## Usage

### 1. Dependency
Add the dependency to your Spring Boot microservice in `build.gradle`:

```groovy
implementation(project(":github-core"))

testImplementation project(':github-core')
```

### 2. Required System Variables
The following environment variables must be set in the deployment environment or IDE:

- GITHUB_CLIENT_ID: Your GitHub OAuth App Client ID.

- GITHUB_CLIENT_SECRET: Your GitHub OAuth App Client Secret.

### 3. Optional Configuration
If you need to override the default URIs or timeouts, you can add the following to your consumer's application.yml:

```yaml
github:
  base-api-url: [https://api.github.com](https://api.github.com)
  user-info-uri: "/users"
  user-profile-uri: "/user"
  base-auth-url: "https://github.com"
  authorization-uri: "/login/oauth/authorize"
  token-uri: "/login/oauth/access_token"

```

## Project structure
```markdown
github-core/
├── src/main/java/com/itachallenge/githubcore/
│   ├── service/      # IGithubApiService & GithubApiService
    ├── exception/    # GithubApiException & GithubUserNotFoundException
    ├── config/       # GithubServiceConfig
├── src/test/         # Unit tests
├── build.gradle      # Gradle module configuration
└── readme.md         # This text file           
```
## Development notes

- The module disables bootJar since it’s not a standalone Spring Boot app.
- Default configuration is defined directly in GithubProperties.java to ensure the module works out-of-the-box.
- SonarQube quality gate compliance with minimum test coverage is enforced.
- This library is versioned and updated in sync with other ITA Challenge services.
