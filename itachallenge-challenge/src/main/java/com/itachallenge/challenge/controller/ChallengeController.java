package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.service.IChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Optional;

@RestController
@RequestMapping(value = "/" +
        "itachallenge/api/v1/challenge")
public class ChallengeController {
    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    IChallengeService challengeService;

    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");

        Optional<String> challengeService = discoveryClient.getInstances("itachallenge-challenge")
                .stream()
                .findAny()
                .map(Object::toString);

        Optional<String> userService = discoveryClient.getInstances("itachallenge-user")
                .stream()
                .findAny()
                .map(Object::toString);

        Optional<String> scoreService = discoveryClient.getInstances("itachallenge-score")
                .stream()
                .findAny()
                .map(Object::toString);

        log.info("~~~~~~~~~~~~~~~~~~~~~~");
        log.info("Scanning micros:");
        log.info((userService.map(String::toString).orElse("No Services"))
                .concat(System.lineSeparator())
                .concat(challengeService.map(String::toString).orElse("No Services"))
                .concat(System.lineSeparator())
                .concat(scoreService.map(String::toString).orElse("No Services")));

        log.info("~~~~~~~~~~~~~~~~~~~~~~");
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping(path = "/challenges/{challengeId}")
    @Operation(
            operationId = "Get the information from a chosen challenge.",
            summary = "Get to see the Challenge level, its details and the available languages.",
            description = "Sending the ID Challenge through the URI to retrieve it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json") }),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found.", content = { @Content(schema = @Schema()) })
            }
    )
    public Mono<GenericResultDto<ChallengeDto>> getOneChallenge(@PathVariable("challengeId") String id) {
        return challengeService.getChallengeById(id);
    }

    @DeleteMapping("/resources/{idResource}")
    @Operation(
            operationId = "Get the information from a chosen resource.",
            summary = "Get to see the resource and all its related parameters.",
            description = "Sending the ID Resource through the URI to retrieve it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json") }),
                    @ApiResponse(responseCode = "404", description = "The Resource with given Id was not found.", content = { @Content(schema = @Schema()) })
            }
    )
    public Mono<GenericResultDto<String>> removeResourcesById(@PathVariable String idResource) {
        return challengeService.removeResourcesByUuid(idResource);
    }

    @GetMapping("/challenges")
    @Operation(
            operationId = "Get all the stored challenges into the Database.",
            summary = "Get to see all challenges and their levels, details and their available languages.",
            description = "Requesting all the challenges through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json") }),
                    @ApiResponse(responseCode = "404", description = "No challenges were found.", content = { @Content(schema = @Schema()) })
            }
    )
    public Mono<GenericResultDto<ChallengeDto>> getAllChallenges() {
        return challengeService.getAllChallenges();
    }

    @GetMapping("/language")
    @Operation(
            operationId = "Get all the stored languages from the Database.",
            summary = "Get to see all languages available.",
            description = "Requesting all available languages through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json") }),
                    @ApiResponse(responseCode = "404", description = "No languages were found.", content = { @Content(schema = @Schema()) })
            }
    )
    public Mono<GenericResultDto<LanguageDto>> getAllLanguages() {
        return challengeService.getAllLanguages();
    }
}
