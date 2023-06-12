package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.exceptions.ErrorResponseMessage;
import com.itachallenge.challenge.services.IChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {
    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private IChallengeService challengeService;

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping(path = "/getOne/{id}")
    public ResponseEntity<Mono<ChallengeDto>> getOneChallenge(@PathVariable("id") String id) {

        try {
            boolean validUUID = challengeService.isValidUUID(id);

            if (!validUUID) {
                ErrorResponseMessage errorMessage = new ErrorResponseMessage(HttpStatus.NOT_FOUND.value(), "Invalid ID format.");
                log.error("{} ID: {}, incorrect.", errorMessage, id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage.getMessage());
            }

            Mono<ChallengeDto> challenge = challengeService.getChallengeId(UUID.fromString(id))
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .onErrorResume(error -> {
                        if (error instanceof IllegalArgumentException) {
                            String errorMessageIlArgEx = "ERROR: " + error.getMessage();
                            log.error("An IllegalArgumentException was thrown with Bad Request response: {}", error.getMessage());
                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessageIlArgEx));
                        } else {
                            log.error("An IllegalArgumentException was thrown with Internal Server Error response: {}", error.getMessage());
                            return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
                        }
                    });

            return ResponseEntity.ok(challenge);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("An Exception was thrown with Internal Server Error response: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
