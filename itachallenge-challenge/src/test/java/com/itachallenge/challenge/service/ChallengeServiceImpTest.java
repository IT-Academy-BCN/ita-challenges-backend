package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.SolutionDocument;
import com.itachallenge.challenge.document.TestingValueDocument;
import com.itachallenge.challenge.dto.*;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.exception.ResourceNotFoundException;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.LanguageRepository;
import com.itachallenge.challenge.repository.SolutionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class ChallengeServiceImpTest {

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
    @Mock
    private DocumentToDtoConverter<ChallengeDocument, RelatedDto> relatedChallengeConverter = new DocumentToDtoConverter<>();
    @Mock
    private DocumentToDtoConverter<TestingValueDocument, TestingValueDto> testingValueConverter;

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
        challengeDto.setChallengeId(challengeId);
        challengeDto.setLevel("EASY");

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(challengeConverter.convertDocumentToDto(any(), any())).thenReturn(challengeDto);

        // Act
        Mono<ChallengeDto> result = challengeService.getChallengeById(challengeId.toString());

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getChallengeId().equals(challengeId) &&
                        dto.getLevel().equals(challengeDto.getLevel())
                )
                .expectComplete()
                .verify();

        verify(challengeRepository).findByUuid(challengeId);
        verify(challengeConverter).convertDocumentToDto(any(), any());
    }

    @Test
    void getChallengeById_InvalidId_ErrorThrown() {
        // Arrange
        String invalidId = "invalid-id";

        // Act
        Mono<ChallengeDto> result = challengeService.getChallengeById(invalidId);

        // Assert
        StepVerifier.create(result)
                .expectError(BadUUIDException.class)
                .verify();

        verifyNoInteractions(challengeRepository);
        verifyNoInteractions(challengeConverter);
    }

