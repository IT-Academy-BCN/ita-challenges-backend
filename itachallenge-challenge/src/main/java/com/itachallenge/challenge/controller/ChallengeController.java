package com.itachallenge.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itachallenge.challenge.services.ChallengeServiceImpl;


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
    	
    	try {
    	Set<UUID> relatedchallenge = challengeservice.getRelatedChallenge(challenge_id);
    	
    	return ResponseEntity.ok(relatedchallenge);
    	
    	   } catch (Exception e) {
               log.error("An Exception was thrown with Internal Server Error response: {}", e.getMessage());
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
           }
    }
    
}
