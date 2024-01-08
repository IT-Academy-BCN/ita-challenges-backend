package com.itachallenge.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/auth")
public class AuthController {


    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping(value = "/test")
    public String test() {
        return "Hello from ITA ChallengeAuth!!!";
    }
}
