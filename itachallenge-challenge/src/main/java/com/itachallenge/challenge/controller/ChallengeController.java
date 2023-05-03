package com.itachallenge.challenge.controller;


import com.itachallenge.challenge.service.ChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    ChallengeService challengeService;

    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping(value = "/getAllChallenges", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getAllChallenges(@RequestParam(defaultValue = "0") int offset,
                                         @RequestParam(defaultValue = "10") int limit) throws IOException {

        return challengeService.getAllChallenges(offset,limit);

    }



}
