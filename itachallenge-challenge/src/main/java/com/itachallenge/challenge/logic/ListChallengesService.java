package com.itachallenge.challenge.logic;

import com.itachallenge.challenge.views.FilterView;
import com.itachallenge.challenge.views.ListChallengesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class ListChallengesService {

    private Set<FilterView> filters;

    @Autowired
    public ListChallengesService(ListChallengesConfig listChallengesConfig) {
        this.filters = listChallengesConfig.getFilters();
    }

    public Mono<Set<FilterView>> getFilters(){
        return Mono.just(filters);
    }
}
