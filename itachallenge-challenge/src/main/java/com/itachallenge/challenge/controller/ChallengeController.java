package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.helper.ParameterValidator;
import com.itachallenge.challenge.service.IChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {
    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    IChallengeService challengeService;

    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");

        Optional<URI> uri = discoveryClient.getInstances("itachallenge-challenge")
                .stream()
                .findAny()
                .map(s -> s.getUri());

        log.info("****** URI: " + (uri.isPresent() ? uri.get().toString() : "No URI"));

        Optional<String> services = discoveryClient.getInstances("itachallenge-user")
                .stream()
                .findAny()
                .map(s -> s.toString());

        log.info("****** Services: " + (services.isPresent() ? services.get().toString() : "No Services"));
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping(path = "/challenges/{challengeId}")
    @Operation(
            operationId = "Get the information from a chosen challenge.",
            summary = "Get to see the Challenge level, its details and the available languages.",
            description = "Sending the ID Challenge through the URI to retrieve it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found.", content = {@Content(schema = @Schema())})
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
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "The Resource with given Id was not found.", content = {@Content(schema = @Schema())})
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
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "No challenges were found.", content = {@Content(schema = @Schema())})
            }
    )
    public Mono<GenericResultDto<ChallengeDto>> getAllChallenges() {
        return challengeService.getAllChallenges();
    }

    @GetMapping("/challengesPaginated")
    @Operation(
            operationId = "Get only the challenges paginated.",
            summary = "Get to see challenges paginated and their levels, details and their available languages.",
            description = "Requesting the challenges paginated through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "No challenges were found.", content = {@Content(schema = @Schema())})
            })
    public Flux<ChallengeDto> getChallengesPaginated(@Valid @RequestBody ParameterValidator parameterValidator,
                                                     @RequestParam(required = false) String pageNumber,
                                                     @RequestParam(required = false) String pageSize) {
        return challengeService.getChallengesPaginated(parameterValidator.getValidPageNumber(pageNumber),
                parameterValidator.getValidPageSize(pageSize));

    }
}