//    @Test
//    void getChallengeById_NonexistentId_ErrorThrown() {
//        // Arrange
//        UUID challengeId = UUID.randomUUID();
//
//        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.empty());
//
//        // Act
//        Mono<ChallengeDto> result = challengeService.getChallengeById(challengeId.toString());
//
//        // Assert
//        StepVerifier.create(result)
//                .expectError(ChallengeNotFoundException.class)
//                .verify();
//
//        verify(challengeRepository).findByUuid(challengeId);
//        verifyNoInteractions(challengeConverter);
//    }

    @Test
    void getChallengeByIdWhenNonexistentIdThenReturnsError_test() {

        String idString = "4f8a6c91-8a9d-49b0-9f2c-3e67d2b18b7d";

        UUID id = UUID.fromString(idString);

        when(challengeRepository.findByUuid(id)).thenReturn(Mono.empty());

        Mono<ChallengeDto> result = challengeService.getChallengeById(idString);

        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof ChallengeNotFoundException
                        && error.getMessage().equals("Challenge with id " + id + " not found.")
                );
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
        Mono<String> result = challengeService.removeResourcesByUuid(resourceId.toString());

        // Assert
        StepVerifier.create(result)
                .expectNext("Resource removed successfully")
                .verifyComplete();

        verify(challengeRepository).findAllByResourcesContaining(resourceId);
        verify(challengeRepository).save(any(ChallengeDocument.class));
    }

    @Test
    void removeResourcesByUuid_NonexistentId_ErrorThrown() {
        // Arrange
        UUID resourceId = UUID.randomUUID();

        when(challengeRepository.findAllByResourcesContaining(resourceId)).thenReturn(Flux.empty());

        // Act
        Mono<String> result = challengeService.removeResourcesByUuid(resourceId.toString());

        // Assert
        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(challengeRepository).findAllByResourcesContaining(resourceId);
        verifyNoMoreInteractions(challengeRepository);
    }

    @Test
    void removeResourcesByUuid_InvalidFormatId_ReturnsBadUUIDException() {
        // Arrange
        String invalidId = "invalid_uuid";

        // Act
        Mono<String> result = challengeService.removeResourcesByUuid(invalidId);

        // Assert
        StepVerifier.create(result)
                .expectError(BadUUIDException.class)
                .verify();

        verifyNoInteractions(challengeRepository);
        verifyNoInteractions(challengeConverter);
    }

    @Test
    void getAllChallenges_ChallengesExist_ChallengesReturned() {
        int offset = 0;
        int limit = 2;
        ChallengeDocument challengeDocument = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        when(challengeRepository.count()).thenReturn(Mono.just(2L));
        when(challengeRepository.findAllByUuidNotNullExcludingTestingValues())
                .thenReturn(Flux.just(challengeDocument));
        when(challengeConverter.convertDocumentToDto(any(), any()))
                .thenReturn(challengeDto);

        StepVerifier.create(challengeService.getAllChallenges(offset, limit))
                .expectNextMatches(result -> {
                    System.out.println("Results size: " + result.getResults().length);
                    System.out.println("First result: " + (result.getResults().length > 0 ? result.getResults()[0] : "None"));
                    assertThat(result.getResults()).hasSize(1);
                    assertThat(result.getResults()[0]).isEqualTo(challengeDto);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void getAllLanguages_LanguageExist_LanguageReturned() {
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
                .expectNextMatches(dto -> dto.getCount() == 0 && Arrays.equals(dto.getResults(), expectedLanguages))
                .expectComplete()
                .verify();

        verify(languageRepository).findAll();
        verify(languageConverter).convertDocumentFluxToDtoFlux(any(), any());
    }

    @Test
    void getSolutions_ReturnsExpectedSolutions() {
        // Arrange
        String challengeStringId = "e5f71456-62db-4323-a8d2-1d473d28a931";
        String languageStringId = "b5f78901-28a1-49c7-98bd-1ee0a555c678";
        UUID languageId = UUID.fromString(languageStringId);
        UUID solutionId1 = UUID.fromString("c8a5440d-6466-463a-bccc-7fefbe9396e4");
        UUID solutionId2 = UUID.fromString("0864463e-eb7c-4bb3-b8bc-766d71ab38b5");
        SolutionDocument solution1 = new SolutionDocument(solutionId1, "Solution 1", languageId);
        SolutionDocument solution2 = new SolutionDocument(solutionId2, "Solution 2", languageId);
        ChallengeDocument challenge = new ChallengeDocument();
        challenge.setUuid(UUID.fromString(challengeStringId));
        challenge.setSolutions(Arrays.asList(solution1.getUuid(), solution2.getUuid()));

        when(challengeRepository.findByUuid(challenge.getUuid())).thenReturn(Mono.just(challenge));
        when(solutionRepository.findById(solutionId1)).thenReturn(Mono.just(solution1));
        when(solutionRepository.findById(solutionId2)).thenReturn(Mono.just(solution2));

        // Act
        Mono<GenericResultDto<SolutionDto>> resultMono = challengeService.getSolutions(challengeStringId, languageStringId);

        // Assert
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    List<SolutionDto> actualSolutions = List.of(result.getResults());
                    assertThat(actualSolutions).hasSize(2);
                    assertThat(actualSolutions).extracting(SolutionDto::getSolutionText)
                            .containsExactlyInAnyOrder("Solution 1", "Solution 2");
                })
                .verifyComplete();
    }
    @Test
    void getSolutions_InvalidChallengeId_ThrowsBadUUIDException() {
        String invalidChallengeId = "invalid-challenge-id";
        String validLanguageId = "valid-language-id";
        StepVerifier.create(challengeService.getSolutions(invalidChallengeId, validLanguageId))
                .expectError(BadUUIDException.class)
                .verify();
    }

    @Test
    void getSolutions_NoSolutionsForLanguage_EmptyResult() {
        String challengeStringId = "e5f71456-62db-4323-a8d2-1d473d28a931";
        String languageStringId = "b5f78901-28a1-49c7-98bd-1ee0a555c678";
        UUID languageId = UUID.fromString(languageStringId);
        UUID solutionId1 = UUID.fromString("c8a5440d-6466-463a-bccc-7fefbe9396e4");
        SolutionDocument solution1 = new SolutionDocument(solutionId1, "Solution 1", UUID.randomUUID());
        ChallengeDocument challenge = new ChallengeDocument();
        challenge.setUuid(UUID.fromString(challengeStringId));
        challenge.setSolutions(Arrays.asList(solution1.getUuid()));

        when(challengeRepository.findByUuid(challenge.getUuid())).thenReturn(Mono.just(challenge));
        when(solutionRepository.findById(solutionId1)).thenReturn(Mono.just(solution1));

        StepVerifier.create(challengeService.getSolutions(challengeStringId, languageStringId))
                .expectNextMatches(result -> result.getResults().length == 0)
                .verifyComplete();
    }

    @Test
    void testGetSolutions_InvalidChallengeId() {
        // Arrange
        String invalidChallengeStringId = "invalid_challenge_id";
        String languageStringId = "b5f78901-28a1-49c7-98bd-1ee0a555c678";

        // Act & Assert
        StepVerifier.create(challengeService.getSolutions(invalidChallengeStringId, languageStringId))
                .expectError(BadUUIDException.class)
                .verify();

        verify(challengeRepository, never()).findByUuid(any(UUID.class));
        verify(solutionRepository, never()).findById(any(UUID.class));
        verify(solutionConverter, never()).convertDocumentFluxToDtoFlux(any(), any());
    }

    @Test
    void testGetSolutions_InvalidLanguageId() {
        // Arrange
        String challengeStringId = "e5f71456-62db-4323-a8d2-1d473d28a931";
        String invalidLanguageStringId = "invalid_language_id";

        // Act & Assert
        StepVerifier.create(challengeService.getSolutions(challengeStringId, invalidLanguageStringId))
                .expectError(BadUUIDException.class)
                .verify();

        verify(challengeRepository, never()).findByUuid(any(UUID.class));
        verify(solutionRepository, never()).findById(any(UUID.class));
        verify(solutionConverter, never()).convertDocumentFluxToDtoFlux(any(), any());
    }

    @Test
    void testGetSolutions_ChallengeNotFound() {
        // Arrange
        String nonExistentChallengeStringId = "2f948de0-6f0c-4089-90b9-7f70a0812322";
        String languageStringId = "b5f78901-28a1-49c7-98bd-1ee0a555c678";

        // Simulate that the challenge with the specified UUID is not found
        when(challengeRepository.findByUuid(any(UUID.class))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(challengeService.getSolutions(nonExistentChallengeStringId, languageStringId))
                .expectError(ChallengeNotFoundException.class)
                .verify();

        verify(challengeRepository).findByUuid(any(UUID.class));
        verify(solutionRepository, never()).findById(any(UUID.class));
        verify(solutionConverter, never()).convertDocumentFluxToDtoFlux(any(), any());
    }

    @Test
    void testGetRelatedChallenges() {
        // Arrange
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
        when(challengeRepository.findByUuid(related1.getUuid())).thenReturn(Mono.just(related1));
        when(challengeRepository.findByUuid(related2.getUuid())).thenReturn(Mono.just(related2));
        when(challengeRepository.findByUuid(related3.getUuid())).thenReturn(Mono.just(related3));
        when(challengeConverter.convertDocumentFluxToDtoFlux(any(), any()))
                .thenReturn(Flux.fromIterable(expectedRelated));

        // Act
        Mono<GenericResultDto<ChallengeDto>> resultMono = challengeService.getRelatedChallenges(challengeStringId, 0, challenge.getRelatedChallenges().size());

        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(resultDto -> {
                    assertThat(resultDto.getOffset()).isZero();
                    assertThat(resultDto.getLimit()).isEqualTo(expectedRelated.size());
                    assertThat(resultDto.getCount()).isEqualTo(expectedRelated.size());
                    return true;
                })
                .verifyComplete();

        verify(challengeRepository).findByUuid(UUID.fromString(challengeStringId));
        verify(challengeRepository, times(4)).findByUuid(any(UUID.class));
        verify(challengeConverter, times(1)).convertDocumentFluxToDtoFlux(any(), any());
    }
    @Test
    void addSolution_ValidChallengeIdAndLanguageId_SolutionAdded() {
        SolutionDto solutionDto = new SolutionDto();
        solutionDto.setIdChallenge(UUID.randomUUID());
        solutionDto.setIdLanguage(UUID.randomUUID());
        solutionDto.setSolutionText("Sample Solution");

        ChallengeDocument challenge = new ChallengeDocument();
        challenge.setUuid(solutionDto.getIdChallenge());
        challenge.setSolutions(new ArrayList<>());

        SolutionDocument solutionDocument = new SolutionDocument();
        solutionDocument.setSolutionText(solutionDto.getSolutionText());
        solutionDocument.setIdLanguage(solutionDto.getIdLanguage());
        solutionDocument.setUuid(UUID.randomUUID());

        when(challengeRepository.findByUuid(solutionDto.getIdChallenge())).thenReturn(Mono.just(challenge));
        when(solutionRepository.save(any(SolutionDocument.class))).thenReturn(Mono.just(solutionDocument));
        when(challengeRepository.save(any(ChallengeDocument.class))).thenReturn(Mono.just(challenge));
        when(solutionConverter.convertDocumentToDto(any(SolutionDocument.class), eq(SolutionDto.class))).thenReturn(solutionDto);

        StepVerifier.create(challengeService.addSolution(solutionDto))
                .expectNextMatches(result -> result.getSolutionText().equals("Sample Solution"))
                .verifyComplete();
    }


    @Test
    void addSolution_ConversionToSolutionDtoReturnsNull_ErrorThrown() {
        // Arrange
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        SolutionDto solutionDto = new SolutionDto();
        solutionDto.setIdChallenge(challengeId);
        solutionDto.setIdLanguage(languageId);
        solutionDto.setSolutionText("Sample solution");

        ChallengeDocument challengeDocument = new ChallengeDocument();
        challengeDocument.setUuid(challengeId);
        challengeDocument.setSolutions(new ArrayList<>());

        SolutionDocument savedSolutionDocument = new SolutionDocument();
        savedSolutionDocument.setUuid(UUID.randomUUID());
        savedSolutionDocument.setSolutionText("Sample solution");
        savedSolutionDocument.setIdLanguage(languageId);

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(solutionRepository.save(any(SolutionDocument.class))).thenReturn(Mono.just(savedSolutionDocument));
        when(challengeRepository.save(any(ChallengeDocument.class))).thenReturn(Mono.just(challengeDocument));
        when(solutionConverter.convertDocumentToDto(any(SolutionDocument.class), eq(SolutionDto.class)))
                .thenReturn(null);

        // Act
        Mono<SolutionDto> result = challengeService.addSolution(solutionDto);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(challengeRepository).findByUuid(challengeId);
        verify(solutionRepository).save(any(SolutionDocument.class));
        verify(challengeRepository).save(any(ChallengeDocument.class));
        verify(solutionConverter).convertDocumentToDto(any(SolutionDocument.class), eq(SolutionDto.class));
    }
    @Test
    void testGetRelatedChallenges_ReturnedAll() {
        // Arrange
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
        when(challengeRepository.findByUuid(related1.getUuid())).thenReturn(Mono.just(related1));
        when(challengeRepository.findByUuid(related2.getUuid())).thenReturn(Mono.just(related2));
        when(challengeRepository.findByUuid(related3.getUuid())).thenReturn(Mono.just(related3));
        when(challengeConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.fromIterable(expectedRelated));

        // Act
        Mono<GenericResultDto<ChallengeDto>> resultMono = challengeService.getRelatedChallenges(challengeStringId, 0, challenge.getRelatedChallenges().size());

        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(resultDto -> {
                    assertThat(resultDto.getOffset()).isZero();
                    assertThat(resultDto.getLimit()).isEqualTo(expectedRelated.size());
                    assertThat(resultDto.getCount()).isEqualTo(expectedRelated.size());
                    return true;
                })
                .expectComplete()
                .verify();

    }

    @Test
    void testGetRelatedChallenges_Return_OffsetOne_LimitOne() {
        // Arrange
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
        when(challengeRepository.findByUuid(related1.getUuid())).thenReturn(Mono.just(related1));
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

    }

    @Test
    void getTestingParamsByChallengeIdAndLanguageId_validId_ChallengeFound() {
        // Arrange
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        ChallengeDocument challengeDocument = new ChallengeDocument();
        challengeDocument.setUuid(challengeId);
        challengeDocument.setLanguages(Collections.singleton(new LanguageDocument(languageId, "English")));
        TestingValueDocument testingValueDocument = new TestingValueDocument(Collections.singletonList("input"), Collections.singletonList("output"));
        challengeDocument.setTestingValues(Collections.singletonList(testingValueDocument));

        TestingValueDto testingValueDto = TestingValueDto.builder()
                .inParam(Collections.singletonList("input"))
                .outParam(Collections.singletonList("output"))
                .build();

        List<TestingValueDto> expectedTestingValues = Collections.singletonList(testingValueDto);

        Map<String, Object> expectedResult = new LinkedHashMap<>();
        expectedResult.put("uuid_challenge", challengeId.toString());
        expectedResult.put("uuid_language", languageId.toString());
        expectedResult.put("test_params", expectedTestingValues);

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(testingValueConverter.convertDocumentToDto(testingValueDocument, TestingValueDto.class)).thenReturn(testingValueDto);

        // Act
        Mono<Map<String, Object>> result = challengeService.getTestingParamsByChallengeIdAndLanguageId(challengeId.toString(), languageId.toString());

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response).containsEntry("uuid_challenge", challengeId.toString());
                    assertThat(response).containsEntry("uuid_language", languageId.toString());
                    Object testParamsObject = response.get("test_params");
                    if (testParamsObject instanceof List) {
                        List<?> testParamsList = (List<?>) testParamsObject;
                        if (!testParamsList.isEmpty() && testParamsList.get(0) instanceof TestingValueDto) {
                            List<TestingValueDto> testParams = (List<TestingValueDto>) testParamsList;
                            assertThat(testParams.get(0).getInParam()).isEqualTo(expectedTestingValues.get(0).getInParam());
                            assertThat(testParams.get(0).getOutParam()).isEqualTo(expectedTestingValues.get(0).getOutParam());
                        }
                    }
                })
                .expectComplete()
                .verify();

        verify(challengeRepository).findByUuid(challengeId);
        verify(testingValueConverter).convertDocumentToDto(testingValueDocument, TestingValueDto.class);
    }

    @Test
    void getChallengesByLanguageOrDifficulty_NoChallengesFound_ExceptionThrown() {
        // Arrange
        when(challengeRepository.findAllByUuidNotNullExcludingTestingValues()).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(challengeService.getChallengesByLanguageOrDifficulty(Optional.empty(), Optional.empty(), 0, 1))
                .expectErrorMatches(error -> error instanceof ChallengeNotFoundException && error.getMessage().equals("No challenges found"))
                .verify();

        verify(challengeRepository).findAllByUuidNotNullExcludingTestingValues();
    }

    @Test
    void getTestingParamsByChallengeIdAndLanguageId_invalidLanguageId_ChallengeNotFoundExceptionThrown() {
        // Arrange
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        UUID anotherLanguageId = UUID.randomUUID();
        ChallengeDocument challengeDocument = new ChallengeDocument();
        challengeDocument.setUuid(challengeId);
        challengeDocument.setLanguages(Collections.singleton(new LanguageDocument(languageId, "English")));

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));

        // Act
        Mono<Map<String, Object>> result = challengeService.getTestingParamsByChallengeIdAndLanguageId(challengeId.toString(), anotherLanguageId.toString());

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof ChallengeNotFoundException && error.getMessage().equals("Language " + anotherLanguageId + " not found in Challenge " + challengeId))
                .verify();

        verify(challengeRepository).findByUuid(challengeId);
    }

    @Test
    void getChallengesByLanguageAndDifficulty_ValidInput_ChallengesReturned() {
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
                    assertThat(actualResult.getResults()[0]).usingRecursiveComparison().isEqualTo(challengeDto);
                })
                .verifyComplete();
    }

    @Test
    void getChallengesByLanguageOrDifficulty_OnlyIdLanguagePresent_ChallengesReturned() {
        // Arrange
        String languageId = UUID.randomUUID().toString();
        ChallengeDocument challengeDocument = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        when(languageRepository.findByIdLanguage(UUID.fromString(languageId))).thenReturn(Mono.just(new LanguageDocument()));
        when(challengeRepository.findByLanguages_IdLanguage(UUID.fromString(languageId))).thenReturn(Flux.just(challengeDocument));
        when(challengeConverter.convertDocumentToDto(any(), any())).thenReturn(challengeDto);

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getChallengesByLanguageOrDifficulty(Optional.of(languageId), Optional.empty(), 0, 1);

        // Assert
        StepVerifier.create(result)
                .assertNext(actualResult -> {
                    assertThat(actualResult.getResults()[0]).usingRecursiveComparison().isEqualTo(challengeDto);
                })
                .verifyComplete();
    }

    @Test
    void getChallengesByLanguageOrDifficulty_OnlyLevelPresent_ChallengesReturned() {
        // Arrange
        String difficulty = "HARD";
        ChallengeDocument challengeDocument = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        GenericResultDto<ChallengeDto> genericResultDto = new GenericResultDto<>();
        genericResultDto.setResults(new ChallengeDto[]{challengeDto});

        when(challengeRepository.findByLevel(difficulty)).thenReturn(Flux.just(challengeDocument));
        when(challengeConverter.convertDocumentToDto(any(), any())).thenReturn(ChallengeDto.builder().build());

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getChallengesByLanguageOrDifficulty(Optional.empty(), Optional.of(difficulty), 0, 1);

        // Assert
        StepVerifier.create(result)
                .assertNext(actualResult -> {
                    assertThat(actualResult.getResults()[0]).usingRecursiveComparison().isEqualTo(genericResultDto.getResults()[0]);
                })
                .verifyComplete();
    }
}
