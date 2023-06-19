package com.itachallenge.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itachallenge.challenge.documents.Challenge;


@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);
    
    @Autowired
    
    ChallengeServiceImpl challengeservice;

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping ("/{challenge_id}/related")
    public ResponseEntity<Set<UUID>> ChallengeRelated (@PathVariable(value = "challenge_id") UUID challenge_id) {
    	
    	Set<UUID> relatedchallenge = challengeservice.findChallengeByUuid(challenge_id).getRelatedChallenge();
    	
    	
    	return ResponseEntity.ok(relatedchallenge);
    }
    
   
    private Set<UUID> relatedChallenges;
}
