package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.annotations.ValidGenericPattern;
import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.dto.*;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.JwtException;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.challenge.service.ITagService;
import com.itachallenge.challenge.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final String DEFAULT_OFFSET = "0";
    private static final String DEFAULT_LIMIT = "200";  //if no limit, all elements (avoid exception with default value 200)
    private static final String LIMIT = "^([1-9]\\d?|1\\d{2}|200)$";  // Integer in range [1, 200]
    private static final String INVALID_PARAM = "Invalid parameter";
    private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final String STRING_PATTERN = "^[A-Za-z]{1,9}$";  //max 9 characters
    private static final String MESSAGE = "message";

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private final PropertiesConfig config;

    @Autowired
    private IChallengeService challengeService;

    public ChallengeController(PropertiesConfig config) {
        this.config = config;
    }

    @GetMapping(path = "/challenges/{challengeId}")
    @Operation(
            operationId = "Get the information from a chosen challenge.",
            summary = "Get to see the Challenge level, its details and the available languages.",
            description = "Sending the ID Challenge through the URI to retrieve it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "200", description = "The Challenge with given Id was not found."),
                    @ApiResponse(responseCode = "400", description = "Malformed or invalid parameter(s)")
            }
    )
    public Mono<ResponseEntity<ChallengeDto>> getOneChallenge(@PathVariable("challengeId") String id) {

        return challengeService.getChallengeById(id)
                .map(dto -> ResponseEntity.ok().body(dto));
    }

    @GetMapping("/challenges")
    @Operation(
            operationId = "Get only the challenges on a page.",
            summary = "Get to see challenges on a page and their levels, details and their available languages.",
            description = "Requesting the challenges for a page sending page number and the number of items per page through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing or unexpected parameters")

            })

    public Mono<GenericResultDto<ChallengeDto>> getAllChallenges(
            @RequestParam(defaultValue = DEFAULT_OFFSET) @ValidGenericPattern(message = INVALID_PARAM) String offset,
            @RequestParam(defaultValue = DEFAULT_LIMIT) @ValidGenericPattern(pattern = LIMIT, message = INVALID_PARAM) String limit) {
        return challengeService.getAllChallenges(Integer.parseInt(offset), Integer.parseInt(limit));
    }

    @GetMapping("/challenges/byFilter")
    @Operation(
            operationId = "Get challenges on a page by FILTER (language and/or difficulty and/or tags).",
            summary = "Get to see challenges on a page and their levels and/or language, and/or tags",
            description = "Requesting the challenges for a page sending page number and the number of items per page through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "200", description = "The language with given Id was not found."),
                    @ApiResponse(responseCode = "400", description = "Missing or unexpected parameters"),
                    @ApiResponse(responseCode = "400", description = "Malformed UUID")
            })

    public Mono<GenericResultDto<ChallengeDto>> getChallengesByFilter(@ModelAttribute ChallengeFilterDto filter) {
        log.info("Entering in filter service");
        return challengeService.getChallengesByFilter(
                Optional.ofNullable(filter.getIdLanguage()),
                Optional.ofNullable(filter.getLevel()),
                filter.getOffset(),
                filter.getLimit(),
                Optional.ofNullable(filter.getTags())
        );
    }

    @PostMapping("/challenges")
    @Operation(
            operationId = "Add challenge.",
            summary = "Post a challenge providing the necessary data.",
            description = "Sending the title, description, difficulty level, language and solution, a new challenge document will be inserted in the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeCreateDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing parameter(s)"),
            }
    )
    public Mono<ResponseEntity<ChallengeDto>> addChallenge(@Valid @RequestBody ChallengeCreateDto createFormDto) {
        return challengeService.addChallenge(createFormDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(path = "/challenges/{challengeId}")
    @Operation(
            operationId = "Delete a chosen challenge.",
            summary = "Deleting a challenge.",
            description = "Sending the ID Challenge through the URI to delete it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found."),
                    @ApiResponse(responseCode = "400", description = "Malformed or invalid parameter(s)")
            }
    )
    public Mono<ResponseEntity<DeleteResponseDto>> deleteOneChallenge(@PathVariable("challengeId") String id) {

        return challengeService.deleteChallengeById(id)
                .map(dto -> ResponseEntity.ok().body(dto));
    }

}
