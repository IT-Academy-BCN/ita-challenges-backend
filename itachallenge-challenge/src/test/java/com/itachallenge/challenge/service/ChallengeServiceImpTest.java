package com.itachallenge.challenge.service;

import com.itachallenge.challenge.controller.ChallengeController;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.RelatedDto;
import com.itachallenge.challenge.repository.ChallengeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeServiceImp.class)
class ChallengeServiceImpTest {
    //VARIABLES
    private final static UUID VALID_ID = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
    private final static String INVALID_ID = "123456789";

    @InjectMocks
    private ChallengeServiceImp challengeService;

    @MockBean
    private ChallengeDto challengeDto;

    @MockBean
    ChallengeRepository challengerepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChallengeId() {
        ChallengeDto expectedDto = new ChallengeDto();
        expectedDto.setChallengeId(VALID_ID);

        when(challengeDto.getChallengeId()).thenReturn(VALID_ID);

        Mono<?> result = challengeService.getChallengeId(VALID_ID);

        StepVerifier.create(result)
                .expectNextMatches(response -> response instanceof ResponseEntity &&
                        ((ResponseEntity<?>) response).getStatusCode() == HttpStatus.OK &&
                        ((ResponseEntity<?>) response).getBody() instanceof ChallengeDto &&
                        ((ChallengeDto) ((ResponseEntity<?>) response).getBody()).getChallengeId().equals(VALID_ID))
                .verifyComplete();
    }

    @Test
    void getChallengeId_Empty() {

        when(challengeDto.getChallengeId()).thenReturn(null);

        Mono<?> result = challengeService.getChallengeId(VALID_ID);

        StepVerifier.create(result)
                .expectNextMatches(response -> response instanceof ResponseEntity &&
                        ((ResponseEntity<?>) response).getStatusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void testIsValidUUID_Ok() {
        boolean result = challengeService.isValidUUID(VALID_ID.toString());

        assertTrue(result);
    }

    @Test
    void testIsValidUUID_NotValid() {
        boolean result = challengeService.isValidUUID(INVALID_ID);

        assertFalse(result);
    }
    @Test
    void  TestGetRelatedChallengePaginated() {

		RelatedDto rel1 = RelatedDto.builder().relatedId(UUID.fromString("40728c9c-a557-4d12-bf8f-3747d0924197"))
				.titleRelatedId("titulo 1").build();
		RelatedDto rel2 = RelatedDto.builder().relatedId(UUID.fromString("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0"))
				.titleRelatedId("titulo 2").build();
		RelatedDto rel3 = RelatedDto.builder().relatedId(UUID.fromString("5f71e51d-1e3e-44a2-bc97-158021f1a344"))
				.titleRelatedId("titulo 3").build();

		List<RelatedDto> related = new ArrayList<>();

		related.add(rel1);
		related.add(rel2);
		related.add(rel3);
		
		Mono<List<RelatedDto>> monoRelated = Mono.just(related);
		
	    String identificacion = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
		ChallengeServiceImp service = Mockito.mock(ChallengeServiceImp.class);
	    
        when(service.getRelatedChallenge(identificacion)).thenReturn(monoRelated);

		Mono<List<RelatedDto>> serviceResponse = service.getRelatedChallengePaginated(identificacion,0,10);
  

        StepVerifier.create(serviceResponse)
                .expectNextMatches(response -> response instanceof ResponseEntity &&
                        ((ResponseEntity<?>) response).getStatusCode() == HttpStatus.OK &&
                        ((ResponseEntity<?>) response).getBody().equals(related)                        )
                .verifyComplete();
    }

}

