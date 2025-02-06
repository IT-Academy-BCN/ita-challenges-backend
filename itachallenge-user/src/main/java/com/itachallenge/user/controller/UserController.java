package com.itachallenge.user.controller;

import com.itachallenge.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

    private UserService userService;

    @Operation(summary = "Validate existing mentor", description = "Endpoint for validating a user as an existing mentor in the database. ")
    @GetMapping("/validateMentor")
    public Mono<ResponseEntity<String>> validateMentor(@RequestParam Mono<String> githubUsername) {
        return githubUsername
                .switchIfEmpty(Mono.just(""))
                .flatMap(username -> {
                    if (username.isBlank()) {
                        log.warn("Validation failed: Username cannot be empty.");
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be empty. "));
                    }
                    return userService.isMentor(Mono.just(username))
                            .map(existingUsername -> ResponseEntity.status(HttpStatus.OK).body(existingUsername))
                            .switchIfEmpty(Mono.defer(() -> {
                                log.warn("Unauthorized access attempt for username '{}'", username);
                                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized username for this request. "));
                            }));
                }).onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request, please try again. "));
    }


}