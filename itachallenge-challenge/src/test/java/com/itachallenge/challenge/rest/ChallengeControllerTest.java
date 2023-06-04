package com.itachallenge.challenge.rest;

import com.itachallenge.challenge.dtos.FilterDto;
import com.itachallenge.challenge.logic.ListChallengesService;
import com.itachallenge.challenge.utils.ResourceHelper;
import com.itachallenge.challenge.views.FilterView;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeController.class)
class ChallengeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ListChallengesService listChallengesService;

    @Test
    @DisplayName("GET filters options test")
    @SneakyThrows(IOException.class)
    void getChallengesFiltersTest(){
        String filterJsonPath = "json/FiltersJson.json";
        FilterDto[] expected = new ResourceHelper(filterJsonPath).mapResourceToObject(FilterDto[].class);
        when(listChallengesService.getFilters()).thenReturn(Mono.just(Set.of(expected)));

        String uri = ChallengeController.MICRO +ChallengeController.CHALLENGES_FILTERS;
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FilterDto[].class)
                .value(Assertions::assertNotNull)
                .value(result -> {
                    assertThat(result).hasSize(expected.length);
                    assertThat(result).usingRecursiveFieldByFieldElementComparator()
                            .containsExactlyInAnyOrder(expected);
                });
    }
}
