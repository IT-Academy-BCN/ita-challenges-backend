package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.RelatedDto;
import com.itachallenge.challenge.helper.Converter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
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
    private ChallengeRepository challengeRepository;
    @MockBean
    private Converter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChallengeId() {
        ChallengeDocument challenge = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        when(challengeRepository.findByUuid(VALID_ID)).thenReturn(Mono.just(challenge));
        when(converter.fromChallengeToChallengeDto(any(Flux.class))).thenReturn(Flux.just(challengeDto));

        Mono<ChallengeDto> result = challengeService.getChallengeId(VALID_ID);

        //Comprueba que devuelva un elemento únicamente (Mono)
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetChallengeId_ConvertDto() {
        ChallengeDocument challenge = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        when(challengeRepository.findByUuid(VALID_ID)).thenReturn(Mono.just(challenge));
        when(converter.fromChallengeToChallengeDto(any(Flux.class))).thenReturn(Flux.just(challengeDto));

        Mono<ChallengeDto> result = challengeService.getChallengeId(VALID_ID);

        StepVerifier.create(result)
                .expectNext(challengeDto)
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
    void removeResourcesById_Successfull() {
        //Existing ID
        UUID resourceId = UUID.fromString("69814d46-dd12-4e22-8e1e-2cdaf31dca03");

        // Init Data
        ChallengeDocument challenge1 = ChallengeDocument.builder()
                .uuid(resourceId)
                .resources(Set.of(UUID.fromString("09dd7278-8be5-471a-b706-abda9150094f"), UUID.fromString("3b6ac964-dc93-4c14-a4da-e20a977c4c4a")))
                .build();

        ChallengeDocument challenge2 = ChallengeDocument.builder()
                .uuid(UUID.fromString("330a49d1-84cb-4e89-adf3-5e439aeb3c41"))
                .resources(Set.of(UUID.fromString("3a9a92b9-4e0e-4fda-b4c6-b6d3de0e8e3c"), UUID.fromString("0a67c417-03ab-4ad2-8989-7c764bdf2230")))
                .build();


        when(challengeRepository.findAllByResourcesContaining(resourceId)).thenReturn(Flux.just(challenge1));
        when(challengeRepository.save(challenge1))
                .thenReturn(Mono.just(challenge1));
        when(challengeRepository.save(challenge2))
                .thenReturn(Mono.just(challenge2));

        // Act
        boolean result = challengeService.removeResourcesByUuid(resourceId);

        // Assert
        assertTrue(result);

        verify(challengeRepository, times(1)).save(challenge1);
        verify(challengeRepository, times(1)).findAllByResourcesContaining(resourceId);

        assertEquals(2, challenge1.getResources().size());
        assertFalse(challenge1.getResources().contains(resourceId));

        assertEquals(2, challenge2.getResources().size());
        assertFalse(challenge2.getResources().contains(resourceId));

    }

    @Test
    void removeResourcesById_NotSuccessfull(){
        //Non Existing ID
        UUID resourceId = UUID.randomUUID();

        ChallengeDocument challenge1 = ChallengeDocument.builder()
                .uuid(UUID.randomUUID())
                .resources(Set.of(UUID.fromString("09dd7278-8be5-471a-b706-abda9150094f"), UUID.fromString("3b6ac964-dc93-4c14-a4da-e20a977c4c4a")))
                .build();

        ChallengeDocument challenge2 = ChallengeDocument.builder()
                .uuid(UUID.fromString("330a49d1-84cb-4e89-adf3-5e439aeb3c41"))
                .resources(Set.of(UUID.fromString("3a9a92b9-4e0e-4fda-b4c6-b6d3de0e8e3c"), UUID.fromString("0a67c417-03ab-4ad2-8989-7c764bdf2230")))
                .build();

        when(challengeRepository.findAllByResourcesContaining(resourceId)).thenReturn(Flux.empty());
        when(challengeRepository.save(challenge1))
                .thenReturn(Mono.just(challenge1));
        when(challengeRepository.save(challenge2))
                .thenReturn(Mono.just(challenge2));

        // Act
        boolean result = challengeService.removeResourcesByUuid(resourceId);

        // Assert
        assertFalse(result);

        verify(challengeRepository, times(0)).save(challenge1);
        verify(challengeRepository, times(1)).findAllByResourcesContaining(resourceId);

        assertEquals(2, challenge1.getResources().size());
        assertFalse(challenge1.getResources().contains(resourceId));

        assertEquals(2, challenge2.getResources().size());
        assertFalse(challenge2.getResources().contains(resourceId));
    }
    @Test
	void TestGetRelatedChallenge() {
		//variables
		ChallengeDocument challenge = ChallengeDocument.builder()
				.uuid(UUID.randomUUID())
				.title("Primer Titulo")
				.level("dos")
				.relatedChallenges(Set.of
						(UUID.fromString("40728c9c-a557-4d12-bf8f-3747d0924197"),
						UUID.fromString("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0"),
						UUID.fromString("f71e51d-1e3e-44a2-bc97-158021f1a344")))
				.build();
		

		UUID thisId = challenge.getUuid();

		
		//mocks
		when(challengeRepository.findByUuid(any(UUID.class))).thenReturn(Mono.just(challenge));
				
		//Tests
		Flux<ChallengeDto> serviceResponse = challengeService.getRelatedChallenge("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0");
		
		
		StepVerifier.create(serviceResponse)
		.expectNextMatches(response -> response.getChallengeId().equals(thisId)
							&& response.getTitle().equals("Primer Titulo")
							&& response.getLevel().equals("dos")
					         && response instanceof ChallengeDto);

	}
}
