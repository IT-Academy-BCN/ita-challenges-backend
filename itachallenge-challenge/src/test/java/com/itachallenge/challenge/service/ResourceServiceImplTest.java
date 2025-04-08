package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.ChallengeListDto;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.enums.AssociationType;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private DocumentToDtoConverter<ResourceDocument, ResourceDto> resourceConverter;

    @Mock
    private IChallengeService challengeService;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test //
    void createResource_WithValidData_ResourceCreated() {
        UUID resourceId = UUID.randomUUID();
        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .associationType(AssociationType.NONE)
                .challengeIds(Collections.emptyList())
                .build();

        ResourceDocument resourceDocument = new ResourceDocument(resourceId, "Title", "Description", "http://example.com", Topic.DEBUGGING, ResourceContentType.VIDEO, Collections.emptyList(), AssociationType.NONE);

        when(resourceConverter.convertDtoToDocument(any(), eq(ResourceDocument.class))).thenReturn(resourceDocument);
        when(resourceRepository.save(any(ResourceDocument.class))).thenReturn(Mono.just(resourceDocument));
        when(resourceConverter.convertDocumentToDto(any(), eq(ResourceDto.class))).thenReturn(resourceDto);

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectNext(resourceDto)
                .verifyComplete();

        verify(resourceRepository).save(resourceDocument);
        verify(resourceConverter).convertDtoToDocument(any(), eq(ResourceDocument.class));
        verify(resourceConverter).convertDocumentToDto(any(), eq(ResourceDto.class));
    }

    @Test
    void createResource_WithMissingContentType_ShouldThrowError() {
        UUID resourceId = UUID.randomUUID();
        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(null)
                .associationType(AssociationType.NONE)
                .challengeIds(Collections.emptyList())
                .build();

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void createResource_WithnullTopicType_ShouldThrowError() {
        UUID resourceId = UUID.randomUUID();
        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(null)
                .contentType(ResourceContentType.COURSE)
                .associationType(AssociationType.NONE)
                .challengeIds(Collections.emptyList())
                .build();

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test //
    void createResource_WithAssociationTypeChoose_ShouldReturnUpdatedResource() {
        UUID resourceId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .challengeIds(Collections.emptyList())
                .associationType(AssociationType.ALLSAMETOPIC)
                .build();

        ChallengeDto challengeDto = ChallengeDto.builder()
                .challengeId(challengeId)
                .title("Challenge")
                .build();

        ChallengeListDto challengeListDto = new ChallengeListDto(List.of(challengeDto), 1);

        ResourceDocument resourceDocument = new ResourceDocument(
                resourceId, "Title", "Description", "http://example.com",
                Topic.DEBUGGING, ResourceContentType.VIDEO,
                Collections.emptyList(), AssociationType.ALLSAMETOPIC
        );

        when(challengeService.getChallengesByTopic(Topic.DEBUGGING, 0, -1)).thenReturn(Mono.just(challengeListDto));
        when(resourceConverter.convertDtoToDocument(any(), eq(ResourceDocument.class))).thenReturn(resourceDocument);
        when(resourceRepository.save(any(ResourceDocument.class))).thenReturn(Mono.just(resourceDocument));
        when(resourceConverter.convertDocumentToDto(any(), eq(ResourceDto.class))).thenReturn(resourceDto);

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectNextMatches(updatedResource -> updatedResource.getChallengeIds().contains(challengeId))
                .verifyComplete();

        verify(challengeService).getChallengesByTopic(Topic.DEBUGGING, 0, -1);
        verify(resourceConverter, atMost(2)).convertDtoToDocument(any(), eq(ResourceDocument.class));
        verify(resourceRepository, atMost(2)).save(any(ResourceDocument.class));
        verify(resourceConverter).convertDocumentToDto(any(), eq(ResourceDto.class));
    }


    @Test //
    void createResource_WithAssociationTypeChoose_NoChallengesFound_ShouldThrowError() {
        UUID resourceId = UUID.randomUUID();

        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .associationType(AssociationType.CHOOSE)
                .challengeIds(Collections.emptyList())
                .build();

        ChallengeListDto emptyChallengeList = new ChallengeListDto(Collections.emptyList(), 0);

        when(challengeService.getChallengesByTopic(Topic.DEBUGGING, 0, -1))
                .thenReturn(Mono.just(emptyChallengeList));

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("No challenges found for the selected topic"))
                .verify();

        verify(challengeService).getChallengesByTopic(Topic.DEBUGGING, 0, -1);

        verifyNoInteractions(resourceConverter);
        verifyNoInteractions(resourceRepository);
    }

    @Test
    void createResource_WithAssociationTypeNone_ShouldSaveResource() {
        UUID resourceId = UUID.randomUUID();

        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .associationType(AssociationType.NONE)
                .challengeIds(Collections.emptyList())
                .build();

        ResourceDocument resourceDocument = new ResourceDocument(resourceId, "Title", "Description", "http://example.com", Topic.DEBUGGING, ResourceContentType.VIDEO, Collections.emptyList(), AssociationType.NONE);

        when(resourceConverter.convertDtoToDocument(any(), eq(ResourceDocument.class))).thenReturn(resourceDocument);
        when(resourceRepository.save(any(ResourceDocument.class))).thenReturn(Mono.just(resourceDocument));
        when(resourceConverter.convertDocumentToDto(any(), eq(ResourceDto.class))).thenReturn(resourceDto);

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectNext(resourceDto)
                .verifyComplete();

        verify(resourceRepository).save(resourceDocument);
        verify(resourceConverter).convertDtoToDocument(any(), eq(ResourceDocument.class));
        verify(resourceConverter).convertDocumentToDto(any(), eq(ResourceDto.class));
    }


    @Test
    void createResource_WithNullResourceDto_ShouldThrowError() {
        Mono<ResourceDto> result = resourceService.createResource(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void createResource_WithChooseAssociationType_NoChallenges_ShouldNotSave() {
        UUID resourceId = UUID.randomUUID();

        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .associationType(AssociationType.CHOOSE)
                .challengeIds(Collections.emptyList())
                .build();

        ChallengeListDto emptyChallengeList = new ChallengeListDto(Collections.emptyList(), 0);

        when(challengeService.getChallengesByTopic(Topic.DEBUGGING, 0, -1))
                .thenReturn(Mono.just(emptyChallengeList));

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("No challenges found for the selected topic"))
                .verify();

        verifyNoInteractions(resourceRepository);
    }

    @Test
    void createResource_WithAssociationTypeALLSAMETOPIC_ShouldPopulateChallengeIds() {
        UUID resourceId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .associationType(AssociationType.ALLSAMETOPIC)
                .challengeIds(Collections.emptyList())
                .build();

        ChallengeDto challengeDto = ChallengeDto.builder()
                .challengeId(challengeId)
                .title("Challenge")
                .build();

        ChallengeListDto challengeListDto = new ChallengeListDto(List.of(challengeDto), 1);

        ResourceDocument resourceDocument = new ResourceDocument(
                resourceId, "Title", "Description", "http://example.com", Topic.DEBUGGING,
                ResourceContentType.VIDEO, List.of(challengeId), AssociationType.ALLSAMETOPIC
        );

        when(challengeService.getChallengesByTopic(Topic.DEBUGGING, 0, -1))
                .thenReturn(Mono.just(challengeListDto));
        when(resourceConverter.convertDtoToDocument(any(), eq(ResourceDocument.class)))
                .thenReturn(resourceDocument);
        when(resourceRepository.save(any(ResourceDocument.class)))
                .thenReturn(Mono.just(resourceDocument));
        when(resourceConverter.convertDocumentToDto(any(), eq(ResourceDto.class)))
                .thenReturn(resourceDto);

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectNextMatches(updatedResource -> updatedResource.getChallengeIds().contains(challengeId))
                .verifyComplete();

        verify(challengeService).getChallengesByTopic(Topic.DEBUGGING, 0, -1);
    }

    @Test
    void createResource_WithChooseAssociationType_NoChallengesFound_ShouldThrowError() {
        UUID resourceId = UUID.randomUUID();

        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .associationType(AssociationType.CHOOSE)
                .challengeIds(Collections.emptyList())
                .build();

        ChallengeListDto emptyChallengeList = new ChallengeListDto(Collections.emptyList(), 0);

        when(challengeService.getChallengesByTopic(Topic.DEBUGGING, 0, -1))
                .thenReturn(Mono.just(emptyChallengeList));

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("No challenges found for the selected topic"))
                .verify();

        verify(challengeService).getChallengesByTopic(Topic.DEBUGGING, 0, -1);
        verifyNoInteractions(resourceConverter);
        verifyNoInteractions(resourceRepository);
    }

    @Test
    void createResource_WithAssociationTypeALLSAMETOPIC_MultipleChallenges_ShouldPopulateChallengeIds() {
        UUID resourceId = UUID.randomUUID();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();

        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .associationType(AssociationType.ALLSAMETOPIC)
                .challengeIds(Collections.emptyList())
                .build();

        ChallengeDto challengeDto1 = ChallengeDto.builder()
                .challengeId(challengeId1)
                .title("Challenge 1")
                .build();
        ChallengeDto challengeDto2 = ChallengeDto.builder()
                .challengeId(challengeId2)
                .title("Challenge 2")
                .build();

        ChallengeListDto challengeListDto = new ChallengeListDto(List.of(challengeDto1, challengeDto2), 2);

        ResourceDocument resourceDocument = new ResourceDocument(
                resourceId, "Title", "Description", "http://example.com", Topic.DEBUGGING,
                ResourceContentType.VIDEO, List.of(challengeId1, challengeId2), AssociationType.ALLSAMETOPIC
        );

        when(challengeService.getChallengesByTopic(Topic.DEBUGGING, 0, -1))
                .thenReturn(Mono.just(challengeListDto));
        when(resourceConverter.convertDtoToDocument(any(), eq(ResourceDocument.class)))
                .thenReturn(resourceDocument);
        when(resourceRepository.save(any(ResourceDocument.class)))
                .thenReturn(Mono.just(resourceDocument));
        when(resourceConverter.convertDocumentToDto(any(), eq(ResourceDto.class)))
                .thenReturn(resourceDto);

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectNextMatches(updatedResource -> updatedResource.getChallengeIds().contains(challengeId1) &&
                        updatedResource.getChallengeIds().contains(challengeId2))
                .verifyComplete();

        verify(challengeService).getChallengesByTopic(Topic.DEBUGGING, 0, -1);
    }

    @Test
    void createResource_WithFailedConversion_ShouldThrowError() {
        UUID resourceId = UUID.randomUUID();

        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Title")
                .description("Description")
                .url("http://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .associationType(AssociationType.NONE)
                .challengeIds(Collections.emptyList())
                .build();

        when(resourceConverter.convertDtoToDocument(any(), eq(ResourceDocument.class))).thenReturn(null); // Fallida de conversió

        Mono<ResourceDto> result = resourceService.createResource(resourceDto);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof IllegalStateException &&
                        error.getMessage().equals("Conversion DTO to Document null"))
                .verify();
    }


}
