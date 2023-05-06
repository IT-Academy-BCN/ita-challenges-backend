package com.itachallenge.challenge.service;

import com.itachallenge.challenge.helper.ResourceHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class ChallengeService {

    @Value("${dummy.filters-info.path}")
    private String dummyFiltersInfoJsonPath;
    @Value("${dummy.sort-info.path}")
    private String dummySortInfoJsonPath;

    public Mono<String> getDummyFiltersInfo(){
        ResourceHelper resourceHelper = new ResourceHelper();
        try {
            String filters = resourceHelper.readResourceAsString(dummyFiltersInfoJsonPath);
            return Mono.just(filters);
        }catch (IOException ex){
            return Mono.just(ex.getMessage());
        }
    }

    public Mono<String> getDummySortInfo(){
        ResourceHelper resourceHelper = new ResourceHelper();
        try {
            String sortInfo = resourceHelper.readResourceAsString(dummySortInfoJsonPath);
            return Mono.just(sortInfo);
        }catch (IOException ex){
            return Mono.just(ex.getMessage());
        }
    }
}
