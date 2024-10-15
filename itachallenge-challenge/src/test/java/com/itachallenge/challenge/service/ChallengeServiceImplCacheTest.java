package com.itachallenge.challenge.service;

import com.itachallenge.challenge.config.CacheConfig;
import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.SolutionDocument;
import com.itachallenge.challenge.dto.*;

import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.LanguageRepository;

import com.itachallenge.challenge.repository.SolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {CacheConfig.class})
class ChallengeServiceImplCacheTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private SolutionRepository solutionRepository;

    @Mock
    private DocumentToDtoConverter<ChallengeDocument, ChallengeDto> challengeConverter;

    @Mock
    private DocumentToDtoConverter<LanguageDocument, LanguageDto> languageConverter;

    @Mock
    private DocumentToDtoConverter<SolutionDocument, SolutionDto> solutionConverter;

    @InjectMocks
    private ChallengeServiceImp challengeService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Cache cache = cacheManager.getCache("challenges");
        assertThat(cache).as("Cache 'challenges' should not be null").isNotNull(); // Ensure cache exists
        cache.clear();
    }

    @DisplayName("Cache - getChallengeById")
    @Test
    void getChallengeById_cacheTest() {
        // Arrange
        UUID challengeId = UUID.randomUUID();
        ChallengeDocument challengeDocument = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();
        challengeDto.setChallengeId(challengeId);
        challengeDto.setLevel("EASY");

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

        when(challengeRepository.count()).thenReturn(Mono.just(4L));
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

        verify(challengeRepository, times(1)).count();
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

    //TODO - Add more tests for caching(challengesByLanguageOrDifficulty, solutions, testingParams)

    @DisplayName("Cache - getChallengesByLanguageOrDifficulty")
    @Test
    void testGetChallengesByLanguageOrDifficulty_cacheTest(){
        // Arrange
        String idLanguage = UUID.randomUUID().toString();
        String level = "EASY";
        int offset = 0;
        int limit = 2;

        ChallengeDocument challengeDocument = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        when(languageRepository.findByIdLanguage(UUID.fromString(idLanguage))).thenReturn(Mono.just(new LanguageDocument()));
        when(challengeRepository.findByLevelAndLanguages_IdLanguage(level, UUID.fromString(idLanguage))).thenReturn(Flux.just(challengeDocument));
        when(challengeConverter.convertDocumentToDto(challengeDocument, ChallengeDto.class)).thenReturn(challengeDto);

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getChallengesByLanguageOrDifficulty(Optional.of(idLanguage), Optional.of(level), offset, limit);

        // Assert
        StepVerifier.create(result)
                .assertNext(actualResult -> {
                    assertThat(actualResult.getCount()).isEqualTo(1);
                    assertThat(actualResult.getResults()).containsExactly(challengeDto);
                })
                .verifyComplete();

        verify(challengeRepository, times(1)).findByLevelAndLanguages_IdLanguage(level, UUID.fromString(idLanguage));
        verify(challengeConverter, times(1)).convertDocumentToDto(challengeDocument, ChallengeDto.class);

        // Act - Cached Results
        Mono<GenericResultDto<ChallengeDto>> resultCached = challengeService.getChallengesByLanguageOrDifficulty(Optional.of(idLanguage), Optional.of(level), offset, limit);

        // Assert - Cached Results
        StepVerifier.create(resultCached)
                .assertNext(actualResult -> {
                    assertThat(actualResult.getCount()).isEqualTo(1);
                    assertThat(actualResult.getResults()).containsExactly(challengeDto);
                })
                .verifyComplete();

        verifyNoMoreInteractions(challengeRepository, challengeConverter);
    }

    @DisplayName("Cache - getSolutions")
    @Test
    void testGetChallengeSolutions_cacheTest(){
        // Arrange
        String challengeStringId = "e5f71456-62db-4323-a8d2-1d473d28a931";
        String languageStringId = "b5f78901-28a1-49c7-98bd-1ee0a555c678";
        UUID languageId = UUID.fromString(languageStringId);
        UUID solutionId1 = UUID.fromString("c8a5440d-6466-463a-bccc-7fefbe9396e4");
        UUID solutionId2 = UUID.fromString("0864463e-eb7c-4bb3-b8bc-766d71ab38b5");

        ChallengeDocument challenge = new ChallengeDocument();
        challenge.setUuid(UUID.fromString(challengeStringId));
        SolutionDocument solution1 = new SolutionDocument(solutionId1, "Solution 1", languageId);
        SolutionDocument solution2 = new SolutionDocument(solutionId2, "Solution 2", languageId);
        challenge.setSolutions(Arrays.asList(solution1.getUuid(), solution2.getUuid()));
        SolutionDto solutionDto1 = new SolutionDto(solution1.getUuid(), solution1.getSolutionText(), solution1.getIdLanguage());
        SolutionDto solutionDto2 = new SolutionDto(solution2.getUuid(), solution2.getSolutionText(), solution2.getIdLanguage());
        List<SolutionDto> expectedSolutions = List.of(solutionDto1, solutionDto2);

        when(challengeRepository.findByUuid(challenge.getUuid())).thenReturn(Mono.just(challenge));
        when(solutionRepository.findById(solutionId1)).thenReturn(Mono.just(solution1));
        when(solutionRepository.findById(solutionId2)).thenReturn(Mono.just(solution2));
        when(solutionConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.fromIterable(expectedSolutions));

        // Act
        Mono<GenericResultDto<SolutionDto>> resultMono = challengeService.getSolutions(challengeStringId, languageStringId);

        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(resultDto -> {
                    assertThat(resultDto.getOffset()).isZero();
                    assertThat(resultDto.getLimit()).isEqualTo(expectedSolutions.size());
                    assertThat(resultDto.getCount()).isEqualTo(expectedSolutions.size());
                    return true;
                })
                .verifyComplete();

        verify(challengeRepository).findByUuid(UUID.fromString(challengeStringId));
        verify(solutionRepository, times(2)).findById(any(UUID.class));
        verify(solutionConverter, times(1)).convertDocumentFluxToDtoFlux(any(), any());

        // Act - Cached Results
        Mono<GenericResultDto<SolutionDto>> resultCached = challengeService.getSolutions(challengeStringId, languageStringId);

        // Assert - Cached Results
        StepVerifier.create(resultCached)
                .assertNext(actualResult -> {
                    assertThat(actualResult.getCount()).isEqualTo(2);
                    assertThat(actualResult.getResults()).containsExactly(solutionDto1, solutionDto2);
                })
                .verifyComplete();

        verifyNoMoreInteractions(challengeRepository, challengeConverter);
    }
}
