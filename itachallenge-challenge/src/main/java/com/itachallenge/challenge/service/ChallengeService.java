package com.itachallenge.challenge.service;

import com.itachallenge.challenge.helper.ResourceHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChallengeService {

    public Mono<String> getDummyFilters(){
        ResourceHelper resourceHelper = new ResourceHelper();
        String dummyFiltersJsonPath = "json/Filters.json";
        String filters = resourceHelper.readResourceAsString(dummyFiltersJsonPath);
        return Mono.just(filters);
    }

    public Mono<String> getDummySortInfo(){
        ResourceHelper resourceHelper = new ResourceHelper();
        String dummySortInfoJsonPath = "json/SortInfo.json";
        String sortInfo = resourceHelper.readResourceAsString(dummySortInfoJsonPath);
        return Mono.just(sortInfo);
    }
}
