package com.itachallenge.challenge.controller;


import com.itachallenge.challenge.dto.challengessection.FiltersDto;
import com.itachallenge.challenge.dto.challengessection.SortInfoDto;
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
    public static final String FILTERS = "/filters";

    public static final String SORT = "/sortOptions";


    private final ChallengeService challengeService;

    @GetMapping(value = TEST_DEMO)
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping(value = FILTERS)
    public Mono<FiltersDto> getChallengesFilters(){
        return challengeService.getChallengesFilters();
    }

    @GetMapping(value = SORT)
    public Mono<SortInfoDto> getChallengesSortInfo(){
        return challengeService.getChallengesSortInfo();
    }
}


