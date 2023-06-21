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
    public Mono<ResponseEntity<ChallengeDto>> getOneChallenge(@PathVariable("id") String id) {
        try {
            boolean validUUID = challengeService.isValidUUID(id);

            if (!validUUID) {
                ErrorResponseMessage errorMessage = new ErrorResponseMessage(HttpStatus.BAD_REQUEST.value(), "Invalid ID format. Please indicate the correct format.");
                log.error("{} ID: {}, incorrect.", errorMessage, id);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.getMessage());
            }

            Mono<?> challengeMono = challengeService.getChallengeId(UUID.fromString(id))
                    .map(challenge -> ResponseEntity.ok().body(challenge))
                    .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

            return (Mono<ResponseEntity<ChallengeDto>>) challengeMono;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("An Exception was thrown with Internal Server Error response: {}", e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

}
