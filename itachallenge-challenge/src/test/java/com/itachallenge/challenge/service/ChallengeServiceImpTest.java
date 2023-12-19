package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.SolutionDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.dto.SolutionDto;
import com.itachallenge.challenge.dto.RelatedDto;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.LanguageRepository;
import com.itachallenge.challenge.repository.SolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;


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
        expectedDto.setInfo(0, 1, 1, new ChallengeDto[]{challengeDto});

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(challengeConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.just(challengeDto));

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getChallengeById(challengeId.toString());

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getCount() == 1 && Arrays.equals(dto.getResults(), expectedDto.getResults()))
                .expectComplete()
                .verify();

        verify(challengeRepository).findByUuid(challengeId);
        verify(challengeConverter).convertDocumentFluxToDtoFlux(any(), any());
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
        verifyNoInteractions(challengeConverter);
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
        verifyNoInteractions(challengeConverter);
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
                .expectNextMatches(dto -> dto.getCount() == 1 && dto.getResults()[0].equals("resource deleted correctly"))
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
    void getAllChallenges_ChallengesExist_ChallengesReturned() {
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
        when(challengeConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.just(challengeDto1, challengeDto2, challengeDto3, challengeDto4));

        // Act
        Flux<ChallengeDto> result = challengeService.getAllChallenges(offset, limit);

        // Assert
        verify(challengeRepository).findAllByUuidNotNull();
        verify(challengeConverter).convertDocumentFluxToDtoFlux(any(), any());

        StepVerifier.create(result)
                .expectSubscription()
                .expectNextCount(4)
                .expectComplete()
                .verify();

        StepVerifier.create(result.skip(offset).take(limit))
                .expectSubscription()
                .expectNext(challengeDto2, challengeDto3)
                .expectComplete()
                .verify();

        StepVerifier.create(challengeRepository.findAllByUuidNotNull().skip(offset).take(limit))
                .expectSubscription()
                .expectNextCount(2)
                .expectComplete()
                .verify();
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
                .expectNextMatches(dto -> dto.getCount() == 2 && Arrays.equals(dto.getResults(), expectedLanguages))
                .expectComplete()
                .verify();

        verify(languageRepository).findAll();
        verify(languageConverter).convertDocumentFluxToDtoFlux(any(), any());
    }

    @Test
    void testGetSolutions() {
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
        verify(solutionConverter, times(2)).convertDocumentFluxToDtoFlux(any(), any());
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
        /*RelatedDto relatedDto1 = RelatedDto.builder()
                .relatedChallengeId(relatedId)
                .challengeTitle("Example")
                .level("Apprentice")
                .creationDate("01-01-2020")
                .popularity(5)
                .languages(relatedLanguages).build();
        RelatedDto relatedDto2 = RelatedDto.builder()
                .relatedChallengeId(relatedId2)
                .challengeTitle("Example2")
                .level("Apprentice2")
                .creationDate("01-01-2020")
                .popularity(5)
                .languages(relatedLanguages).build();
        RelatedDto relatedDto3 = RelatedDto.builder()
                .relatedChallengeId(relatedId3)
                .challengeTitle("Example3")
                .level("Apprentice3")
                .creationDate("01-01-2020")
                .popularity(5)
                .languages(relatedLanguages).build();*/
        RelatedDto relatedDto1 = new RelatedDto();
        relatedDto1.setRelatedChallengeId(relatedId);
        RelatedDto relatedDto2 = new RelatedDto();
        relatedDto2.setRelatedChallengeId(relatedId2);
        RelatedDto relatedDto3 = new RelatedDto();
        relatedDto3.setRelatedChallengeId(relatedId3);
        List<RelatedDto> expectedRelated = List.of(relatedDto1, relatedDto2, relatedDto3);

        when(challengeRepository.findByUuid(challenge.getUuid())).thenReturn(Mono.just(challenge));
        when(challengeRepository.findByUuid(related1.getUuid())).thenReturn(Mono.just(related1));
        when(challengeRepository.findByUuid(related2.getUuid())).thenReturn(Mono.just(related2));
        when(challengeRepository.findByUuid(related3.getUuid())).thenReturn(Mono.just(related3));
        when(relatedChallengeConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.fromIterable(expectedRelated));

        // Act
        Mono<GenericResultDto<RelatedDto>> resultMono = challengeService.getRelatedChallenges(challengeStringId);

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
        verify(relatedChallengeConverter, times(3)).convertDocumentFluxToDtoFlux(any(), any());
    }

    @Test
    void addSolution_ValidChallengeIdAndLanguageId_SolutionAdded() {
        // Arrange
        String challengeStringId = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
        String languageStringId = "660e1b18-0c0a-4262-a28a-85de9df6ac5f";
        UUID challengeId = UUID.fromString(challengeStringId);
        UUID languageId = UUID.fromString(languageStringId);
        UUID solutionId = UUID.randomUUID();

        SolutionDocument solution = new SolutionDocument(solutionId, "Solution 1", languageId);
        SolutionDto solutionDto = new SolutionDto(solutionId, "Solution 1", languageId, challengeId);
        ChallengeDocument challengeDocument = new ChallengeDocument();
        challengeDocument.setUuid(challengeId);

        when(challengeRepository.save(any(ChallengeDocument.class))).thenReturn(Mono.just(challengeDocument));
        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(solutionRepository.save(any(SolutionDocument.class))).thenReturn(Mono.just(solution));
        when(solutionConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.just(solutionDto));


        // Act
        Mono<SolutionDto> resultMono = challengeService.addSolution(solutionDto);
        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(resultDto -> {
                    assertThat(resultDto.getUuid()).isEqualTo(solutionId);
                    assertThat(resultDto.getSolutionText()).isEqualTo("Solution 1");
                    assertThat(resultDto.getIdLanguage()).isEqualTo(languageId);
                    assertThat(resultDto.getIdChallenge()).isEqualTo(challengeId);
                    return true;
                })
                .verifyComplete();

        verify(challengeRepository).findByUuid(challengeId);
        verify(solutionRepository).save(any(SolutionDocument.class));
        verify(solutionConverter).convertDocumentFluxToDtoFlux(any(), any());
    }
