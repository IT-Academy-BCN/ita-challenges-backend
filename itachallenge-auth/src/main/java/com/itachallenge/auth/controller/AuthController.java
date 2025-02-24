package com.itachallenge.auth.controller;


import com.itachallenge.auth.service.IAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final String KEY_IS_VALID = "isValid";
    private static final String KEY_USERNAME = "username";

    @Autowired
    public IAuthService authService;

    @Value("${spring.application.version}")
    private String version;

    @Value("${spring.application.name}")
    private String appName;

    public AuthController() {
    }

    @GetMapping(value = "/test")
    public String test() {
        return "Hello from ITA ChallengeAuth!!!";
    }


    @PostMapping("/github/authenticate")
    public Mono<ResponseEntity<Map<String, Object>>> authenticateWithGithub(@RequestBody Map<String, String> codeRequest) {
        return authService.exchangeCodeForToken(codeRequest.get("code"))
                .flatMap(authService::validateTokenWithGithub)
                .flatMap(response -> {
                    if (!(boolean) response.get(KEY_IS_VALID)) {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .header("X-Authentication-Status", "Failed")
                                .body(response));
                    }
                    String githubUsername = (String) response.get(KEY_USERNAME);
                    return authService.validateUserExists(githubUsername)
                            .flatMap(userExists -> {
                                if (!userExists) {
                                    log.warn("User {} does not exist in database", githubUsername);

                                    Map<String, Object> errorResponse = new HashMap<>();
                                    errorResponse.put(KEY_IS_VALID, false);
                                    errorResponse.put("message", "User does not exist in the database");

                                    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                            .header("X-Validation-Status", "UserNotFound")
                                            .header("X-Github-Username", githubUsername)
                                            .body(errorResponse));
                                }
                                return Mono.just(ResponseEntity.ok()
                                        .header("X-Authentication-Status", "Success")
                                        .header("X-Github-Username", githubUsername)
                                        .body(response));
                            });
                })
                .onErrorResume(ex -> {
                    log.error("GitHub authentication error: {}", ex.getMessage());

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put(KEY_IS_VALID, false);
                    errorResponse.put(KEY_USERNAME, null);

                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header("X-Authentication-Status", "Error")
                            .header("X-Error-Message", "An error occurred during authentication.")
                            .body(errorResponse));
                });
    }


    @GetMapping("/version")
    public Mono<ResponseEntity<Map<String, String>>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("application_name", appName);
        response.put("version", version);
        return Mono.just(ResponseEntity.ok(response));
    }

    @GetMapping("/call-user-test")
    public Mono<String> callUserTest() {
        return authService.callUserTest();
    }

}

