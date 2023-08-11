package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.dto.PagingParametersDto;
import com.itachallenge.challenge.service.IChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

        Optional<String> challengeService = discoveryClient.getInstances("itachallenge-challenge")
                .stream()
                .findAny()
                .map(s -> s.toString());

        Optional<String> userService = discoveryClient.getInstances("itachallenge-user")
                .stream()
                .findAny()
                .map(s -> s.toString());

        Optional<String> scoreService = discoveryClient.getInstances("itachallenge-score")
                .stream()
                .findAny()
                .map(s -> s.toString());

        log.info("~~~~~~~~~~~~~~~~~~~~~~");
        log.info("Scanning micros:");
        log.info((userService.isPresent() ? userService.get().toString() : "No Services")
                .concat(System.lineSeparator())
                .concat(challengeService.isPresent() ? challengeService.get().toString() : "No Services")
                .concat(System.lineSeparator())
                .concat(scoreService.isPresent() ? scoreService.get().toString() : "No Services"));

        log.info("~~~~~~~~~~~~~~~~~~~~~~");
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

    @GetMapping("/language")
    public Mono<GenericResultDto<LanguageDto>> getAllLanguages() {
        return challengeService.getAllLanguages();
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
    public Flux<ChallengeDto> getChallengesPaginated(@Valid @RequestBody PagingParametersDto pagingParametersDto,
                                                     @RequestParam(required = false, defaultValue = "1") String pageNumber,
                                                     @RequestParam(required = false, defaultValue = "3") String pageSize) {



/*        Optional<Integer> optionalPageNumber = this.PaginationHelper.getValidPageNumber(pageNumber);

        return challengeService.getChallengesPaginated((PaginationHelper.getValidPageNumber(pageNumber)).get(),
                (PaginationHelper.getValidPageSize(pageSize)).get());
*/

        return challengeService.getChallengesPaginated(getValidPageNumber(pageNumber), getValidPageSize(pageSize));
    }

    public int getValidPageNumber(String pageNumber) {
        if (pageNumber == null || pageNumber.isEmpty()) {
            return 1;
        }
        if (!NumberUtils.isDigits(pageNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return Integer.parseInt(pageNumber);
    }

    public int getValidPageSize(String pageSize) {
        if (pageSize == null || pageSize.isEmpty()) {
            return 3;
        }
        if (!NumberUtils.isDigits(pageSize)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return Integer.parseInt(pageSize);
    }

}
