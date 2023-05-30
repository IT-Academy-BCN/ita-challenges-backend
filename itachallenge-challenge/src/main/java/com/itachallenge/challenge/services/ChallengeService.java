package com.itachallenge.challenge.services;

import com.itachallenge.challenge.config.PropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final PropertiesConfig config;

    public Mono<String> getFiltersInfo(){
        return Mono.just(config.loadFiltersData());
    }

    public Mono<String> getSortInfo(){
        return Mono.just(config.loadSortData());
    }
}
