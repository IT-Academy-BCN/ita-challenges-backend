package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.service.IChallengeService;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(ChallengeController.class)
class ChallengeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IChallengeService challengeService;

    @MockBean
    private DiscoveryClient discoveryClient;
    
    @InjectMocks
    ChallengeController challengecontroller;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
       @Test
        @DisplayName("Test EndPoint: related")
        void ChallengeRelatedTest_VALID_ID () throws Exception{
        
    	   final String VALID_ID = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
    	   
            ChallengeDto ch1 = ChallengeDto.builder()
    				.challengeId(UUID.fromString("40728c9c-a557-4d12-bf8f-3747d0924197"))
    				.level("1")
    				.build();
            ChallengeDto ch2 = ChallengeDto.builder()
    				.challengeId(UUID.fromString("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0"))
    				.level("titulo 2")
    				.build();
            ChallengeDto ch3 = ChallengeDto.builder()
    				.challengeId(UUID.fromString("5f71e51d-1e3e-44a2-bc97-158021f1a344"))
    				.level("titulo 3")
    				.build();
    	
            when(challengeService.getRelatedChallenge(any(String.class))).thenReturn(Flux.just(ch1, ch2, ch3));

            Mono<ResponseEntity<GenericResultDto<ChallengeDto>>> responseRelated = challengecontroller
            		.relatedChallenge(0, 10, VALID_ID);
        	
          StepVerifier.create(responseRelated)
          .expectNextMatches(response -> response.getStatusCode().equals(HttpStatus.OK)
                  && (response.getBody()).getResults()[0].getChallengeId()
                  .equals(UUID.fromString("40728c9c-a557-4d12-bf8f-3747d0924197")))
          
          .verifyComplete();


        }
        @Test
        @DisplayName("Test EndPoint: related_not_valid_id")
        void ChallengeRelatedTest_INVALID_ID () {
        	
            final String INVALID_ID = "123456789";   
        	
               BadUUIDException exception = Assert.assertThrows(BadUUIDException.class,() -> {
                   challengecontroller.relatedChallenge(0, 0, INVALID_ID);
               });

               StepVerifier.create(Mono.just(exception))
                               .expectNextMatches(resp -> {
                                   
                                   assertEquals("Invalid ID format. Please indicate the correct format.", exception.getMessage());
                                   return true;
                               })
                                       .verifyComplete();

           }

    }