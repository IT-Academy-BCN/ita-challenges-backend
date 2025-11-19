## CHANGELOG

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### [itachallenge-user-3.0.7-RELEASE] - 2025-11-19

### Added
- Foundation for action-based status handling (Taiga User Story [#871], PR [#1025]), following the validation structure for UUID
    - `SolutionAction` enum with values: SAVE, GIVE_UP, SUBMIT
    - `ActionValidator` class with case-insensitive validation logic
    - `@ValidAction` custom validation annotation

## [Unreleased]
### Chore/Internal
- Introduced `SolutionAction` enum (internal change)
  - Values: SAVE, GIVE_UP, SUBMIT
  - Prepared for action-based status handling (Taiga User Story [#871], PR [#1036])

### [itachallenge-user-3.0.5-RELEASE] - 2025-11-13

### Added
- Created FavoriteController. (PR [##1019])

### Changed
- Refactor: Moved the GET favorite-related logic out of UserController into FavoriteController.
- Improves separation of concerns, modularity, and testability.
- No API or schema changes at this stage.
### [itachallenge-challenge-3.0.2-RELEASE] - 2025-11-17

### Added
- Internal package skeleton for challenge submissions (Taiga [#804], PR [#1034]):
  - Core (non-HTTP): `com.itachallenge.submission.{document,repository,service,event,exception}`.
  - HTTP layer: `com.itachallenge.challenge.controller.submission` and
    `com.itachallenge.challenge.dto.submission`.
  - Mirror test packages under `src/test/java/com/itachallenge/challenge/controller/submission` (using `.gitkeep`), no test sources yet.
  - Mirror test packages under `src/test/java/com/itachallenge/challenge/dto/submission` (using `.gitkeep`), no test sources yet.
  - Mirror test packages under `src/test/java/com/itachallenge/submission` (using `.gitkeep`), no test sources yet.
- Docs: README updated to explain that `submission` is an internal domain package
  (not a new module/artifact) and the rationale for a future extraction to a dedicated library or microservice.

### Changed
- Internal refactor of the `itachallenge-challenge` package structure to isolate the
  **submission** domain.  
  No new endpoints, no database changes, and no runtime behavior changes.


### [itachallenge-user-3.0.4-RELEASE] - 2025-11-12
### Added
- Created FavoriteService interface and FavoriteServiceImpl implementation. (PR [##1021])

### Changed
- Refactor: Moved the GET favorite-related logic out of UserServiceImpl into FavoriteServiceImpl.
- Improves separation of concerns, modularity, and testability.
- No API or schema changes at this stage.

### [itachallenge-challenge-3.0.1-RELEASE] - 2025-11-11

### Changed
- Added dependency to error-response module in challenge service / created message.properties files to store i18n messages. (Taiga [#734] & [#797], PR [#997])
  - introduced external validation messages;
  - does not break APIs, but may alter error texts.

### [itachallenge-user-3.0.3-RELEASE] - 2025-11-07

### Added
- Foundation for managing user favorites in a dedicated collection.
  - Favorite domain & persistence
    - FavoriteDocument & FavoriteDocumentTest 
    - FavoriteRepository (ReactiveMongoRepository<FavoriteDocument, UUID>) ; Favorites collection starts empty.
    - FavoriteResponseDto & FavoriteResponseDtoTest
 

### Changed
- UserServiceImpl
  - addChallengeToFavorites() and deleteChallengeFromFavorites() updated for persistence in FavoriteRepository.
  - Temporary logic in UserServiceImpl will be migrated to FavoriteService in upcoming sprints.
  - UserServiceImplTest updated to reflect these temporary changes.
  - No changes to API endpoints or responses at this stage.


### [itachallenge-user-3.0.2-RELEASE] - 2025-11-05

### Added
- Internal package skeleton for user interactions(favorites & bookmarks).
  - Web (HTTP): `com.itachallenge.user.controller.userinteraction.{favorite,bookmark}` y `com.itachallenge.user.dto.userinteraction.{favorite,bookmark}`.
  - Core (non-HTTP): `com.itachallenge.userinteraction.{document,repository,service,event,exception}`.
  - Mirror of tests in `src/test/java` with the same structure (using `.gitkeep`), no sources.
- Minimal test for `App.main()` to satisfy new-code coverage.
- Docs: README updated explaining this is an internal package reorganization (not a new submodule/artifact) and the rationale for future extraction to a library or microservice.

### Changed
- Moved `App` to the root package (`com.itachallenge`) to simplify component scanning.  
  No new endpoints, no DB changes, and no runtime behavior change.

### [error-response-core-1.0.0-RELEASE] - 2025-10-22

### Added
- Error Response module (error-response-core) to generate consistent error through the whole application. (Taiga [#796], PR [#1010])
  - APIErrorResponse and FieldErrorDto for consistent error structure.
  - ErrorResponseBuilder and BaseExceptionHandler for general error handling.

### [itachallenge-user-3.0.1-RELEASE] - 2025-10-22

### Fixed
- Improved error handling when GitHub API is down or unresponsive. (Taiga [#744], PR [#991])
  - Now returns 503 Service Unavailable or 504 Gateway Timeout instead of a generic 500.
  - Introduced `GithubUnavailableException` to encapsulate timeout and unavailability causes.
  - Updated `UserGlobalExceptionHandler` to map these cases accordingly.
  - 
### [itachallenge-user-3.0.0-RELEASE] - 2025-10-09

### Changed
- Before: SolutionStatus enum only had two options `ENDED` and `IN_PROGRESS`, solutions with status `ENDED` could be modified, 
  leading to ambiguity in submission state.
- After: SolutionStatus enum options can be marked as `IN_PROGRESS`, `SUBMITTED_COMPLETE` or `SUBMITTED_INCOMPLETE`.
  Solutions marked as `SUBMITTED_COMPLETE` or `SUBMITTED_INCOMPLETE` now throw `UnmodifiableSolutionException`
  when updated. Only `IN_PROGRESS` solutions remain editable.
- This changes prevents accidental overwrites of finalized submissions.
- this changes will break the communication between back and frontend, it is required to adjust the type of answer that can be submitted
  by users ENDED is replaced with SUBMITTED_COMPLETE and SUBMITTED_INCOMPLETE has to be added to better reflect the status 
  in which challenges can be set (Taiga user story [#703])
  
### [itachallenge-user-2.1.0-RELEASE] - 2025-10-03

### Added
- Added a new Integer points field to UserDocument with a default value of 0.
- Corrected usages of the UserDocument all-args constructor in tests to account for the new field.
- Added dedicated test cases to verify the correct behavior of the points field, including default initialization.

### [itachallenge-challenge-3.0.0-RELEASE] - 2025-09-22

### Added
- Added the @NotEmpty annotation to the tags field in the ChallengeCreateDto.java DTO to make it a mandatory field. (Taiga [#654], PR [#961])

### [itachallenge-user-2.0.0-RELEASE] - 2025-09-12

### Changed
- Added github username validation to user microservice upon creation of a new user (Taiga [#650], PR [#960])
  - Before: POST /users/create accepted any githubUsername value and returned success (201/ok) even if that wasn't a GitHub username. 
  - After: POST /users/create now calls the GitHub API and rejects the request when the GitHub username does not exist. 
  - Clients that previously relied on creating users with invalid GitHub usernames will now get errors for some requests that used to succeed.

### [itachallenge-githubcore-1.0.0-RELEASE] - 2025-09-11

### Added
- Added github-core module to decouple out of the auth and user microservices the verification of the user against github  (Taiga [#649], PR [#959])

### [itachallenge-document-1.0.2-RELEASE] - 2025-09-17

### Changed
- Refactored DocumentController to use constructor injection, removing all instances of @Autowired to align with project standards. (Taiga [#657], PR [#963])

### [itachallenge-user-1.4.2-RELEASE] 2025-09-17

### Changed
- Refactored all @Autowired field injections to constructor injections to improve code quality and testability.(Taiga [#656], PR [#962])

### Fixed
- Fixed NullPointerException in MaxLengthURIFilter when URI is null. (Taiga [#656], PR [#962])

### [itachallenge-jwtcore-1.0.2-RELEASE] - 2025-07-30

### Changed
- Removed unused internal method `extractAllClaimsMap()`.
- Refactored tests to directly use `extractAllClaims(...)`, removing reflection and improving maintainability.

### [itachallenge-user-1.4.1-RELEASE] - 2025-07-28

### Fixed
- Added back "roll' to the AdminCrateUserService and to the AdminCreateUserResponseDto due to create the user with USER roll assigned. (Taiga [#634], PR [#956])

### [itachallenge-challenge-2.4.2-RELEASE] - 2025-07-23

### Refactored
- Moved all JWT logic to new jwt-core module. (Taiga [#594], PR [#955])

### [itachallenge-auth-2.0.5-RELEASE] - 2025-07-22

### Refactored
- Moved all JWT logic to new jwt-core module. (NOTE: Role Switch Service remains in auth)  (Taiga [#593], PR [#952])

### [itachallenge-jwtcore-1.0.1-RELEASE] - 2025-07-22

### Added
- Added jwt-core module to decouple out of the microservices the token logic of validation, UUID extraction from the Authorization header and Claims extraction  (Taiga [#593], PR [#952])


### [itachallenge-user-1.4.0-RELEASE] - 2025-07-22

### Added
- Added user/create endpoint to add an alumnee without role validation. (Taiga [#618], PR [#953])



### [itachallenge-challenge-2.4.1-RELEASE] - 2025-07-21

### Refactored
- Unified challenge fetching and filtering logic in `ChallengeServiceImpl` to remove structural duplication. (Taiga [#603], PR [#951])


### [itachallenge-jwtcore-1.0.0-RELEASE] - 2025-07-21

### Added
- Added jwt-core module to decouple out of the microservices the token logic of validation, UUID extraction from the Authorization header and Claims extraction  (Taiga [#592], PR [#950])


### [itachallenge-challenge-2.4.0-RELEASE] - 2025-07-08

### Added
- Added GET endpoint in `ChallengeController` to retrieve related challenges to the one on screen of same language, difficulty and tag (Taiga [#563], PR [#947])


### [itachallenge-challenge-2.3.0-RELEASE] - 2025-06-20

### Added
- Added GET endpoint in `TagController` to retrieve tags filtered by language. (Taiga [#485], PR [#909])

### [itachallenge-challenge-2.2.0-RELEASE] - 2025-06-19

### Added
- Added `idLanguage` field to `Tag` model and `findByLanguageUuid` method to the Tag repository to filter tags by language UUID. (Taiga [#481], PR [#906])

### [itachallenge-challenge-2.1.1-RELEASE] - 2025-06-18

#### Fixed
- Changed HTTP status from 201 Created to 200 OK in ChallengeSolvedController to fix client error handling (Taiga [#533], PR [#921])

### [itachallenge-user-1.3.0-RELEASE] - 2025-06-19

### Added
- Mapped and returned `status` field in `UserSolutionServiceImpl` methods: `addSolution()` and `getAllSolutionsByUser()` (Taiga [#523], PR [#923])

### [itachallenge-user-1.2.0-RELEASE] - 2025-06-19

### Added
- Added `status` field to `UserSolutionResponseDto` and `SubmitSolutionResponseDto`, allowing frontend to detect if a solution is in progress or completed. (Taiga [#522], PR [#922])

[itachallenge-user-1.1.0-RELEASE] - 2025-06-18

### Added
- isSolved and timesSolved information in the PUT /solution endpoint response (Taiga [#531], PR [#919])

### [itachallenge-challenge-2.1.0-RELEASE] - 2025-06-12

### Added
-Save and return challenge creation date (Taiga [#478], PR [#902])

### [itachallenge-challenge-2.0.4-RELEASE] - 2023-06-03

### Added
- Add validation to verify that the provided user ID exists for the getAllSolutionsByUser endpoint. (Taiga [#437], PR [#889])
- FavoriteController and extracted endpoints from ChallengeController. (Taiga [#418], PR [#886]) 
- POST endpoint in Auth microservice to allow user role change at runtime (Taiga [#404], PR [#882])  
- Hardcoded GET endpoint to return all of a user’s challenge solutions (Taiga [#435], PR [#884])  
- JWT authentication to logout endpoint in User microservice (Taiga [#399], PR [#864])  
- Error handling for malformed tag UUIDs and duplicate UUID detection in TagServiceImpl.getValidatedTags (Taiga [#355], PR [#872]) 
- Tag validation in the addChallenge endpoint (Taiga [#354], PR [#866])  
- GET endpoint for retrieving resources from a challenge (Taiga [#281], PR [#852])
- GET endpoint to retrieve all user's bookmarked challenges (Taiga [#255], PR [#849]) 
- DELETE endpoint in Challenge microservice to unbookmark a challenge (Taiga [#205], PR [#843]) 
- POST endpoint in Challenge microservice to bookmark a challenge (Taiga [#183], PR [#829])
- POST endpoint in User microservice to bookmark a challenge (Taiga [#183], PR [#827])
- DELETE endpoint for user's bookmarks in User microservice (Taiga [#205], PR [#831])
- GET endpoint to retrieve the list of challenges marked as favorites by a user (Taiga [#181], PR [#826])
- PUT endpoint `/solution` in User microservice to save a user solution in the database (Taiga [#198], PR [#828])
- Added the class ChallengeServiceImpl to comunicate with the Challenge microservice (Taiga [#413], PR [#890])

### Changed
- Replaced Hardcoded GET endpoint to return all of a user’s challenge solutions with actual solutions (Taiga [#406], PR [#887])
- POST endpoint for adding new challenges in Challenge microservice (PR #763)
- Improved filtering at `/GET Challenges`: added DTO for filters (language, level, tags), refactored `filterByLanguage()`, `filterByLevel()`, and `filterByTags()` methods, and added `GET /allTags` endpoint (PR #180 & PR #185)
- Modify method to increase times solved counter (Taiga [#415], PR [#885]) 

### Removed
- Removed `score` attribute from `UserSolution` DTOs and cleaned up all score/solution-related code in User microservice (PR #825, PR #725)
- Removed Score microservice due to updated architectural approach (PR #709)

### Refactored
- Simplified challenge entity in Challenge microservice and applied structural refactor (PR #712)

### Security
- Secured POST endpoint `addChallenge` for creating a challenge in Challenge microservice (PR #871)
- Secured PUT endpoint for updating a challenge in Challenge microservice (PR #875)

### [itachallenge-challenge-2.0.3-RELEASE] - 2023-11-12
* Issue #441b: Added Mongock to tracing database changes

### [itachallenge-challenge-1.6.0-RELEASE] - 2023-05-16
* Issue #513: Fixed limit parameter at endpoint /GET Challenges filtered
* Issue #510: Fixed pagination at endpoint /GET Challenges filtered
* Issue #474: Fixed endpoint /version at all micros
* Issue #497: Removing testing values from the response
* Issue #397: Fixed filtering by params idLanguage and level at endpoint /GET Challenges

### [itachallenge-challenge-1.5.0-RELEASE] - 2023-05-08
* Issue #397: Filter revision at endpoint /GET Challenges?idLanguage=99&level=EASY with params offset, limit and count and pagination

### [itachallenge-challenge-1.1.0-RELEASE] - 2023-12-12
* CORS enabled

### [itachallenge-challenge-1.0.0-RELEASE] - 2023-12-04
* First version

### [docker-compose-1.0.3] - 2023-12-04
* MongoDB Challenge Docker Volumes parameterized

### [docker-compose-1.0.2] - 2023-12-01
* Docker image Challenge (version)
* MongoDB Access Port (from another Docker container)
* Added routes /GET to access Challenge container

### [docker-compose-1.0.1] - 2023-11-30
* Fix routing to avoid POST requests to Mock Service

### [itachallenge-mock-1.0.0-RELEASE] - 2023-11-30
* First version

### [docker-compose-1.0] - 2023-11-30
* First version
