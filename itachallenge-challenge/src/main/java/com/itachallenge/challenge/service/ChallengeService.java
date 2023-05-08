package com.itachallenge.challenge.service;

import com.itachallenge.challenge.config.DummiesConfig;
import com.itachallenge.challenge.helper.ResourceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final DummiesConfig dummiesConfig;

    public Mono<String> getDummyFiltersInfo(){
        ResourceHelper resourceHelper = new ResourceHelper();
        try {
            String filters = resourceHelper.readResourceAsString(dummiesConfig.getFilterPath());
            return Mono.just(filters);
        }catch (IOException ex){
            return Mono.just(ex.getMessage());
        }
    }

    public Mono<String> getDummySortInfo(){
        ResourceHelper resourceHelper = new ResourceHelper();
        try {
            String sortInfo = resourceHelper.readResourceAsString(dummiesConfig.getSortPath());
            return Mono.just(sortInfo);
        }catch (IOException ex){
            return Mono.just(ex.getMessage());
        }
    }
}
