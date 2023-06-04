package com.itachallenge.challenge.logic;

import com.itachallenge.challenge.dtos.AllDtosTests;
import com.itachallenge.challenge.views.FilterView;
import com.itachallenge.challenge.views.ListChallengesConfig;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ListChallengesServiceTest {

    @Autowired
    private ListChallengesService service;

    /*
    If I want to mock a method of a bean that is executed
    inside the constructor (when injected) ->
    the standard when.then does not work.
    solution:
    https://stackoverflow.com/questions/71409140/constructor-injection-with-mocked-object-which-has-to-set-mocked-value-in-constr
     */
    @TestConfiguration
    static class TestConfig {

        @MockBean
        private ListChallengesConfig config;

        @PostConstruct
        void initMocks() {
            when(config.getFilters()).thenReturn(AllDtosTests.buildExpectedFilters());
        }
    }

    @Test
    @DisplayName("Test to check if filters are properly included in mono response")
    void getFiltersTest(){
        Set<FilterView> expectedFilters = AllDtosTests.buildExpectedFilters();
        Mono<Set<FilterView>> filtersPublisher = service.getFilters();
        StepVerifier.create(filtersPublisher)
                .assertNext(filters -> {
                    assertEquals(expectedFilters.size(), filters.size());
                    assertThat(filters).usingRecursiveFieldByFieldElementComparator()
                            .containsExactlyInAnyOrder(expectedFilters.toArray(FilterView[]::new));
                })
                .verifyComplete();
    }
}
