## CHANGELOG

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

* PR #886: Created new FavoriteController and refactored ChallengeController.
* PR #872: Added error handling for malformed tag UUIDs and duplicate UUID detection in TagServiceImpl.getValidatedTags
* Issue #871  Secured POST endpoint addChallenge for creating a challenge in the Challenge Microservice.
* PR #866: Added tag validation in the addChallenge endpoint.
* Issue #864: Added JWT authentication to logout endpoint in User microservice.
* Issue #852: Added GET endpoint for retrieving resources from a challenge
* Issue #849: Added GET endpoint to retrieve all User's bookmarked challenges.
* Issue #843: Added DELETE endpoint in the Challenge Microservice to unbookmark a challenge.
* Issue #829: Added bookmark POST endpoint in the Challenge Microservice to bookmark a challenge.
* Issue #827: Added bookmark POST endpoint in the User Microservice to bookmark a challenge.
* Issue #831: Added DELETE endpoint for user's bookmals in User Microservice.
* Issue #826: Added GET endpoint to retrieve the list of challenges marked as favorites by a user.                                          
* PR #828: Enable PUT endpoint("/solution") in User microservice to save a user solution in user database
* PR #825: Removing score attribute in Dtos de UserSolution                                         
* Issue #763: Modified POST endpoint for adding new challenge (in Challenge micro)  
              Created language image attribute in LanguageDocument and LanguageDto.
              Added language image's URL to the database and updated Challenge tests.
              Created POST endpoint to add a new challenge to users favorites (in Challenge micro)
              Created DELETE endpoint to remove a challenge from users favorites (in Challenge micro)
* Issue #725: Removing all Score and Solution related code in User microservice
* Issue #712: Refactoring in Challenge micro due to simplifying challenge entity
* Issue #709: Removing Score microservice due to new approach
* PR #825: Removing score attribute in Dtos de UserSolution
* Feature #180 & #185: Filter revision at endpoint /GET Challenges
    * Added DTO for filter with language, level and tags
    * Refactored the filtering method in filterByLanguage(), FilterByLevel() and FilterByTags()
    * Added ENDPOINT GET /allTags

### [itachallenge-challenge-2.0.4-RELEASE] - 2023-11-12
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
