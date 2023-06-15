package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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

    @GetMapping("/resources")
    public Mono<String> dadaw(){
        return Mono.just("Es un endpoint diferente");
    }

    @DeleteMapping("/resources/{idResource}")
    public ResponseEntity<?> removeResourcesById(@PathVariable String idResource) throws BadUUIDException {
        UUID uuid = getUUID(idResource);
        UUID uuidResource = UUID.fromString(idResource);
        if (challengeService.removeResourcesById(uuidResource)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private UUID getUUID(String uuidString) throws BadUUIDException {
        UUID uuid = null;
        if (uuidString.matches("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$")) {
            uuid = UUID.fromString(uuidString);
            return uuid;
        } else {
            throw new BadUUIDException();
        }
    }
}
