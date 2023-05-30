package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.helpers.ResourceHelper;
import com.itachallenge.challenge.services.ChallengeService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeController.class)
public class ChallengeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ChallengeService challengeService;

    @Test
    @DisplayName("GET filters options test")
    @SneakyThrows(IOException.class)
    void getChallengesFiltersTest(){
        String filterJsonPath = "json/FiltersInfoResponse.json";
        String expected =  new ResourceHelper(filterJsonPath).readResourceAsString();
        when(challengeService.getFiltersInfo()).thenReturn(Mono.just(expected));

        String uri = ChallengeController.MICRO_CHALLENGE +ChallengeController.FILTERS;
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(Assertions::assertNotNull)
                .value(result -> assertThat(result).isEqualTo(expected));
    }

    @Test
    @DisplayName("GET sorting info test")
    @SneakyThrows(IOException.class)
    void getChallengesSortInfoTest(){
        String sortJsonPath = "json/SortInfoResponse.json";
        String expected =  new ResourceHelper(sortJsonPath).readResourceAsString();
        when(challengeService.getSortInfo()).thenReturn(Mono.just(expected));

        String uri = ChallengeController.MICRO_CHALLENGE +ChallengeController.SORT_OPTIONS;
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(Assertions::assertNotNull)
                .value(result -> assertThat(result).isEqualTo(expected));
    }
}
