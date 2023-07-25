package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        expectedDto.setInfo(0, 1, 1, new ChallengeDto[]{challengeDto});

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(converter.fromChallengeToChallengeDto(any())).thenReturn(Flux.just(challengeDto));

        // Act
        Mono<GenericResultDto<ChallengeDto>> result = challengeService.getChallengeById(challengeId.toString());

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getCount() == 1 && Arrays.equals(dto.getResults(), expectedDto.getResults()))
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
    void testGetChallengesByLanguagesAndLevel_ChallengeNotFoundException() {
        Set<String> languages = new HashSet<>();
        languages.add("Not_languages");
        Set<String> levels = new HashSet<>();
        levels.add("Not_levels");

        //Listas vacías
        List<ChallengeDocument> challengeDocuments = new ArrayList<>();
        List<ChallengeDto> challengeDtos = new ArrayList<>();

        when(challengeRepository.findByLevelIn(any())).thenReturn(Flux.fromIterable(challengeDocuments));
        when(challengeRepository.findByLanguages_LanguageNameIn(any())).thenReturn(Flux.fromIterable(challengeDocuments));
        when(converter.fromChallengeToChallengeDto(any())).thenReturn(Flux.fromIterable(challengeDtos));

        Mono<GenericResultDto<ChallengeDto>> resultMono = challengeService.getChallengesByLanguagesAndLevel(languages, levels);

        StepVerifier.create(resultMono)
                .expectError(ChallengeNotFoundException.class)
                .verify();
    }

    @Test
    void testGetChallengesByLanguagesAndLevel() {
        List<ChallengeDocument> challengeDocuments = new ArrayList<>();
        List<ChallengeDto> challengeDtos = new ArrayList<>();

        Set<String> languages = new HashSet<>();
        languages.add("Javascript");
        Set<String> levels = new HashSet<>();
        levels.add("MEDIUM");

        ChallengeDto challengeDto = new ChallengeDto();
        challengeDto.setTitle("Challenge title");
        challengeDto.setLevel("MEDIUM");
        challengeDto.setLanguages(Set.of(
                new LanguageDto(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), "Javascript")
        ));

        challengeDtos.add(challengeDto);

        ChallengeDocument challengeDocument = new ChallengeDocument();
        challengeDocument.setLevel("MEDIUM");
        challengeDocument.setLanguages(Set.of(
                new LanguageDocument(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), "Javascript")
        ));

        challengeDocuments.add(challengeDocument);

        when(challengeRepository.findByLanguages_LanguageNameIn(any())).thenReturn(Flux.fromIterable(challengeDocuments));
        when(challengeRepository.findByLevelIn(any())).thenReturn(Flux.fromIterable(challengeDocuments));
        when(converter.fromChallengeToChallengeDto(any())).thenReturn(Flux.fromIterable(challengeDtos));

        Mono<GenericResultDto<ChallengeDto>> resultMono = challengeService.getChallengesByLanguagesAndLevel(languages, levels);

        GenericResultDto<ChallengeDto> genericResultDto = new GenericResultDto<>();
        genericResultDto.setInfo(0, 5, 1, new ChallengeDto[] {challengeDto});

        StepVerifier.create(resultMono)
                .assertNext(resultDto -> {
                    assertEquals(genericResultDto.getOffset(), resultDto.getOffset());
                    assertEquals(genericResultDto.getLimit(), resultDto.getLimit());
                    assertEquals(genericResultDto.getCount(), resultDto.getCount());
                    assertEquals(challengeDto.getLevel(), resultDto.getResults()[0].getLevel());
                    assertEquals(challengeDto.getLanguages(), resultDto.getResults()[0].getLanguages());
                })
                .verifyComplete();
    }

}
