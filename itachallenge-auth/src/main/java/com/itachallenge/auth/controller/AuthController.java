package com.itachallenge.auth.controller;

import com.itachallenge.auth.config.ClientConfig; // Se asume una importación como esta
import com.itachallenge.auth.dto.SwitchRoleRequest;
import com.itachallenge.auth.exception.CustomBadRequestException;
import com.itachallenge.auth.exception.CustomInternalServerErrorException;
import com.itachallenge.auth.service.IAuthService;
import com.itachallenge.auth.service.IJwtService;
import com.itachallenge.auth.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.lang.String;

import java.util.HashMap;
import java.util.Map;

@Service
@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    // Constantes del código original que ya no se usan en el nuevo flujo de autenticación
    // private static final String KEY_IS_VALID = "isValid";
    // private static final String KEY_USERNAME = "username";
    public static final String X_GITHUB_USERNAME = "X-Github-Username";
    public static final String X_AUTHENTICATION_STATUS = "X-Authentication-Status";
    private static final String MESSAGE_KEY = "message";

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

    // <-- MÉTODO MODIFICADO
    @Operation(summary = "GitHub OAuth2 Authentication", description = "Authenticate a user using GitHub OAuth2 code and the request's Origin header.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Missing 'code' or 'Origin' header"),
            @ApiResponse(responseCode = "401", description = "Invalid GitHub token"),
            @ApiResponse(responseCode = "500", description = "Internal server error during authentication")
    })
    @PostMapping("/github/authenticate")
    public Flux<ResponseEntity<Map<String, Object>>> authenticateWithGithub(
            @RequestHeader(value = "Origin", required = false) String origin,
            @RequestBody(required = false) Map<String, String> codeRequest) {



        if (origin == null) {
            Map<String, Object> body = Map.of(
                    "message", "Missing 'Origin' header in the request",
                    "isValid", false,
                    "username", null
            );
            return Flux.just(ResponseEntity.badRequest().body(body));
        }

        if (codeRequest == null || !codeRequest.containsKey("code")) {
            Map<String, Object> body = Map.of(
                    "message", "Missing 'code' in the request body",
                    "isValid", false,
                    "username", null
            );
            return Flux.just(ResponseEntity.badRequest().body(body));
        }

        String code = codeRequest.get("code");

        return authService.determineEnvironmentFromOrigin(origin)
                .flatMap(env -> authService.getClientConfigForEnv(env)
                    .flatMap(config -> authService.exchangeCodeForToken(code, config)
                        .flatMap(authService::validateTokenWithGithub)
                        .flatMap(response -> {
                            if (!(boolean) response.get("isValid")) {
                                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .header("X-Authentication-Status", "Failed")
                                        .body(response));
                            }
                            String githubUsername = (String) response.get("username");
                            return getUserDetailsFromGithubUsername(response, githubUsername);
                        })
                    )
                )
                .onErrorResume(ex -> {
                    log.error("GitHub authentication error: {}", ex.getMessage());
                    Map<String, Object> errorResponse = Map.of(
                            "isValid", false,
                            "username", null,
                            "message", "An error occurred during authentication."
                    );
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header("X-Authentication-Status", "Error")
                            .header("X-Error-Message", ex.getMessage())
                            .body(errorResponse));
                });
    }

    private Mono<ResponseEntity<Map<String, Object>>> getUserDetailsFromGithubUsername(
            Map<String, Object> tokenResponse, String username) {
        Map<String, Object> body = new HashMap<>();
        body.put("isValid", true);
        body.put("username", username);
        return Mono.just(ResponseEntity.ok()
                .header(X_AUTHENTICATION_STATUS, "Success")
                .header(X_GITHUB_USERNAME, username)
                .body(body));
    }


    // <-- MÉTODOS ORIGINALES (SIN CAMBIOS)
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

    @PostMapping("/switch-role")
    @Operation(
            summary = "Temporarily switch the user's role and return a new token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token generated successfully or expired.",
                            content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid role requested."),
                    @ApiResponse(responseCode = "401", description = "Missing or malformed Authorization header or invalid token."),
                    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
            }
    )
    public Mono<ResponseEntity<Map<String, String>>> switchRole(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SwitchRoleRequest request) {

        String token = jwtService.extractBearerToken(authHeader);
        String newToken = jwtService.switchRole(token, request.getNewRole());
        log.info("Switch-role successful for token");
        return Mono.just(ResponseEntity.ok(Map.of("token", newToken)));
    }
}
