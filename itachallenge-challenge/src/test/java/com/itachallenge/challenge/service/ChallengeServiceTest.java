package com.itachallenge.challenge.service;

import com.itachallenge.challenge.helper.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

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
    @SneakyThrows(IOException.class)
    void getChallengesFiltersTest(){
        String jsonPath = "json/Filters.json";
        String expected = resourceHelper.readResourceAsString(jsonPath);

        Mono<String> result = challengeService.getDummyFilters();
        StepVerifier.create(result)
                .assertNext(dto -> assertThat(dto).isEqualTo(expected))
                .verifyComplete();
    }

    @Test
    @DisplayName("Get all challenge's sorting options Test")
    @SneakyThrows(IOException.class)
    void getChallengesSortInfoTest(){
        String jsonPath = "json/SortInfo.json";
        String expected = resourceHelper.readResourceAsString(jsonPath);

        Mono<String> result = challengeService.getDummySortInfo();
        StepVerifier.create(result)
                .assertNext(dto -> assertThat(dto).isEqualTo(expected))
                .verifyComplete();
    }
}
