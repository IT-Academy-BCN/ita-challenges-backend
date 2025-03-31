package com.itachallenge.user.controller;

import com.itachallenge.user.annotations.ValidGithubUsername;
import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.dto.UserSolutionDto;
import com.itachallenge.user.dto.UserSolutionScoreDto;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public static final String X_FAVORITE_ADDED = "X-Favorite-Added";
    public static final String X_FAVORITE_MESSAGE = "X-Favorite-Message";
    public static final String X_VALIDATION_STATUS = "X-Validation-Status";
    public static final String FALSE = "False";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/test")
    public String test() {
        return "Hello from ITA Challenge UserController!!!";
    }

    @Operation(
            summary = "Retrieve User",
            description = "Retrieves user details for the given GitHub username if it exists in the database.",
            parameters = {
                    @Parameter(
                            name = "githubUsername",
                            description = "GitHub username to search in the database.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDocument.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. The provided Github username is not valid.",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found. The requested Github username does not exist in the database",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while retrieving the user.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )

    @GetMapping("/users/{githubUsername}")
    public Mono<ResponseEntity<UserDocument>> getUser(@PathVariable @ValidGithubUsername String githubUsername) {
        return userService.getUser(githubUsername)
                .map(user -> {
                    log.info("User found: {} (Role: {})", githubUsername, user.getRole());
                    return ResponseEntity.ok()
                            .header(X_VALIDATION_STATUS, "Success")
                            .header("X-Github-Username", githubUsername)
                            .body(user);
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.warn("User not found: {}", githubUsername);
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .header(X_VALIDATION_STATUS, "Error")
                            .header("X-Error-Message", "User not found")
                            .body(null);
                }))
                .onErrorResume(e -> {
                    log.error("Error retrieving user '{}': {}", githubUsername, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(X_VALIDATION_STATUS, "Error")
                            .header("X-Error-Message", "An error occurred retrieving user.")
                            .body(null));
                });
    }

    @Operation(
            summary = "Add Challenge to User Favorite Challenges",
            description = "Adds challenge to user favorites",
            parameters = {
                    @Parameter(
                            name = "User ID",
                            description = "User ID",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "Challenge ID",
                            description = "Challenge ID",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Challenge is already in favorites",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "201",
                            description = "Challenge added to favorites",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request. The provided IDs have a bad format",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found. No user is found with the provided user id.",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )

    @PostMapping("/users/{userId}/favorites/{challengeId}")
    public Mono<ResponseEntity<Boolean>> addToFavorites(@PathVariable String userId, @PathVariable String challengeId) {
        return userService.addChallengeToFavorites(userId, challengeId)
                .map(added -> {
                    if (Boolean.TRUE.equals(added)) {
                        log.info("Challenge '{}' added to user '{}' favorites", challengeId, userId);
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .header(X_FAVORITE_ADDED, "True")
                                .header(X_FAVORITE_MESSAGE, "Challenge added to favorites.")
                                .body(true);
                    }
                    log.info("User's '{}' favorites already contain Challenge '{}'", userId, challengeId);
                    return ResponseEntity.ok()
                            .header(X_FAVORITE_ADDED, FALSE)
                            .header(X_FAVORITE_MESSAGE, "Challenge is already in favorites.")
                            .body(false);
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        log.warn("No User not found with id: {}", userId);
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .header(X_FAVORITE_ADDED, FALSE)
                                .header(X_FAVORITE_MESSAGE, "User not found.")
                                .body(false));
                    }
                    if (throwable instanceof BadUUIDException) {
                        log.error("The provided IDs are not valid.");
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .header(X_FAVORITE_ADDED, FALSE)
                                .header(X_FAVORITE_MESSAGE, "The provided IDs are not valid.")
                                .body(false));
                    }
                    log.error("Unexpected error: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(X_FAVORITE_ADDED, FALSE)
                            .header(X_FAVORITE_MESSAGE, "Unexpected server error.")
                            .body(false));
                });
    }

    @PutMapping(path = "/solution")
    @Operation(
            summary = "perform a solution, adding challenge,language,user, status and the corresponding solution text.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserSolutionDto.class),
                            mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "500", description = "Challenge status: ended",
                            content = {@Content(schema = @Schema())})
            }
    )
    public Mono<ResponseEntity<UserSolutionScoreDto>> addSolution(
            @Valid @RequestBody UserSolutionDto userSolutionDto) {

        final int score = 0;
        UserSolutionScoreDto userSolutionScoreDto = new UserSolutionScoreDto(
                userSolutionDto.getUserId(),
                userSolutionDto.getChallengeId(),
                userSolutionDto.getLanguageId(),
                userSolutionDto.getSolutionText()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(userSolutionScoreDto));
    }

}