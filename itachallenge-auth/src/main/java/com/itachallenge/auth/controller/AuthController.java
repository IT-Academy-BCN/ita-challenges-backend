package com.itachallenge.auth.controller;


import com.itachallenge.auth.exception.CustomBadRequestException;
import com.itachallenge.auth.exception.CustomInternalServerErrorException;
import com.itachallenge.auth.service.IAuthService;
import com.itachallenge.auth.service.IJwtService;
import com.itachallenge.auth.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

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
    public static final String X_GITHUB_USERNAME = "X-Github-Username";
    public static final String X_AUTHENTICATION_STATUS = "X-Authentication-Status";
    private static final String MESSAGE_KEY = "message";
    private static final String LOGOUT_SUCCESS = "Logout successful";

    private final IAuthService authService;

    private final IUserService userService;

    private final IJwtService jwtService;

    private final String version;

    private final String appName;

    public AuthController(IAuthService authService,
                          IUserService userService,
                          IJwtService jwtService,
                          @Value("${spring.application.version}") String version,
                          @Value("${spring.application.name}") String appName) {
        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.version = version;
        this.appName = appName;
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
                                .header(X_AUTHENTICATION_STATUS, "Failed")
                                .body(response));
                    }
                    String githubUsername = (String) response.get(KEY_USERNAME);
                    return getUserDetailsFromGithubUsername(response, githubUsername);
                })
                .onErrorResume(ex -> {
                    log.error("GitHub authentication error: {}", ex.getMessage());

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put(KEY_IS_VALID, false);
                    errorResponse.put(KEY_USERNAME, null);

                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(X_AUTHENTICATION_STATUS, "Error")
                            .header("X-Error-Message", "An error occurred during authentication.")
                            .body(errorResponse));
                });
    }

    private Mono<ResponseEntity<Map<String, Object>>> getUserDetailsFromGithubUsername(Map<String, Object> response, String githubUsername) {

        return userService.fetchUserData(githubUsername)
                .map(user -> jwtService.generateToken(user.getUsername(), user.getRole(), user.getUuid()))
                .map(token -> {
                    response.put("token", token);
                    return ResponseEntity.ok()
                            .header(X_AUTHENTICATION_STATUS, "Success")
                            .header(X_GITHUB_USERNAME, githubUsername)
                            .body(response);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    response.put(KEY_USERNAME, null);
                    response.put(KEY_IS_VALID, false);
                    response.put(MESSAGE_KEY, "User does not exist in the database");
                    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .header("X-Validation-Status", "Forbidden")
                            .header(X_GITHUB_USERNAME, githubUsername)
                            .header("X-Error-Message", "User does not exist in the database")
                            .body(response));
                }))
                .onErrorResume(throwable -> {
                    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                    String message = "Unexpected Error Occurred";
                    log.error("Error in the authentication process: {}", throwable.getMessage());

                    if (throwable instanceof CustomBadRequestException) {
                        status = HttpStatus.BAD_REQUEST;
                        message = throwable.getMessage();
                    }else if (throwable instanceof CustomInternalServerErrorException) {
                        message = throwable.getMessage();
                    }
                    response.put(KEY_USERNAME, null);
                    response.put(KEY_IS_VALID, false);
                    response.put(MESSAGE_KEY, message);
                    return Mono.just(ResponseEntity.status(status)
                            .header("X-Validation-Status", "Forbidden")
                            .header(X_GITHUB_USERNAME, githubUsername)
                            .body(response)
                    );
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
        return userService.callUserTest();
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, String>>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Logout attempt without token or malformed header");
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(MESSAGE_KEY, "Authorization header is missing or malformed")));
        }

        String token = authHeader.replace("Bearer ", "").trim();
        jwtService.validateToken(token);
        return Mono.just(ResponseEntity.ok(Map.of(MESSAGE_KEY, "Logout successful")));
    }
}
