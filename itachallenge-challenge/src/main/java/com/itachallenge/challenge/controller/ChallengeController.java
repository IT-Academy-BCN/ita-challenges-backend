package com.itachallenge.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import com.itachallenge.challenge.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = ChallengeController.CHALLENGE)
@RequiredArgsConstructor
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);
    public static final String CHALLENGE = "/itachallenge/api/v1/challenge";
    public static final String TEST_DEMO = "/test";

    public static final String CHALLENGES = "/getAllChallenges";
    public static final String FILTERS = "/filters";
    public static final String SORT = "/sortOptions";

    private final ChallengeService challengeService;

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping(value = FILTERS)
    public Mono<String> getChallengesFilters(){
        return challengeService.getDummyFiltersInfo();
    }

    @GetMapping(value = SORT)
    public Mono<String> getChallengesSortInfo(){
        return challengeService.getDummySortInfo();
    }
}
