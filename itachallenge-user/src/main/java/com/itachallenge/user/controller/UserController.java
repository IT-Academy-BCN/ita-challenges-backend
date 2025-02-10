package com.itachallenge.user.controller;

import com.itachallenge.user.annotations.ValidGithubUsername;
import com.itachallenge.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserService userService;

    @Operation(
            operationId = "Receives a username and validate if it exists in the database. ",
            summary = "Validate existing mentor",
            description = "Endpoint for validating a user as an existing mentor in the database. ")
    @GetMapping("/validate-mentor")
    public Mono<ResponseEntity<String>> validateMentor(@RequestParam @ValidGithubUsername Mono<String> githubUsername) {
        return githubUsername
                .flatMap(username -> userService.isMentor(Mono.just(username))
                        .map(existingUsername -> ResponseEntity.status(HttpStatus.OK).body(existingUsername))
                        .switchIfEmpty(Mono.defer(() -> {
                            log.warn("Unauthorized access attempt for username '{}'", username);
                            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                        }))
                ).onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request, please try again. "));
    }


}