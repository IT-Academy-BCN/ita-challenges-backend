package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.challengessection.FiltersDto;
import com.itachallenge.challenge.dto.challengessection.SortInfoDto;
import com.itachallenge.challenge.helper.ResourceHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChallengeServiceTest {

    @Autowired
    private ChallengeService challengeService;

    private static ResourceHelper resourceHelper;

    @BeforeAll
    static void setUp(){
        resourceHelper = new ResourceHelper();
    }


    @Test
    @DisplayName("Get all challenge's filters available Test")
    void getChallengesFiltersTest(){
        String jsonPath = "json/Filters.json";
        FiltersDto expected = resourceHelper.mapResource(jsonPath, FiltersDto.class);

        Mono<FiltersDto> result = challengeService.getChallengesFilters();
        StepVerifier.create(result)
                .assertNext(dto -> assertThat(dto).usingRecursiveComparison().isEqualTo(expected))
                .verifyComplete();
    }

    @Test
    @DisplayName("Get all challenge's sorting options Test")
    void getChallengesSortInfoTest(){
        String jsonPath = "json/SortInfo.json";
        SortInfoDto expected = resourceHelper.mapResource(jsonPath, SortInfoDto.class);

        Mono<SortInfoDto> result = challengeService.getChallengesSortInfo();
        StepVerifier.create(result)
                .assertNext(dto -> assertThat(dto).usingRecursiveComparison().isEqualTo(expected))
                .verifyComplete();
    }
}
