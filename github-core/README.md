# ITA GITHUB-CORE MODULE

`github-core` is a shared library module designed to centralize all Github checks logic across ITA Challenge microservices.

## Features

- 👤 **Username Validation**: Checks whether the username is a valid Github username.
- ⚙️ **Configuration**: easy change of API Github URL should there be the need.
- ⚠️ **Exceptions**: for when the Username is not found in Github and for when the API is not working.
- 🧪 **Test Coverage**: Comes with unit tests to ensure reliability and ease of maintenance.
- 🚫 **No BootJar**: This module is a pure library and not a Spring Boot application.

## Usage

Add the dependency to your Spring Boot microservice in `build.gradle`:

```groovy
implementation(project(":github-core"))

testImplementation project(':github-core')
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
- SonarQube quality gate compliance with minimum test coverage is enforced.
- This library is versioned and updated in sync with other ITA Challenge services.