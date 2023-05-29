package com.itachallenge.challenge.service;

import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.helper.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ChallengeServiceTest {

    @Autowired
    private ChallengeService challengeService;

    @MockBean
    private PropertiesConfig config;

    @Test
    @DisplayName("Get all challenge's filters available Test")
    @SneakyThrows(IOException.class)
    void getChallengesFiltersInfoTest(){
        String jsonPath = "json/FiltersInfoResponse.json";
        String expected =  new ResourceHelper(jsonPath).readResourceAsString();

        when(config.getFiltersData()).thenReturn(expected);

        Mono<String> result = challengeService.getFiltersInfo();
        StepVerifier.create(result)
                .assertNext(filterInfo -> assertThat(filterInfo).isEqualTo(expected))
                .verifyComplete();
    }

    @Test
    @DisplayName("Get all challenge's sorting options Test")
    @SneakyThrows(IOException.class)
    void getChallengesSortInfoTest(){
        String jsonPath = "json/SortInfoResponse.json";
        String expected =  new ResourceHelper(jsonPath).readResourceAsString();

        when(config.getSortData()).thenReturn(expected);

        Mono<String> result = challengeService.getSortInfo();
        StepVerifier.create(result)
                .assertNext(sortInfo -> assertThat(sortInfo).isEqualTo(expected))
                .verifyComplete();
    }
}
