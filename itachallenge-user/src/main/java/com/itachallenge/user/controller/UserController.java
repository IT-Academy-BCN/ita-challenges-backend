package com.itachallenge.user.controller;

import com.itachallenge.user.annotations.ValidGithubUsername;
import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.dto.UserSolutionResponseDto;
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

import java.util.Set;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public static final String X_FAVORITE_ADDED = "X-Favorite-Added";
    public static final String X_FAVORITE_DELETED = "X-Favorite-Deleted";
    public static final String X_FAVORITE_MESSAGE = "X-Favorite-Message";
    public static final String X_BOOKMARK_ADDED = "X-Bookmark-Added";
    public static final String X_BOOKMARK_DELETED = "X-Bookmark-Deleted";
    public static final String X_BOOKMARK_MESSAGE = "X-Bookmark-Message";
    public static final String X_VALIDATION_STATUS = "X-Validation-Status";
    public static final String X_ERROR_MESSAGE = "X-Error-Message";
    public static final String X_GITHUB_USERNAME ="X-Github-Username";
    public static final String FALSE = "False";

    private final UserService userService;
    private final IUserSolutionService userSolutionService;

    public UserController(UserService userService, IUserSolutionService userSolutionService) {
        this.userService = userService;
        this.userSolutionService = userSolutionService;
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
                            .header(X_GITHUB_USERNAME, githubUsername)
                            .body(user);
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.warn("User not found: {}", githubUsername);
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .header(X_VALIDATION_STATUS, "Error")
                            .header(X_ERROR_MESSAGE, "User not found")
                            .body(null);
                }))
                .onErrorResume(e -> {
                    log.error("Error retrieving user '{}': {}", githubUsername, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(X_VALIDATION_STATUS, "Error")
                            .header(X_ERROR_MESSAGE, "An error occurred retrieving user.")
                            .body(null));
                });
    }

    @Operation(
            summary = "Add Challenge to User Favorite Challenges",
            description = "Adds challenge to user favorites",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "User ID",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "challengeId",
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
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserSolutionRequestDto.class),
                            mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "500", description = "Challenge status: ended",
                            content = {@Content(schema = @Schema())})
            }
    )
    public Mono<ResponseEntity<UserSolutionResponseDto>> addSolution(
            @Valid @RequestBody UserSolutionRequestDto userSolutionRequestDto) {

        UserSolutionResponseDto userSolutionResponseDto = new UserSolutionResponseDto(
                userSolutionRequestDto.getUserId(),
                userSolutionRequestDto.getChallengeId(),
                userSolutionRequestDto.getLanguageId(),
                userSolutionRequestDto.getSolutionText()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(userSolutionResponseDto));
    }
  
    @Operation(
            summary = "Add Challenge to User Bookmark Challenges",
            description = "Adds challenge to user Bookmarks",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "User ID",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "challengeId",
                            description = "Challenge ID",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Challenge is already in bookmarks",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "201",
                            description = "Challenge added to bookmarks",
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

    @PostMapping("/users/{userId}/bookmarks/{challengeId}")
    public Mono<ResponseEntity<Boolean>> addToBookmarks(@PathVariable String userId, @PathVariable String challengeId) {
        return userService.addChallengeToBookmarks(userId, challengeId)
                .map(added -> {
                    if (Boolean.TRUE.equals(added)) {
                        log.info("Challenge '{}' added to user '{}' bookmarks", challengeId, userId);
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .header(X_BOOKMARK_ADDED, "True")
                                .header(X_BOOKMARK_MESSAGE, "Challenge added to Bookmarks.")
                                .body(true);
                    }
                    log.info("User's '{}' bookmarks already contain Challenge '{}'", userId, challengeId);
                    return ResponseEntity.ok()
                            .header(X_BOOKMARK_ADDED, FALSE)
                            .header(X_BOOKMARK_MESSAGE, "Challenge is already in Bookmarks.")
                            .body(false);
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        log.warn("No User not found with id: {}", userId);
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .header(X_BOOKMARK_ADDED, FALSE)
                                .header(X_BOOKMARK_MESSAGE, "User not found.")
                                .body(false));
                    }
                    if (throwable instanceof BadUUIDException) {
                        log.error("The provided IDs are not valid.");
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .header(X_BOOKMARK_ADDED, FALSE)
                                .header(X_BOOKMARK_MESSAGE, "The provided IDs are not valid.")
                                .body(false));
                    }
                    log.error("Unexpected error: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(X_BOOKMARK_ADDED, FALSE)
                            .header(X_BOOKMARK_MESSAGE, "Unexpected server error.")
                            .body(false));
                });
    }


    @Operation(
            summary = "Delete Challenge from User Favorite Challenges",
            description = "Deletes challenge from user favorites",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "User ID",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "challengeId",
                            description = "Challenge ID",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Challenge deleted from favorites or was not in favorites",
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
    @DeleteMapping("/users/{userId}/favorites/{challengeId}")
    public Mono<ResponseEntity<Boolean>> deleteFromFavorites(@PathVariable String userId, @PathVariable String challengeId) {
        return userService.deleteChallengeFromFavorites(userId, challengeId)
                .map(deleted -> {
                    if (Boolean.TRUE.equals(deleted)) {
                        log.info("Challenge '{}' deleted from user '{}' favorites", challengeId, userId);
                        return ResponseEntity.ok()
                                .header(X_FAVORITE_DELETED, "True")
                                .header(X_FAVORITE_MESSAGE, "Challenge deleted from favorites.")
                                .body(true);
                    }
                    log.info("No change, User's '{}' favorites doesn't contain Challenge '{}'", userId, challengeId);
                    return ResponseEntity.ok()
                            .header(X_FAVORITE_DELETED, FALSE)
                            .header(X_FAVORITE_MESSAGE, "Challenge not found in user's favorites.")
                            .body(false);
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        log.error("No User not found with id: {}", userId);
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .header(X_FAVORITE_DELETED, FALSE)
                                .header(X_FAVORITE_MESSAGE, "User not found.")
                                .body(false));
                    }
                    if (throwable instanceof BadUUIDException) {
                        log.error("The provided IDs are not valid.");
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .header(X_FAVORITE_DELETED, FALSE)
                                .header(X_FAVORITE_MESSAGE, "The provided IDs are not valid.")
                                .body(false));
                    }
                    log.error("Unexpected error: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(X_FAVORITE_DELETED, FALSE)
                            .header(X_FAVORITE_MESSAGE, "Unexpected server error.")
                            .body(false));
                });

    }

    @Operation(
            summary = "Delete Challenge from User Bookmark Challenges",
            description = "Deletes challenge from user bookmarks",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "User ID",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "challengeId",
                            description = "Challenge ID",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Challenge deleted from bookmarks or was not in bookmarks",
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
    @DeleteMapping("/users/{userId}/bookmarks/{challengeId}")
    public Mono<ResponseEntity<Boolean>> deleteFromBookmarks(@PathVariable String userId, @PathVariable String challengeId) {
        return userService.deleteChallengeFromBookmarks(userId, challengeId)
                .map(deleted -> {
                    if (Boolean.TRUE.equals(deleted)) {
                        log.info("Challenge '{}' deleted from user '{}' bookmarks", challengeId, userId);
                        return ResponseEntity.ok()
                                .header(X_BOOKMARK_DELETED, "True")
                                .header(X_BOOKMARK_MESSAGE, "Challenge deleted from bookmarks.")
                                .body(true);
                    }
                    log.info("No change, User's '{}' bookmarks doesn't contain Challenge '{}'", userId, challengeId);
                    return ResponseEntity.ok()
                            .header(X_BOOKMARK_DELETED, FALSE)
                            .header(X_BOOKMARK_MESSAGE, "Challenge not found in user's bookmarks.")
                            .body(false);
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        log.error("No User not found with id: {}", userId);
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .header(X_BOOKMARK_DELETED, FALSE)
                                .header(X_BOOKMARK_MESSAGE, "User not found.")
                                .body(false));
                    }
                    if (throwable instanceof BadUUIDException) {
                        log.error("The provided IDs are not valid.");
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .header(X_BOOKMARK_DELETED, FALSE)
                                .header(X_BOOKMARK_MESSAGE, "The provided IDs are not valid.")
                                .body(false));
                    }
                    log.error("Unexpected error: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(X_BOOKMARK_DELETED, FALSE)
                            .header(X_BOOKMARK_MESSAGE, "Unexpected server error.")
                            .body(false));
                });

    }

    @Operation(
            summary = "Gets challenges marked as favorites by a user",
            description = "Returns a set of challenge IDs that the specified user has marked as favorites",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "UUID of the user",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Set of favorite challengeIds by user"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid UUID format"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            }
    )

    @GetMapping("/users/{userId}/favorites")
    public Mono<ResponseEntity<Set<UUID>>> getUserFavorites(@PathVariable String userId) {
        return userService.getUserFavorites(userId)
                .map(favorites -> {
                    log.info("Retrieved {} favorite challenges for user {}", favorites.size(), userId);
                    return ResponseEntity.ok()
                            .header(X_VALIDATION_STATUS, "Success")
                            .body(favorites);
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        log.warn("User not found with id: {}", userId);
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .header(X_VALIDATION_STATUS, "Error")
                                .header(X_ERROR_MESSAGE, "User not found")
                                .body(null));
                    }
                    if (throwable instanceof BadUUIDException) {
                        log.error("Invalid UUID provided: {}", userId);
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .header(X_VALIDATION_STATUS, "Error")
                                .header(X_ERROR_MESSAGE, "Invalid UUID format")
                                .body(null));
                    }
                    log.error("Unexpected error getting favorites from user {}: {}", userId, throwable.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(X_VALIDATION_STATUS, "Error")
                            .header(X_ERROR_MESSAGE, "Unexpected server error")
                            .body(null));
                });
    }

}