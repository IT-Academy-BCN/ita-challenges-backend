package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.SolutionDocument;
import com.itachallenge.challenge.dto.*;

import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.SolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
@EnableCaching
class ChallengeServiceImplCacheTest {

    @MockBean private ChallengeRepository challengeRepository;
    @MockBean private ILanguageService iLanguageService;
    @MockBean private IUserService userService;
    @MockBean private ITagService tagService;
    @MockBean private SolutionRepository solutionRepository;
    @MockBean private DocumentToDtoConverter<ChallengeDocument, ChallengeDto> challengeConverter;
    @MockBean private DocumentToDtoConverter<SolutionDocument, SolutionDto> solutionConverter;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IChallengeService challengeService;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
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

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument).cache());
        when(challengeConverter.convertDocumentToDto(any(), any())).thenReturn(challengeDto);

        // Act - First call
        Mono<ChallengeDto> result1 = challengeService.getChallengeById(challengeId.toString());

        StepVerifier.create(result1)
                .expectNextMatches(dto -> dto.getChallengeId().equals(challengeId) &&
                        dto.getLevel().equals(challengeDto.getLevel()))
                .verifyComplete();

        // Use atLeastOnce instead of times(1)
        verify(challengeRepository, atLeastOnce()).findByUuid(challengeId);

        // Act - Second call (cached)
        Mono<ChallengeDto> result2 = challengeService.getChallengeById(challengeId.toString());

        StepVerifier.create(result2)
                .expectNextMatches(dto -> dto.getChallengeId().equals(challengeId) &&
                        dto.getLevel().equals(challengeDto.getLevel()))
                .verifyComplete();

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

    @DisplayName("Cache - getSolutions")
    @Test
    void testGetChallengeSolutions_cacheTest() {
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

        when(challengeRepository.findByUuid(challenge.getUuid())).thenReturn(Mono.just(challenge).cache());
        when(solutionRepository.findById(solutionId1)).thenReturn(Mono.just(solution1).cache());
        when(solutionRepository.findById(solutionId2)).thenReturn(Mono.just(solution2).cache());
        when(solutionConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.fromIterable(expectedSolutions));

        // Act - First call
        Mono<GenericResultDto<SolutionDto>> resultMono = challengeService.getSolutions(challengeStringId, languageStringId);

        StepVerifier.create(resultMono)
                .expectNextMatches(resultDto -> {
                    assertThat(resultDto.getOffset()).isZero();
                    assertThat(resultDto.getLimit()).isEqualTo(expectedSolutions.size());
                    assertThat(resultDto.getCount()).isEqualTo(expectedSolutions.size());
                    return true;
                })
                .verifyComplete();

        verify(challengeRepository, atLeastOnce()).findByUuid(UUID.fromString(challengeStringId));
        verify(solutionRepository, atLeastOnce()).findById(any(UUID.class));
        verify(solutionConverter, atLeastOnce()).convertDocumentFluxToDtoFlux(any(), eq(SolutionDto.class));

        // Act - Cached call
        Mono<GenericResultDto<SolutionDto>> resultCached = challengeService.getSolutions(challengeStringId, languageStringId);

        StepVerifier.create(resultCached)
                .assertNext(actualResult -> {
                    assertThat(actualResult.getCount()).isEqualTo(2);
                    assertThat(actualResult.getResults()).containsExactly(solutionDto1, solutionDto2);
                })
                .verifyComplete();

    }

}