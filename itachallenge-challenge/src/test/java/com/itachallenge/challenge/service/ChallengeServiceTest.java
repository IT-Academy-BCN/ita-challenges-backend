package com.itachallenge.challenge.service;

import com.itachallenge.challenge.config.DummiesConfig;
import com.itachallenge.challenge.helper.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ChallengeServiceTest {

    @Autowired
    private ChallengeService challengeService;

    @MockBean
    private DummiesConfig dummiesConfig;

    private static ResourceHelper resourceHelper;

    @BeforeAll
    static void setUp(){
        resourceHelper = new ResourceHelper();
    }

    @Test
    @DisplayName("Get all challenge's filters available Test")
    @SneakyThrows(IOException.class)
    void getChallengesFiltersInfoTest(){
        String jsonPath = "json/FiltersInfoResponse.json";
        String expected = resourceHelper.readResourceAsString(jsonPath);

        when(dummiesConfig.getFilterPath()).thenReturn(jsonPath);

        MockedConstruction.MockInitializer<ResourceHelper> configureResourceHelperMock =
                (mock, context) ->
                        when(mock.readResourceAsString(anyString())).thenReturn(expected);

        try (MockedConstruction<ResourceHelper> mockedResourceHelper =
                     mockConstruction(ResourceHelper.class,
                    configureResourceHelperMock )) {

            Mono<String> result = challengeService.getDummyFiltersInfo();
            StepVerifier.create(result)
                    .assertNext(filterInfo -> assertThat(filterInfo).isEqualTo(expected))
                    .verifyComplete();
        }
    }

    @Test
    @DisplayName("Get all challenge's sorting options Test")
    @SneakyThrows(IOException.class)
    void getChallengesSortInfoTest(){
        String jsonPath = "json/SortInfoResponse.json";
        String expected = resourceHelper.readResourceAsString(jsonPath);

        when(dummiesConfig.getSortPath()).thenReturn("");

        MockedConstruction.MockInitializer<ResourceHelper> configureResourceHelperMock =
                (mock, context) ->
                        when(mock.readResourceAsString(anyString())).thenReturn(expected);

        try (MockedConstruction<ResourceHelper> mockedResourceHelper =
                     mockConstruction(ResourceHelper.class,
                             configureResourceHelperMock )) {

            Mono<String> result = challengeService.getDummySortInfo();
            StepVerifier.create(result)
                    .assertNext(sortInfo -> assertThat(sortInfo).isEqualTo(expected))
                    .verifyComplete();
        }
    }
}
