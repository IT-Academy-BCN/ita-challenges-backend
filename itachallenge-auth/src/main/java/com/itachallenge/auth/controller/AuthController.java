package com.itachallenge.auth.controller;

import com.itachallenge.auth.service.IAuthService;
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
@RequestMapping(value = "/itachallenge/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public IAuthService authService;

    @GetMapping(value = "/test")
    public String test() {
        return "Hello from ITA ChallengeAuth!!!";
    }

    @PostMapping("/validate")
    public Mono<ResponseEntity<String>> validateToken(@RequestBody String token) {
        return authService.validateWithSSO(token)
                .map(isValid -> isValid ?
                        new ResponseEntity<>("Token is valid", HttpStatus.OK) :
                        new ResponseEntity<>("Token is not valid", HttpStatus.UNAUTHORIZED));
    }

}


