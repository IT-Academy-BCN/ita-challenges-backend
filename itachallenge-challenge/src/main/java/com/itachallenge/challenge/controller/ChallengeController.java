package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.exception.ErrorResponseMessage;
import com.itachallenge.challenge.service.IChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {
    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private IChallengeService challengeService;

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");

 Optional<URI> uri = discoveryClient.getInstances("itachallenge-challenge")
                .stream()
                .findAny()
                .map( s -> s.getUri());

        log.info("****** URI: " + (uri.isPresent() ? uri.get().toString() : "No URI"));

        Optional<String> services = discoveryClient.getInstances("itachallenge-user")
                .stream()
                .findAny()
                .map( s -> s.toString());

        log.info("****** Services: " + (services.isPresent() ? services.get().toString() : "No Services"));
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping(path = "/challenges/{challengeId}")
    public Mono<ResponseEntity<ChallengeDto>> getOneChallenge(@PathVariable("challengeId") String id) {
        try {
            boolean validUUID = challengeService.isValidUUID(id);

            if (!validUUID) {
                ErrorResponseMessage errorMessage = new ErrorResponseMessage(HttpStatus.BAD_REQUEST.value(), "Invalid ID format. Please indicate the correct format.");
                log.error("{} ID: {}, incorrect.", errorMessage, id);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.getMessage());
            }

            ErrorResponseMessage errorMessage = new ErrorResponseMessage(HttpStatus.OK.value(), "Challenge not found");
            log.info("Challenge not found. ID: {}", id);
            return challengeService.getChallengeId(UUID.fromString(id))
                    .map(challenge -> ResponseEntity.ok().body(challenge))
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.OK, errorMessage.getMessage())));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("An Exception was thrown with Internal Server Error response: {}", e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @DeleteMapping("/resources/{idResource}")
    public ResponseEntity<Void> removeResourcesById(@PathVariable String idResource) throws BadUUIDException {
        UUID uuidResource = getUUID(idResource);
        if (challengeService.removeResourcesByUuid(uuidResource)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private UUID getUUID(String uuidString) throws BadUUIDException {
        if (uuidString.matches("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$")) {
            return UUID.fromString(uuidString);
        } else {
            throw new BadUUIDException("Invalid UUID");
        }
    }

}
