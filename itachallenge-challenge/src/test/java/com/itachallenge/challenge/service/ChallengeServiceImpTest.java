package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.helper.Converter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Arrays;
import java.util.Set;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChallengeServiceImpTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private Converter converter;

    @InjectMocks
    private ChallengeServiceImp challengeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChallengeById_ValidId_ChallengeFound() {
        // Arrange
        UUID challengeId = UUID.randomUUID();
        ChallengeDocument challengeDocument = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();
        GenericResultDto<ChallengeDto> expectedDto = new GenericResultDto<>();
        expectedDto.setInfo(0, 1, 1, new ChallengeDto[] { challengeDto });

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(converter.fromChallengeToChallengeDto(any())).thenReturn(Flux.just(challengeDto));

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getChallengeById(challengeId.toString());

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(
                        dto -> dto.getCount() == 1 && Arrays.equals(dto.getResults(), expectedDto.getResults()))
                .expectComplete()
                .verify();

        verify(challengeRepository).findByUuid(challengeId);
        verify(converter).fromChallengeToChallengeDto(any());
    }

    @Test
    void getChallengeById_InvalidId_ErrorThrown() {
        // Arrange
        String invalidId = "invalid-id";

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getChallengeById(invalidId);

        // Assert
        StepVerifier.create(result)
                .expectError(BadUUIDException.class)
                .verify();

        verifyNoInteractions(challengeRepository);
        verifyNoInteractions(converter);
    }

    @Test
    void getChallengeById_NonexistentId_ErrorThrown() {
        // Arrange
        UUID challengeId = UUID.randomUUID();

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.empty());

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getChallengeById(challengeId.toString());

        // Assert
        StepVerifier.create(result)
                .expectError(ChallengeNotFoundException.class)
                .verify();

        verify(challengeRepository).findByUuid(challengeId);
        verifyNoInteractions(converter);
    }

    @Test
    void removeResourcesByUuid_ValidId_ResourceDeleted() {
        // Arrange
        UUID resourceId = UUID.randomUUID();
        ChallengeDocument challengeDocument = new ChallengeDocument();
        challengeDocument.setResources(Collections.singleton(resourceId));

        when(challengeRepository.findAllByResourcesContaining(resourceId)).thenReturn(Flux.just(challengeDocument));
        when(challengeRepository.save(any(ChallengeDocument.class))).thenReturn(Mono.just(challengeDocument));

        // Act
        Mono<GenericResultDto<String>> result = challengeService.removeResourcesByUuid(resourceId.toString());

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(
                        dto -> dto.getCount() == 1 && dto.getResults()[0].equals("resource deleted correctly"))
                .expectComplete()
                .verify();

        verify(challengeRepository).findAllByResourcesContaining(resourceId);
        verify(challengeRepository).save(any(ChallengeDocument.class));
    }

    @Test
    void removeResourcesByUuid_NonexistentId_ErrorThrown() {
        // Arrange
        UUID resourceId = UUID.randomUUID();

        when(challengeRepository.findAllByResourcesContaining(resourceId)).thenReturn(Flux.empty());

        // Act
        Mono<GenericResultDto<String>> result = challengeService.removeResourcesByUuid(resourceId.toString());

        // Assert
        StepVerifier.create(result)
                .expectError(ChallengeNotFoundException.class)
                .verify();

        verify(challengeRepository).findAllByResourcesContaining(resourceId);
        verifyNoMoreInteractions(challengeRepository);
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
			.expectNextMatches(response -> response.getTitle().equals("Primer Titulo")
								&& response.getChallengeId().equals(thisId)
						         && response instanceof ChallengeDto
						         && response.getLevel().equals("dos"));

		}
	
    @Test
    void getAllChallenges_ChallengesExist_ChallengesReturned() {
        // Arrange
        ChallengeDto challengeDto1 = new ChallengeDto();
        ChallengeDto challengeDto2 = new ChallengeDto();
        ChallengeDto[] expectedChallenges = { challengeDto1, challengeDto2 };

        when(challengeRepository.findAll()).thenReturn(Flux.just(new ChallengeDocument(), new ChallengeDocument()));
        when(converter.fromChallengeToChallengeDto(any())).thenReturn(Flux.just(challengeDto1, challengeDto2));

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getAllChallenges();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getCount() == 2 && Arrays.equals(dto.getResults(), expectedChallenges))
                .expectComplete()
                .verify();

        verify(challengeRepository).findAll();
        verify(converter).fromChallengeToChallengeDto(any());
    }
}
