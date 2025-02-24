package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.ChallengeListDto;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.enums.Topic;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private DocumentToDtoConverter<ResourceDocument, ResourceDto> resourceConverter;

    @Mock
    private IChallengeService challengeService;

    @InjectMocks
    private ResourceService resourceService;

    private ResourceDto resourceDto;
    private ResourceDocument resourceDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        UUID resourceId = UUID.randomUUID();
        String title = "Test Resource";
        String description = "Test Description";
        String url = "http://test.com";
        Topic topic = Topic.COMPONENTS;
        String contentType = "text/html";
        UUID challengeId = UUID.randomUUID();

        resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title(title)
                .description(description)
                .url(url)
                .topic(topic)
                .contentType(contentType)
                .challengeIds(Collections.singletonList(challengeId))
                .build();

        resourceDocument = ResourceDocument.builder()
                .resourceId(resourceId)
                .title(title)
                .description(description)
                .url(url)
                .topic(topic)
                .contentType(contentType)
                .challengeIds(Collections.singletonList(challengeId))
                .build();

        ChallengeDto challengeDto = ChallengeDto.builder()
                .challengeId(challengeId)
                .level("INTERMEDIATE")
                .creationDate("2023-02-24")
                .topic(Topic.DEBUGGING)
                .build();

        List<ChallengeDto> challengeDtos = Collections.singletonList(challengeDto);

        ChallengeListDto challengeListDto = ChallengeListDto.builder()
                .results(challengeDtos)
                .total(challengeDtos.size())
                .build();

        when(challengeService.getChallengesByTopic(Topic.valueOf(anyString()), anyInt(), anyInt()))
                .thenReturn(Mono.just(challengeListDto));
    }

    @Test
    void testCreateResourceWithMatchingChallenge() {
        Mono<ResourceDto> resourceMono = resourceService.createResource(resourceDto);

        StepVerifier.create(resourceMono)
                .expectNextMatches(result -> {
                    return result.getChallengeIds().size() == 1
                            && result.getChallengeIds().get(0).equals(resourceDto.getChallengeIds().get(0));
                })
                .verifyComplete();
    }

    @Test
    void createResource_ValidResource_Success() {
        when(resourceConverter.convertDtoToDocument(any(), any())).thenReturn(resourceDocument);
        when(resourceRepository.save(any())).thenReturn(Mono.just(resourceDocument));
        when(resourceConverter.convertDocumentToDto(any(), any())).thenReturn(resourceDto);


        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getResourceId().equals(resourceDto.getResourceId()) &&
                        dto.getTitle().equals(resourceDto.getTitle()) &&
                        dto.getDescription().equals(resourceDto.getDescription()) &&
                        dto.getUrl().equals(resourceDto.getUrl()) &&
                        dto.getTopic().equals(resourceDto.getTopic()) &&
                        dto.getContentType().equals(resourceDto.getContentType()) &&
                        dto.getChallengeIds().equals(resourceDto.getChallengeIds()))
                .expectComplete()
                .verify();
    }
}
