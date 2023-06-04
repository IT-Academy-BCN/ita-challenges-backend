package com.itachallenge.challenge.rest;


import com.itachallenge.challenge.logic.ListChallengesService;
import com.itachallenge.challenge.views.FilterView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping(value = ChallengeController.MICRO)
class ChallengeController {

    public static final String MICRO = "/itachallenge/api/v1/challenge";

    public static final String CHALLENGES_RESOURCE = "/challenges";

    public static final String CHALLENGES_FILTERS = CHALLENGES_RESOURCE+"/filters";

    private final ListChallengesService listChallengesService;

    @Autowired
    public ChallengeController(ListChallengesService listChallengesService) {
        this.listChallengesService = listChallengesService;
    }

    //TODO: documentation
    @GetMapping(CHALLENGES_FILTERS)
    public Mono<Set<FilterView>> readFilters(){
        return listChallengesService.getFilters();
    }
}
