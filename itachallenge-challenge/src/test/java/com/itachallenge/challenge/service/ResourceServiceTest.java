package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.ChallengeListDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.enums.AssociationType;
import com.itachallenge.challenge.enums.ResourceContentType;
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
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private DocumentToDtoConverter<ResourceDocument, ResourceDto> resourceConverter;

    @Mock
    private IChallengeService challengeService;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ResourceService resourceService;

    private ResourceDto resourceDto;

    @BeforeEach
    void setUp() {
        resourceDto = ResourceDto.builder()
                .resourceId(UUID.randomUUID())
                .title("Test Resource")
                .description("This is a test resource")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(Collections.emptyList())
                .associationType(AssociationType.NONE)
                .build();
    }

    @Test
    void createResource_SuccessfullyCreatesResource() {
        ResourceDocument resourceDocument = ResourceDocument.builder()
                .resourceId(resourceDto.getResourceId())
                .title(resourceDto.getTitle())
                .description(resourceDto.getDescription())
                .url(resourceDto.getUrl())
                .topic(resourceDto.getTopic())
                .contentType(resourceDto.getContentType())
                .challengeIds(resourceDto.getChallengeIds())
                .build();

        when(resourceConverter.convertDtoToDocument(any(ResourceDto.class), eq(ResourceDocument.class)))
                .thenReturn(resourceDocument);

        when(resourceRepository.save(any(ResourceDocument.class)))
                .thenReturn(Mono.just(resourceDocument));

        when(resourceConverter.convertDocumentToDto(any(ResourceDocument.class), eq(ResourceDto.class)))
                .thenReturn(resourceDto);

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectNextMatches(savedResource ->
                        savedResource.getResourceId().equals(resourceDto.getResourceId()) &&
                                savedResource.getTitle().equals(resourceDto.getTitle()) &&
                                savedResource.getDescription().equals(resourceDto.getDescription()) &&
                                savedResource.getUrl().equals(resourceDto.getUrl()) &&
                                savedResource.getTopic().equals(resourceDto.getTopic()) &&
                                savedResource.getContentType().equals(resourceDto.getContentType()) &&
                                savedResource.getChallengeIds().equals(resourceDto.getChallengeIds())
                )
                .verifyComplete();

        verify(resourceConverter, times(1)).convertDtoToDocument(any(ResourceDto.class), eq(ResourceDocument.class));
        verify(resourceRepository, times(1)).save(any(ResourceDocument.class));
        verify(resourceConverter, times(1)).convertDocumentToDto(any(ResourceDocument.class), eq(ResourceDto.class));
    }


    @Test
    void createResource_FailsWhenContentTypeIsNull() {
        resourceDto.setContentType(null);

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(resourceRepository);
    }

    @Test
    void createResource_FailsWhenTopicIsNull() {
        resourceDto.setTopic(null);

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(resourceRepository);
    }

    @Test
    void createResource_ShouldFail_WhenConverterReturnsNull() {
        when(resourceConverter.convertDtoToDocument(any(ResourceDto.class), eq(ResourceDocument.class)))
                .thenReturn(null);

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();

        verify(resourceConverter, times(1)).convertDtoToDocument(any(ResourceDto.class), eq(ResourceDocument.class));
        verifyNoInteractions(resourceRepository);
    }

}
