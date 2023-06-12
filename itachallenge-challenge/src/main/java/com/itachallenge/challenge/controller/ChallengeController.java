package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);
    private final ChallengeService challengeService;

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping("/findall")
    public Flux<Challenge> getall() {
        return challengeService.getAll();
    }

    @GetMapping("/resources/{idResources}")
    public Flux<Challenge> getChallengesByResources(@PathVariable String idResources) {
        return challengeService.getByResource(idResources);
    }

    @DeleteMapping("/resources/{idResource}")
    public ResponseEntity<?> removeResourcesById(@PathVariable String idResource) {
        // Using regex to check if it is a UUID, if not throws a 400 Bad Request
        if (!idResource.matches("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$")) {
            return ResponseEntity.badRequest().build();
        }
        if (challengeService.removeResourcesById(idResource)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
