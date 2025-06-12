package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.annotations.ValidGenericPattern;
import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.dto.*;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.JwtException;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.challenge.service.IJwtService;
import com.itachallenge.challenge.service.ITagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;



@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final String DEFAULT_OFFSET = "0";
    private static final String DEFAULT_LIMIT = "200";  //if no limit, all elements (avoid exception with default value 200)
    private static final String LIMIT = "^([1-9]\\d?|1\\d{2}|200)$";  // Integer in range [1, 200]
    private static final String NO_SERVICE = "No Services";
    private static final String INVALID_PARAM = "Invalid parameter";
    private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final String STRING_PATTERN = "^[A-Za-z]{1,9}$";  //max 9 characters
    private static final String MESSAGE = "message";

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private final PropertiesConfig config;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private IChallengeService challengeService;

    @Autowired
    private ITagService tagService;

    @Autowired
    private IJwtService jwtService;

    @Value("${spring.application.version}")
    private String version;

    @Value("${spring.application.name}")
    private String appName;

    public ChallengeController(PropertiesConfig config) {
        this.config = config;
    }

    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");

        Optional<String> optChallengeService = discoveryClient.getInstances("itachallenge-challenge")
                .stream()
                .findAny()
                .map(Object::toString);

        Optional<String> userService = discoveryClient.getInstances("itachallenge-user")
                .stream()
                .findAny()
                .map(Object::toString);


        log.info("~~~~~~~~~~~~~~~~~~~~~~");
        log.info("Scanning micros:");

        StringBuilder logMessage = new StringBuilder("Scanning micros:");

        if (userService.isPresent()) {
            logMessage.append(System.lineSeparator()).append("User service available");
        } else {
            logMessage.append(System.lineSeparator()).append(NO_SERVICE);
        }

        if (optChallengeService.isPresent()) {
            logMessage.append(System.lineSeparator()).append("Challenge service available");
        } else {
            logMessage.append(System.lineSeparator()).append(NO_SERVICE);
        }


        String logMessageStr = logMessage.toString();
        log.info(logMessageStr);


        log.info("~~~~~~~~~~~~~~~~~~~~~~");


        return "Hello from ITA Challenge!!!";
    }

    @GetMapping(path = "/challenges/{challengeId}")
    @Operation(
            operationId = "Get the information from a chosen challenge.",
            summary = "Get to see the Challenge level, its details and the available languages.",
            description = "Sending the ID Challenge through the URI to retrieve it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Malformed or invalid parameter(s)"),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found.")
            }
    )
    public Mono<ResponseEntity<ChallengeDto>> getOneChallenge(@PathVariable("challengeId") String id) {

        return challengeService.getChallengeById(id)
                .map(dto -> ResponseEntity.ok().body(dto));
    }

    @GetMapping("/challenges")
    @Operation(
            operationId = "Get only the challenges on a page.",
            summary = "Get to see challenges on a page and their levels, details and their available languages.",
            description = "Requesting the challenges for a page sending page number and the number of items per page through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing or unexpected parameters")

            })

    public Mono<GenericResultDto<ChallengeDto>> getAllChallenges(
            @RequestParam(defaultValue = DEFAULT_OFFSET) @ValidGenericPattern(message = INVALID_PARAM) String offset,
            @RequestParam(defaultValue = DEFAULT_LIMIT) @ValidGenericPattern(pattern = LIMIT, message = INVALID_PARAM) String limit) {
        return challengeService.getAllChallenges(Integer.parseInt(offset), Integer.parseInt(limit));
    }

    @GetMapping("/challenges/byFilter")
    @Operation(
            operationId = "Get challenges on a page by FILTER (language, difficulty, or tags).",
            summary = "Get to see challenges on a page and their levels, details and their available languages by language and difficulty, language or difficulty.",
            description = "Requesting the challenges for a page sending page number and the number of items per page through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "200", description = "The language with given Id was not found."),
                    @ApiResponse(responseCode = "400", description = "Missing or unexpected parameters"),
                    @ApiResponse(responseCode = "400", description = "Malformed UUID")
            })

    public Flux<GenericResultDto<ChallengeDto>> getChallengesByFilter(@ModelAttribute ChallengeFilterDto filter) {
        log.info("Entering in filter service with this filter:\n" + filter.toString());
        return challengeService.getChallengesByFilter(
                Optional.ofNullable(filter.getIdLanguage()),
                Optional.ofNullable(filter.getLevel()),
                Optional.ofNullable(filter.getTags()),
                filter.getOffset(),
                filter.getLimit()
        );
    }

    @GetMapping("/solution/challenge/{idChallenge}/language/{idLanguage}")
    @Operation(
            operationId = "Get the solutions from a chosen challenge and language.",
            summary = "Get to see the Solution id, text and language.",
            description = "Sending the ID Challenge and ID Language through the URI to retrieve the Solution from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "200", description = "Successful operation."),
                    @ApiResponse(responseCode = "400", description = "Malformed or invalid parameter(s)"),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found.")
            }
    )
    public Mono<GenericResultDto<SolutionDto>> getSolutions(@PathVariable("idChallenge") String
                                                                    idChallenge, @PathVariable("idLanguage") String idLanguage) {
        return challengeService.getSolutions(idChallenge, idLanguage);

    }

    @PostMapping("/solution")
    @Operation(
            operationId = "Add solution to a chosen chosen challenge.",
            summary = "Update the Challenge level, add accepted solution to the challenge.",
            description = "Sending the ID Challenge, ID Lenguage and the solution through the body URI to update it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = SolutionDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "200", description = "Successful operation.", content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "400", description = "The solution cannot be null and the solution text cannot be empty.", content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "400", description = "Malformed or invalid parameter(s)"),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found.")
            }
    )
    public Mono<Map<String, Object>> addSolution(@Valid @RequestBody SolutionDto solutionDto) {
        return challengeService.addSolution(solutionDto)
                .map(solution -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("uuid_challenge", solution.getIdChallenge());
                    response.put("uuid_language", solution.getIdLanguage());
                    response.put("solution_text", solution.getSolutionText());
                    return response;
                });
    }

    @PostMapping("/challenges")
    @Operation(
            operationId = "Add challenge.",
            summary = "Post a challenge providing the necessary data.",
            description = "Sending the title, description, difficulty level, language and solution, a new challenge document will be inserted in the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeCreateDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing parameter(s)"),
            }
    )

    public Mono<ResponseEntity<ChallengeDto>> addChallenge(
            @Valid @RequestBody ChallengeCreateDto createFormDto,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        return Mono.fromCallable(() -> jwtService.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> challengeService.addChallenge(createFormDto))
                .doOnError(error -> log.error("Error adding challenge: {}", error.getMessage()))
                .map(ResponseEntity::ok);
    }
    @GetMapping("/version")
    @Operation(
            summary = "Get Application Version",
            description = "Retrieve the version of the application.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response with the application version and name.",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    public Mono<ResponseEntity<Map<String, String>>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("application_name", appName);
        response.put("version", version);
        return Mono.just(ResponseEntity.ok(response));
    }

    @DeleteMapping(path = "/challenges/{challengeId}")
    @Operation(
            operationId = "Delete a chosen challenge.",
            summary = "Deleting a challenge.",
            description = "Sending the ID Challenge through the URI to delete it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Malformed or invalid parameter(s)"),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found.")
            }
    )
    public Mono<ResponseEntity<DeleteResponseDto>> deleteOneChallenge(@PathVariable("challengeId") String id) {

        return challengeService.deleteChallengeById(id)
                .map(dto -> ResponseEntity.ok().body(dto));
    }

    @PostMapping("/challenges/{challengeId}/bookmarks")
    @Operation(
            operationId = "Add a challenge to User's bookmarks.",
            summary = "Add a challenge to bookmarks.",
            description = "The ID Challenge sent through the URI is added to the user's bookmarks. User Id is determined from the headers.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = FavoriteDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing or invalid authorization header."),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public Mono<ResponseEntity<BookmarkDto>> addChallengeToBookmarks(
            @PathVariable String challengeId,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        return Mono.fromCallable(() -> jwtService.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> challengeService.addChallengeToBookmarks(challengeId, userId))
                .doOnError(error -> log.error("Error adding challenge to bookmarks: {}", error.getMessage()))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/tags")
    @Operation(
            operationId = "Get all stored tags from the Database for FrontEnd can print them.",
            summary = "Get to see all id tags, name and description.",
            description = "Requesting all the tags through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
            }
    )
    public Mono<GenericResultDto<TagDto>> getAllTags() {
        return tagService.getAllTags();
    }

    @GetMapping("/tags/{languageId}")
    @Operation(
            operationId = "Get tags by languageId",
            summary = "Get all tags filtered by languageId.",
            description = "Retrieve all tags that match the specified languageId.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "No tags found for the specified languageId."),
                    @ApiResponse(responseCode = "400", description = "Malformed or invalid parameter(s).")
            }
    )
    public Mono<ResponseEntity<GenericResultDto<TagDto>>> getTagsByLanguageId(@PathVariable UUID languageId) {
        return tagService.getTagsByLanguageId(languageId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }


    @PutMapping("/challenge/{challengeId}/update")
    @Operation(
            operationId = "Updates an existing challenge.",
            summary = "Updates information of a challenge.",
            description = "Allows to update any information contained in a challenge, providing ChallengeId and new information.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing or invalid authorization header."),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to perform this action."),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
   public Mono<ResponseEntity<ChallengeDto>> updateChallenge(
           @PathVariable String challengeId,
           @Valid @RequestBody ChallengeCreateDto challengeFormDto,
           @RequestHeader(name = "Authorization", required = false) String authHeader) {
       return Mono.fromCallable(() -> jwtService.getUserUuIdFromAuthenticationHeader(authHeader))
               .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
               .flatMap(userId -> challengeService.updateChallenge(challengeId, challengeFormDto))
               .map(ResponseEntity::ok)
               .doOnError(error -> log.error("Error updating challenge: {}", error.getMessage()));
   }


    @DeleteMapping("/challenges/{challengeId}/bookmarks")
    @Operation(
            operationId = "Remove a challenge from the User's bookmarks.",
            summary = "Remove a challenge from bookmarks.",
            description = "The ID Challenge sent through the URI is removed from the user's bookmarks. User Id is determined from the headers.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = FavoriteDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing or invalid authorization header."),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public Mono<ResponseEntity<BookmarkDto>> removeChallengeFromBookmarks(
            @PathVariable String challengeId,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        return Mono.fromCallable(() -> jwtService.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> challengeService.removeChallengeFromBookmarks(challengeId, userId))
                .doOnError(error -> log.error("Error removing challenge with id {} from bookmarks: {}", challengeId, error.getMessage()))
                .map(ResponseEntity::ok);
    }

}