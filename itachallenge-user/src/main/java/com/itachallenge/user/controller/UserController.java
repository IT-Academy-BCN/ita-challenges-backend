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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public static final String X_VALIDATION_STATUS = "X-Validation-Status";
    public static final String X_GITHUB_USERNAME ="X-Github-Username";

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
                            .header(X_GITHUB_USERNAME, githubUsername)
                            .body(user);
                });
    }
}
