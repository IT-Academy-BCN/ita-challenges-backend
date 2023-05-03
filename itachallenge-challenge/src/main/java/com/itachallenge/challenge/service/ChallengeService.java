package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.challengessection.FiltersDto;
import com.itachallenge.challenge.dto.challengessection.SortInfoDto;
import com.itachallenge.challenge.helper.ResourceHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChallengeService {

    public Mono<FiltersDto> getChallengesFilters(){
        FiltersDto filtersDto = initDummyFilters();
        return Mono.just(filtersDto);
    }

    private FiltersDto initDummyFilters(){
        ResourceHelper resourceHelper = new ResourceHelper();
        String dummyJsonPath = "json/Filters.json";
        return resourceHelper.mapResource(dummyJsonPath, FiltersDto.class);
    }

    public Mono<SortInfoDto> getChallengesSortInfo(){
        SortInfoDto sortInfoDto = initDummySortInfo();
        return Mono.just(sortInfoDto);
    }

    private SortInfoDto initDummySortInfo() {
        ResourceHelper resourceHelper = new ResourceHelper();
        String dummyJsonPath = "json/SortInfo.json";
        return resourceHelper.mapResource(dummyJsonPath, SortInfoDto.class);
    }
}