@Test
    void getChallengesByLanguageAndDifficultyTest() {
        // Arrange
        String idLanguage = "660e1b18-0c0a-4262-a28a-85de9df6ac5f";
        String difficulty = "EASY";
        UUID uuidLanguage = UUID.fromString(idLanguage);

        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 2, 2, new ChallengeDto[]{new ChallengeDto(), new ChallengeDto()});

        when(challengeRepository.findByLevelAndLanguages_IdLanguage(difficulty, uuidLanguage)).thenReturn(Flux.empty()); // Mock empty Flux, as it's not relevant for this test
        when(challengeConverter.convertDocumentFluxToDtoFlux(any(), eq(ChallengeDto.class))).thenReturn(Flux.just(expectedResult.getResults())); // Mock the conversion

        // Act
        Mono<GenericResultDto<ChallengeDto>> resultMono = challengeService.getChallengesByLanguageAndDifficulty(idLanguage, difficulty);

        // Assert
        StepVerifier.create(resultMono)
        .expectNextMatches(result -> result.getCount() == 2 && result.getResults() != null && result.getResults().length == 2)
        .verifyComplete();        
    }
    @Test
    void getAllChallenges_ChallengesExist_ChallengesReturned_b() {
        // Arrange
        int offset = 1;
        int offset10 = 10;
        int offset0 = 0;
        int limit2 = 2;
        int limit6 = 6;
        int limit0 = 0;

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
        when(challengeConverter.convertDocumentFluxToDtoFlux(any(), any())).thenReturn(Flux.just(challengeDto1, challengeDto2, challengeDto3, challengeDto4));

        // Act
        Flux<ChallengeDto> result = challengeService.getAllChallenges(offset, limit2);

        // Assert
        verify(challengeRepository).findAllByUuidNotNull();
        verify(challengeConverter).convertDocumentFluxToDtoFlux(any(), any());

        StepVerifier.create(result)
                .expectSubscription()
                .expectNextCount(4)
                .expectComplete()
                .verify();

        StepVerifier.create(result.skip(offset).take(limit2))
                .expectSubscription()
                .expectNext(challengeDto2, challengeDto3)
                .expectComplete()
                .verify();

        StepVerifier.create(result.skip(offset).take(limit6))
                .expectSubscription()
                .expectNext(challengeDto2, challengeDto3, challengeDto4)
                .expectComplete()
                .verify();

        StepVerifier.create(result.skip(offset).take(limit0))
                .expectSubscription()
                .expectNext()
                .expectComplete()
                .verify();

        //test

        StepVerifier.create(result.skip(offset10).take(limit0))
                .expectSubscription()
                .expectNext()
                .expectComplete()
                .verify();

        StepVerifier.create(result.skip(offset10).take(limit6))
                .expectSubscription()
                .expectNext()
                .expectComplete()
                .verify();

        StepVerifier.create(result.skip(offset0).take(limit6))
                .expectSubscription()
                .expectNext(challengeDto1, challengeDto2, challengeDto3, challengeDto4)
                .expectComplete()
                .verify();



        StepVerifier.create(challengeRepository.findAllByUuidNotNull().skip(offset).take(limit2))
                .expectSubscription()
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }
}
