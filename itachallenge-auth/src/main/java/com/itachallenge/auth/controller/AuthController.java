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
                .map(response -> {
                    HttpStatus status = (boolean) response.get(KEY_IS_VALID) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
                    return ResponseEntity.status(status).body(response);
                })
                .onErrorResume(ex -> {
                    log.error("GitHub authentication error: {}", ex.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put(KEY_IS_VALID, false);
                    errorResponse.put(KEY_USERNAME, null);

                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }


    // Old login method with SSO
//    @PostMapping("/validate")
//    public Mono<ResponseEntity<String>> validateToken(@RequestBody String token) {
//        return authService.validateWithSSO(token)
//                .map(isValid -> isValid ?
//                        new ResponseEntity<>("Token is valid", HttpStatus.OK) :
//                        new ResponseEntity<>("Token is not valid", HttpStatus.UNAUTHORIZED));
//    }

    @GetMapping("/version")
    public Mono<ResponseEntity<Map<String, String>>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("application_name", appName);
        response.put("version", version);
        return Mono.just(ResponseEntity.ok(response));
    }

}

