package com.itachallenge.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public AuthController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping(value = "/test")
    public String test() {
        return "Hello from ITA ChallengeAuth!!!";
    }

    @PostMapping("/validate")
    public Mono<ResponseEntity<String>> validateToken(@RequestBody String token) {
        return validateWithSSO(token)
                .map(isValid -> isValid ?
                        new ResponseEntity<>("Token is valid", HttpStatus.OK) :
                        new ResponseEntity<>("Token is not valid", HttpStatus.UNAUTHORIZED));
    }

    private Mono<Boolean> validateWithSSO(String token) {
        // Lógica de validación SSO con el uso de WebClient (Proxy Reactivo)
        String validationUrl = "https://dev.sso.itawiki.eurecatacademy.org/api/v1/api-docs";
        WebClient webClient = webClientBuilder.build();

        return webClient.post()
                .uri(validationUrl)
                .bodyValue(token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(isValid -> {
                    if (isValid) {
                        log.info("Token is valid");
                    } else {
                        log.warn("Token is not valid");
                    }
                    return Mono.justOrEmpty(isValid);
                })
                .onErrorResume(ex -> {
                    if (ex instanceof WebClientResponseException responseException) {
                        log.error("Error from SSO server [{}]: {}", validationUrl, responseException.getStatusCode());
                    } else {
                        log.error("Unexpected error [{}]: {}", validationUrl, ex.getMessage());
                    }
                    return Mono.just(false);
                });
        }
    }


