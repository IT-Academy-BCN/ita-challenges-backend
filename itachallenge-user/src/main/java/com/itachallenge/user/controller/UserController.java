package com.itachallenge.user.controller;

import com.itachallenge.user.annotations.ValidGithubUsername;
import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/test")
    public String test() {
        return "Hello from ITA Challenge UserController!!!";
    }

    /**
     * @deprecated
     * Database now stores more than just Mentors. Deletion should be handled upon completion of task #84 'Implementar roles de usuario'
     */
    @Operation(
            summary = "Boolean validation for existing mentor",
            description = "Checks if a given GitHub username corresponds to an existing mentor in the database and returns a boolean response.",
            tags = {"Mentor"},
            parameters = {
                    @Parameter(
                            name = "githubUsername",
                            description = "GitHub username to validate as an existing mentor.",
                            required = true,
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Mentor validation successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(type = "boolean"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User is not a mentor",
                            content = @Content(mediaType = "application/json", schema = @Schema(type = "boolean"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(mediaType = "application/json", schema = @Schema(type = "boolean"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(type = "boolean"))
                    )
            }
    )

    @Deprecated(forRemoval = true)
    @GetMapping("/validate-mentor-exists")
    public Mono<ResponseEntity<Boolean>> validateMentorExists(@RequestParam @ValidGithubUsername String githubUsername) {
        return userService.exists(Mono.just(githubUsername))
                .map(isMentor -> {
                    if (Boolean.TRUE.equals(isMentor)) {
                        log.info("'{}' successfully validated as a mentor.", githubUsername);
                        return ResponseEntity.ok()
                                .header("X-Validation-Status", "Success")
                                .header("X-Github-Username", githubUsername)
                                .body(true);
                    } else {
                        log.warn("Unauthorized access attempt for username '{}'", githubUsername);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .header("X-Validation-Status", "Failed")
                                .header("X-Github-Username", githubUsername)
                                .body(false);
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error validating mentor at validateMentorExists: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .header("X-Validation-Status", "Error")
                            .header("X-Error-Message", "An error occurred during validation.")
                            .body(false));
                });
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
                            .header("X-Validation-Status", "Success")
                            .header("X-Github-Username", githubUsername)
                            .body(user);
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.warn("User not found: {}", githubUsername);
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .header("X-Validation-Status", "Error")
                            .header("X-Error-Message", "User not found")
                            .body(null);
                }))
                .onErrorResume(e -> {
                    log.error("Error retrieving user '{}': {}", githubUsername, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header("X-Validation-Status", "Error")
                            .header("X-Error-Message", "An error occurred retrieving user.")
                            .body(null));
                });
    }

}