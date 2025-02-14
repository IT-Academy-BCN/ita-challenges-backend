package com.itachallenge.user.controller;

import com.itachallenge.user.annotations.ValidGithubUsername;
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

    @Operation(
            summary = "Validate existing mentor",
            description = "Checks if a given GitHub username corresponds to an existing mentor in the database.",
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
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User is not a mentor",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @GetMapping("/validate-mentor-by-username")
    public Mono<ResponseEntity<String>> validateMentor(@RequestParam @ValidGithubUsername String githubUsername) {
        return userService.isMentorUsername(Mono.just(githubUsername))
                .map(existingUsername -> ResponseEntity.status(HttpStatus.OK).body(existingUsername))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Unauthorized access attempt for username '{}'", githubUsername);
                    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid login attempt. "));
                }))
                .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request, please try again."));
    }

    @GetMapping("/validate-mentor-exists")
    public Mono<ResponseEntity<String>> validateMentorExists(@RequestParam @ValidGithubUsername String githubUsername) {
        return userService.isMentor(Mono.just(githubUsername))
                .map(userIsMentor -> {
                    if (Boolean.TRUE.equals(userIsMentor)) {
                        return ResponseEntity.status(HttpStatus.OK).body(githubUsername);
                    } else {
                        log.warn("Unauthorized access attempt for username '{}'", githubUsername);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid login attempt. ");
                    }
                }).onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request, please try again."));

    }

}