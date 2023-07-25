package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.service.IChallengeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(ChallengeController.class)
class ChallengeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IChallengeService challengeService;

    @MockBean
    private DiscoveryClient discoveryClient;

    @Test
    void test() {
        // Arrange
        List<ServiceInstance> instances = Arrays.asList(
                new DefaultServiceInstance("instanceId", "itachallenge-challenge", "localhost", 8080, false),
                new DefaultServiceInstance("instanceId", "itachallenge-user", "localhost", 8081, false)
        );
        when(discoveryClient.getInstances("itachallenge-challenge")).thenReturn(instances);
        when(discoveryClient.getInstances("itachallenge-user")).thenReturn(Collections.singletonList(instances.get(1)));

        // Act & Assert
        webTestClient.get().uri("/itachallenge/api/v1/challenge/test")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello from ITA Challenge!!!");
    }

    @Test
    void getOneChallenge_ValidId_ChallengeReturned() {
        // Arrange
        String challengeId = "valid-challenge-id";
        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 1, 1, new ChallengeDto[]{new ChallengeDto()});

        when(challengeService.getChallengeById(challengeId)).thenReturn(Mono.just(expectedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}", challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenericResultDto.class)
                .value(dto -> {
                    assert dto != null;
                    assert dto.getCount() == 1;
                    assert dto.getResults() != null;
                    assert dto.getResults().length == 1;
                });
    }

    @Test
    void removeResourcesById_ValidId_ResourceDeleted() {
        // Arrange
        String resourceId = "valid-resource-id";
        GenericResultDto<String> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 1, 1, new String[]{"resource deleted correctly"});

        when(challengeService.removeResourcesByUuid(resourceId)).thenReturn(Mono.just(expectedResult));

        // Act & Assert
        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/resources/{idResource}", resourceId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenericResultDto.class)
                .value(dto -> {
                    assert dto != null;
                    assert dto.getCount() == 1;
                    assert dto.getResults() != null;
                    assert dto.getResults().length == 1;
                });
    }

    @Test
    void getAllChallenges_ChallengesExist_ChallengesReturned() {
        // Arrange
        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 2, 2, new ChallengeDto[]{new ChallengeDto(), new ChallengeDto()});

        when(challengeService.getAllChallenges()).thenReturn(Mono.just(expectedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenericResultDto.class)
                .value(dto -> {
                    assert dto != null;
                    assert dto.getCount() == 2;
                    assert dto.getResults() != null;
                    assert dto.getResults().length == 2;
                });
    }

    @Test
    void getAllLanguages_LanguagesExist_LanguagesReturned() {
        // Arrange
        GenericResultDto<LanguageDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 2, 2, new LanguageDto[]{new LanguageDto(), new LanguageDto()});

        when(challengeService.getAllLanguages()).thenReturn(Mono.just(expectedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/language")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenericResultDto.class)
                .value(dto -> {
                    assert dto != null;
                    assert dto.getCount() == 2;
                    assert dto.getResults() != null;
                    assert dto.getResults().length == 2;
                });
    }
}
