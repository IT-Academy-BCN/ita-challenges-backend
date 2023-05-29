package com.itachallenge.challenge.controller;


import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.repository.ChallengeRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    ChallengeRepository challengeRepository;

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }

    @PostMapping(value="/create")
    public Challenge createChallenge() {

        Challenge challenge = new Challenge();
        challenge.setName("Loops");

        return challengeRepository.save(challenge).block();
    }

    @GetMapping(value="/name")
    public Mono<Challenge> getChallengeByName(@RequestParam(value="name") String name){

        return challengeRepository.findByName(name);
    }
}
