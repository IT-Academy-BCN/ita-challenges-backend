package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.*;

import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ChallengeRepository;

import com.itachallenge.challenge.repository.LanguageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceImpCacheTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private DocumentToDtoConverter<ChallengeDocument, ChallengeDto> challengeConverter;
    @Mock
    private DocumentToDtoConverter<LanguageDocument, LanguageDto> languageConverter;
    @InjectMocks
    private ChallengeServiceImp challengeService;

    @Test
    void getChallengeById_cacheTest() {
        // Arrange
        UUID challengeId = UUID.randomUUID();
        ChallengeDocument challengeDocument = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();
        challengeDto.setChallengeId(challengeId);
        challengeDto.setLevel("EASY");

        // Simular el comportamiento del repositorio y del convertidor
        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(challengeConverter.convertDocumentToDto(any(), any())).thenReturn(challengeDto);

        // Act
        Mono<ChallengeDto> result1 = challengeService.getChallengeById(challengeId.toString());
        Mono<ChallengeDto> result2 = challengeService.getChallengeById(challengeId.toString());

        // Assert
        StepVerifier.create(result1)
                .expectNextMatches(dto -> dto.getChallengeId().equals(challengeId) &&
                        dto.getLevel().equals(challengeDto.getLevel())
                )
                .expectComplete()
                .verify();

        // Verifica que el método del repositorio solo se llame una vez
        verify(challengeRepository, times(1)).findByUuid(challengeId);

    }

    @Test
    void getAllLanguages_cacheTest() { // No esta funcionando.
        // Arrange
        UUID uuid1 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID uuid2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");
        LanguageDocument languageDocument1 = new LanguageDocument(uuid1, "Javascript");
        LanguageDocument languageDocument2 = new LanguageDocument(uuid2, "Python");
        LanguageDto languageDto1 = new LanguageDto(uuid1, "Javascript");
        LanguageDto languageDto2 = new LanguageDto(uuid2, "Python");
        LanguageDto[] expectedLanguages = {languageDto1, languageDto2};

        when(languageRepository.findAll()).thenReturn(Flux.just(languageDocument1, languageDocument2));
        when(languageConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.just(languageDto1, languageDto2));

        // Act
        Mono<GenericResultDto<LanguageDto>> result = challengeService.getAllLanguages();
        Mono<GenericResultDto<LanguageDto>> result2 = challengeService.getAllLanguages();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getCount() == 2 && Arrays.equals(dto.getResults(), expectedLanguages))
                .expectComplete()
                .verify();

        // Verificar que el método del repositorio solo se llame una vez
        verify(languageRepository, times(1)).findAll();

        // Verificar que el convertidor se llama una vez por cada resultado del repositorio
        verify(languageConverter, times(1)).convertDocumentFluxToDtoFlux(any(), any());
    }

    @Test
    void getAllChallenges_cacheTest() { // No esta funcionando.
        // Arrange
        int offset = 1;
        int limit = 2;

        // Simulate a set of ChallengeDocument with non-null UUID
        ChallengeDocument challenge1 = new ChallengeDocument();
        challenge1.setUuid(UUID.randomUUID());
        ChallengeDocument challenge2 = new ChallengeDocument();
        challenge2.setUuid(UUID.randomUUID());
        ChallengeDocument challenge3 = new ChallengeDocument();
        challenge3.setUuid(UUID.randomUUID());
        ChallengeDocument challenge4 = new ChallengeDocument();
        challenge4.setUuid(UUID.randomUUID());

        // Simulate a set of ChallengeDto
        ChallengeDto challengeDto1 = new ChallengeDto();
        ChallengeDto challengeDto2 = new ChallengeDto();
        ChallengeDto challengeDto3 = new ChallengeDto();
        ChallengeDto challengeDto4 = new ChallengeDto();

        when(challengeRepository.findAllByUuidNotNull())
                .thenReturn(Flux.just(challenge1, challenge2, challenge3, challenge4));
        when(challengeConverter.convertDocumentFluxToDtoFlux(any(), any()))
                .thenReturn(Flux.just(challengeDto1, challengeDto2, challengeDto3, challengeDto4));

        // Act
        Flux<ChallengeDto> result1 = challengeService.getAllChallenges(offset, limit);
        Flux<ChallengeDto> result2 = challengeService.getAllChallenges(offset, limit);

        // Assert
        StepVerifier.create(result1)
                .expectSubscription()
                .expectNextCount(4)
                .expectComplete()
                .verify();

        // Ensure that findAllByUuidNotNull() is called only once
        verify(challengeRepository, times(1)).findAllByUuidNotNull();
    }
}