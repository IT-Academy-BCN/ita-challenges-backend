package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.*;

import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.LanguageRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

    @DisplayName("Cache - getChallengeById")
    @Test
    void getChallengeById_cacheTest() {
        // Arrange
        UUID challengeId = UUID.randomUUID();
        ChallengeDocument challengeDocument = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();
        challengeDto.setChallengeId(challengeId);
        challengeDto.setLevel("EASY");

        /*Cache cache = mock(Cache.class);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(cache.get(anyString(), eq(ChallengeDto.class))).thenReturn(null).thenReturn(challengeDto);
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            ChallengeDto value = invocation.getArgument(1);
            return null;
        }).when(cache).put(anyString(), any(ChallengeDto.class));*/



        // Simular el comportamiento del repositorio y del convertidor
        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(challengeConverter.convertDocumentToDto(any(), any())).thenReturn(challengeDto);

        // Act
        Mono<ChallengeDto> result1 = challengeService.getChallengeById(challengeId.toString());

        // Assert
        StepVerifier.create(result1)
                .expectNextMatches(dto -> dto.getChallengeId().equals(challengeId) &&
                        dto.getLevel().equals(challengeDto.getLevel())
                )
                .expectComplete()
                .verify();

        verify(challengeRepository, times(1)).findByUuid(challengeId);

        Mono<ChallengeDto> result2 = challengeService.getChallengeById(challengeId.toString());

        StepVerifier.create(result2)
                .expectNextMatches(dto -> dto.getChallengeId().equals(challengeId) &&
                        dto.getLevel().equals(challengeDto.getLevel())
                )
                .expectComplete()
                .verify();

        verifyNoMoreInteractions(challengeRepository);

    }

    @DisplayName("Cache - getAllLanguage")
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

        /*Cache cache = mock(Cache.class);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(cache.get(anyString(), eq(GenericResultDto.class))).thenReturn(null).thenReturn(new GenericResultDto<>(0, 2, 2, expectedLanguages));
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            GenericResultDto<LanguageDto> value = invocation.getArgument(1);
            return null;
        }).when(cache).put(anyString(), any(GenericResultDto.class));
        */

        when(languageRepository.findAll()).thenReturn(Flux.just(languageDocument1, languageDocument2));
        when(languageConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.just(languageDto1, languageDto2));

        // Act
        Mono<GenericResultDto<LanguageDto>> result = challengeService.getAllLanguages();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getCount() == 2 && Arrays.equals(dto.getResults(), expectedLanguages))
                .expectComplete()
                .verify();

        verify(languageRepository, times(1)).findAll();

        verify(languageConverter, times(1)).convertDocumentFluxToDtoFlux(any(), any());

        Mono<GenericResultDto<LanguageDto>> resultCached = challengeService.getAllLanguages();

        StepVerifier.create(resultCached)
                .expectNextMatches(dto -> dto.getCount() == 2 && Arrays.equals(dto.getResults(), expectedLanguages))
                .expectComplete()
                .verify();

        verifyNoMoreInteractions(languageRepository, languageConverter);
    }
    @DisplayName("Cache - getAllChallenges")
    @Test
    void getAllChallenges_cacheTest() {
        // Arrange
        int offset = 1;
        int limit = 2;

        ChallengeDocument challenge1 = new ChallengeDocument();
        challenge1.setUuid(UUID.randomUUID());
        ChallengeDocument challenge2 = new ChallengeDocument();
        challenge2.setUuid(UUID.randomUUID());
        ChallengeDocument challenge3 = new ChallengeDocument();
        challenge3.setUuid(UUID.randomUUID());
        ChallengeDocument challenge4 = new ChallengeDocument();
        challenge4.setUuid(UUID.randomUUID());

        ChallengeDto challengeDto1 = new ChallengeDto();
        ChallengeDto challengeDto2 = new ChallengeDto();
        ChallengeDto challengeDto3 = new ChallengeDto();
        ChallengeDto challengeDto4 = new ChallengeDto();

        /*Cache cache = mock(Cache.class);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(cache.get(anyString(), eq(GenericResultDto.class))).thenReturn(null).thenReturn(new GenericResultDto<>(offset, limit, 4, new ChallengeDto[]{challengeDto1, challengeDto2, challengeDto3, challengeDto4}));
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            GenericResultDto<ChallengeDto> value = invocation.getArgument(1);
            return null;
        }).when(cache).put(anyString(), any(GenericResultDto.class));*/

        when(challengeRepository.findAllByUuidNotNullExcludingTestingValues())
                .thenReturn(Flux.just(challenge1, challenge2, challenge3, challenge4));
        when(challengeConverter.convertDocumentFluxToDtoFlux(any(), any()))
                .thenReturn(Flux.just(challengeDto1, challengeDto2, challengeDto3, challengeDto4));

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getAllChallenges(offset, limit);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getCount() == 4 && Arrays.equals(dto.getResults(), new ChallengeDto[]{challengeDto1, challengeDto2, challengeDto3, challengeDto4}))
                .expectComplete()
                .verify();

        verify(challengeRepository, times(1)).findAllByUuidNotNullExcludingTestingValues();
        verify(challengeConverter, times(1)).convertDocumentFluxToDtoFlux(any(), any());

        Mono<GenericResultDto<ChallengeDto>> resultCached = challengeService.getAllChallenges(offset, limit);

        StepVerifier.create(resultCached)
                .expectNextMatches(dto -> dto.getCount() == 4 && Arrays.equals(dto.getResults(), new ChallengeDto[]{challengeDto1, challengeDto2, challengeDto3, challengeDto4}))
                .expectComplete()
                .verify();

        verifyNoMoreInteractions(challengeRepository, challengeConverter);
    }
    @DisplayName("Cache - getRelatedChallenges")
    @Test
    void testGetRelatedChallenges_cacheTest() {
        int offset = 1;
        int limit = 1;
        String challengeStringId = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
        UUID relatedId = UUID.fromString("f6e0f877-9560-4e68-bab6-7dd5f16b46a5");
        UUID relatedId2 = UUID.fromString("9d2c4e2b-02af-4327-81b2-7dbf5c3f5a7d");
        UUID relatedId3 = UUID.fromString("2f948de0-6f0c-4089-90b9-7f70a0812319");
        Set<UUID> relatedChallenges = new HashSet<>(Arrays.asList(relatedId, relatedId2, relatedId3));

        ChallengeDocument challenge = new ChallengeDocument();
        challenge.setUuid(UUID.fromString(challengeStringId));
        challenge.setRelatedChallenges(relatedChallenges);
        ChallengeDocument related1 = new ChallengeDocument();
        related1.setUuid(relatedId);
        ChallengeDocument related2 = new ChallengeDocument();
        related2.setUuid(relatedId2);
        ChallengeDocument related3 = new ChallengeDocument();
        related3.setUuid(relatedId3);
        ChallengeDto relatedDto1 = new ChallengeDto();
        relatedDto1.setChallengeId(relatedId);
        ChallengeDto relatedDto2 = new ChallengeDto();
        relatedDto2.setChallengeId(relatedId2);
        ChallengeDto relatedDto3 = new ChallengeDto();
        relatedDto3.setChallengeId(relatedId3);
        List<ChallengeDto> expectedRelated = List.of(relatedDto1, relatedDto2, relatedDto3);

        /*Cache cache = mock(Cache.class);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(cache.get(anyString(), eq(GenericResultDto.class))).thenReturn(null).thenReturn(new GenericResultDto<>(offset, limit, 3, expectedRelated.toArray(new ChallengeDto[0])));
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            GenericResultDto<ChallengeDto> value = invocation.getArgument(1);
            return null;
        }).when(cache).put(anyString(), any(GenericResultDto.class));*/

        when(challengeRepository.findByUuid(challenge.getUuid())).thenReturn(Mono.just(challenge));
        when(challengeRepository.findByUuid(related2.getUuid())).thenReturn(Mono.just(related2));
        when(challengeRepository.findByUuid(related3.getUuid())).thenReturn(Mono.just(related3));
        when(challengeConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.fromIterable(expectedRelated));

        // Act
        Mono<GenericResultDto<ChallengeDto>> resultMono = challengeService.getRelatedChallenges(challengeStringId, offset, limit);

        // Assert
        StepVerifier.create(resultMono)
                .expectSubscription()
                .expectNextCount(1)
                .expectComplete()
                .verify();

        verify(challengeRepository, times(1)).findByUuid(challenge.getUuid());

        // Act
        Mono<GenericResultDto<ChallengeDto>> resultMonoCached = challengeService.getRelatedChallenges(challengeStringId, offset, limit);

        // Assert
        StepVerifier.create(resultMonoCached)
                .expectSubscription()
                .expectNextCount(1)
                .expectComplete()
                .verify();

        verifyNoMoreInteractions(challengeRepository);
    }

}