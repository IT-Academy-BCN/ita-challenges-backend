package com.itachallenge.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/auth")
public class AuthController {

    private final WebClient webClient;

    @Autowired
    public AuthController(WebClient webClient) {
        this.webClient = webClient;
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
        String validationUrl = "https://dev.sso.itawiki.eurecatacademy.org/api/v1/api-docs";  // Reemplazar con la URL del proxy reactivo
        return webClient.post()
                .uri(validationUrl)
                .bodyValue(token)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}

