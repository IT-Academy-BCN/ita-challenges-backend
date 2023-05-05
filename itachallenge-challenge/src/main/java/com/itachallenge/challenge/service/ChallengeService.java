package com.itachallenge.challenge.service;

import com.itachallenge.challenge.helper.ResourceHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class ChallengeService {

    public Mono<String> getDummyFilters(){
        ResourceHelper resourceHelper = new ResourceHelper();
        String dummyFiltersJsonPath = "json/Filters.json";
        try {
            String filters = resourceHelper.readResourceAsString(dummyFiltersJsonPath);
            return Mono.just(filters);
        }catch (IOException ex){
            return Mono.just(ex.getMessage());
        }
    }

    public Mono<String> getDummySortInfo(){
        ResourceHelper resourceHelper = new ResourceHelper();
        String dummySortInfoJsonPath = "json/SortInfo.json";
        try {
            String sortInfo = resourceHelper.readResourceAsString(dummySortInfoJsonPath);
            return Mono.just(sortInfo);
        }catch (IOException ex){
            return Mono.just(ex.getMessage());
        }
    }
}
