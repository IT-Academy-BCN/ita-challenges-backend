package com.itachallenge.challenge.controller;


import com.itachallenge.challenge.service.ChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    public static final String CHALLENGES = "/getAllChallenges";


    @Autowired
    ChallengeService challengeService;


    @GetMapping(value = CHALLENGES, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getAllChallenges() throws IOException {

        return challengeService.getAllChallenges();

    }



}
