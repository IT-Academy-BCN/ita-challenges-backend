package com.itachallenge.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/auth")
public class AuthController {


    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping(value = "/test")
    public String test() {
        return "Hello from ITA ChallengeAuth!!!";
    }


    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestBody String token) {

        return validateWithSSO(token) ?
                new ResponseEntity<>("Token is valid", HttpStatus.OK) :
                new ResponseEntity<>("Token is not valid", HttpStatus.UNAUTHORIZED);
    }

    private boolean validateWithSSO(String token) {
        // Lógica de validación SSO (TODO Proxy)
        return false;
    }
}


}
