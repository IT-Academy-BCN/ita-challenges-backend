package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.annotations.ValidGenericPattern;
import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.SolutionDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.service.IChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final String DEFAULT_OFFSET = "0";
    private static final String DEFAULT_LIMIT = "200";  //if no limit, all elements (avoid exception with default value 200)
    private static final String LIMIT = "^([1-9]\\d?|1\\d{2}|200)$";  // Integer in range [1, 200]
    private static final String NO_SERVICE = "No Services";
    private static final String INVALID_PARAM = "Invalid parameter";
    private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final String STRING_PATTERN = "^[A-Za-z]{1,9}$";  //max 9 characters

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private final PropertiesConfig config;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    IChallengeService challengeService;

    public ChallengeController(PropertiesConfig config) {
        this.config = config;
    }

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
        log.info((userService.isPresent() ? userService.get() : NO_SERVICE)
                .concat(System.lineSeparator())
                .concat(challengeService.isPresent() ? challengeService.get() : NO_SERVICE)
                .concat(System.lineSeparator())
                .concat(scoreService.isPresent() ? scoreService.get() : NO_SERVICE));

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
            operationId = "Get only the challenges on a page.",
            summary = "Get to see challenges on a page and their levels, details and their available languages.",
            description = "Requesting the challenges for a page sending page number and the number of items per page through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")})
            })
    public Flux<ChallengeDto> getAllChallenges(@RequestParam(defaultValue = DEFAULT_OFFSET) @ValidGenericPattern(message = INVALID_PARAM) String offset,
                                               @RequestParam(defaultValue = DEFAULT_LIMIT) @ValidGenericPattern(pattern = LIMIT, message = INVALID_PARAM) String limit) {
        return challengeService.getAllChallenges((Integer.parseInt(offset)), Integer.parseInt(limit));
    }

    @GetMapping("/challenges/")
    @Operation(
            operationId = "Get only the challenges on a page.",
            summary = "Get to see challenges on a page and their levels, details and their available languages.",
            description = "Requesting the challenges for a page sending page number and the number of items per page through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")})
            })
    public Mono<GenericResultDto<ChallengeDto>> getChallengesByLanguageAndDifficulty(@RequestParam @ValidGenericPattern(pattern = UUID_PATTERN, message = INVALID_PARAM) String idLanguage,
                                                                                     @RequestParam @ValidGenericPattern(pattern = STRING_PATTERN, message = INVALID_PARAM) String difficulty) {
        return challengeService.getChallengesByLanguageAndDifficulty(idLanguage, difficulty);
    }

    @GetMapping("/language")
    @Operation(
            operationId = "Get all the stored languages into the Database.",
            summary = "Get to see all id language and name.",
            description = "Requesting all the languages through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
            }
    )
    public Mono<GenericResultDto<LanguageDto>> getAllLanguages() {
        return challengeService.getAllLanguages();
    }

    @GetMapping("/solution/{idChallenge}/language/{idLanguage}")
    @Operation(
            operationId = "Get the solutions from a chosen challenge and language.",
            summary = "Get to see the Solution id, text and language.",
            description = "Sending the ID Challenge and ID Language through the URI to retrieve the Solution from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found.", content = {@Content(schema = @Schema())})
            }
    )
    public Mono<GenericResultDto<SolutionDto>> getSolutions(@PathVariable("idChallenge") String idChallenge, @PathVariable("idLanguage") String idLanguage) {
        return challengeService.getSolutions(idChallenge, idLanguage);

    }

    @PostMapping("/solution")
    @Operation(
            operationId = "Add solution to a chosen chosen challenge.",
            summary = "Update the Challenge level, add accepted solution to the challenge.",
            description = "Sending the ID Challenge, ID Lenguage and the solution through the body URI to update it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = SolutionDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found.", content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "400", description = "The solution cannot be null and the solution text cannot be empty.", content = {@Content(schema = @Schema())})
            }
    )
    public Mono<Map<String, Object>> addSolution(@RequestBody SolutionDto solutionDto) {
        if (solutionDto == null || solutionDto.getSolutionText() == null || solutionDto.getSolutionText().isEmpty()) {
            return Mono.error(new BadRequestException("La solución no puede ser nula y el texto de la solución no puede estar vacío"));
        }

        if (solutionDto.getIdChallenge() == null || solutionDto.getIdLanguage() == null) {
            return Mono.error(new BadRequestException("El id del challenge y el id del lenguaje no pueden ser nulos"));
        }

        return challengeService.addSolution(solutionDto)
                .map(solution -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("uuid_challenge", solution.getIdChallenge());
                    response.put("uuid_language", solution.getIdLanguage());
                    response.put("solution_text", solution.getSolutionText());
                    return response;
                });
    }


}
