package com.itachallenge.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import com.itachallenge.challenge.services.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = ChallengeController.MICRO_CHALLENGE)
@RequiredArgsConstructor
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    public static final String MICRO_CHALLENGE = "/itachallenge/api/v1/challenge";

    public static final String FILTERS = "/filters";

    public static final String SORT_OPTIONS = "/sortOptions";


    private final ChallengeService challengeService;

    @Operation(summary = "Filters available for challenges")
    @GetMapping(value = FILTERS)
    public Mono<String> getChallengesFilters(){
        return challengeService.getFiltersInfo();
    }

    @Operation(summary = "Sorting options available for challenges")
    @GetMapping(value = SORT_OPTIONS)
    public Mono<String> getChallengesSortInfo(){
        return challengeService.getSortInfo();
    }
}
