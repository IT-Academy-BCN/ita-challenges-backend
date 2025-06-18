## CHANGELOG

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### [itachallenge-challenge-2.1.1-RELEASE] - 2025-06-18

#### Fixed
- Changed HTTP status from 201 Created to 200 OK in ChallengeSolvedController to fix client error handling (Taiga [#XXX], PR [#YYY])

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
