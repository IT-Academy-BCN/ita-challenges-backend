package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceDocumentToDtoConverterTest {
    private final DocumentToDtoConverter<ResourceDocument, ResourceDto> converter = new DocumentToDtoConverter<>();

    @Test
    void convertDtoToDocumentTest() {
        UUID resourceId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        List<UUID> challengeIds = List.of(
                UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        );

        ResourceDto dto = ResourceDto.builder()
                .resourceId(resourceId)
                .title("DEBUGGING FOR THE FIRST TIME")
                .description("A guide on how to start debugging")
                .url("https://youtubetutorial.com/debugging")
                .topic(Topic.COMPONENTS)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(challengeIds)
                .build();

        ResourceDocument document = converter.convertDtoToDocument(dto, ResourceDocument.class);

        assertNotNull(document);
        assertEquals(dto.getResourceId(), document.getResourceId());
        assertEquals(dto.getTitle(), document.getTitle());
        assertEquals(dto.getDescription(), document.getDescription());
        assertEquals(dto.getUrl(), document.getUrl());
        assertEquals(dto.getTopic(), document.getTopic());
        assertEquals(dto.getContentType(), document.getContentType());
        assertEquals(dto.getChallengeIds(), document.getChallengeIds());
    }

    @Test
    void convertDocumentToDtoTest() {
        UUID resourceId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        List<UUID> challengeIds = List.of(
                UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        );

        ResourceDocument document = ResourceDocument.builder()
                .resourceId(resourceId)
                .title("DEBUGGING FOR THE FIRST TIME")
                .description("A guide on how to start debugging")
                .url("https://youtubetutorial.com/debugging")
                .topic(Topic.COMPONENTS)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(challengeIds)
                .build();

        ResourceDto dto = converter.convertDocumentToDto(document, ResourceDto.class);

        assertNotNull(dto);
        assertEquals(document.getResourceId(), dto.getResourceId());
        assertEquals(document.getTitle(), dto.getTitle());
        assertEquals(document.getDescription(), dto.getDescription());
        assertEquals(document.getUrl(), dto.getUrl());
        assertEquals(document.getTopic(), dto.getTopic());
        assertEquals(document.getContentType(), dto.getContentType());
        assertEquals(document.getChallengeIds(), dto.getChallengeIds());
    }

    @Test
    void convertDocumentFluxToDtoFluxTest() {
        UUID resourceId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        List<UUID> challengeIds = List.of(
                UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        );

        ResourceDocument document1 = ResourceDocument.builder()
                .resourceId(resourceId)
                .title("DEBUGGING FOR THE FIRST TIME")
                .description("A guide on how to start debugging")
                .url("https://youtubetutorial.com/debugging")
                .topic(Topic.COMPONENTS)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(challengeIds)
                .build();

        ResourceDocument document2 = ResourceDocument.builder()
                .resourceId(UUID.randomUUID())
                .title("ANOTHER TITLE")
                .description("A different guide")
                .url("https://anotherurl.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(challengeIds)
                .build();

        Flux<ResourceDocument> documentFlux = Flux.just(document1, document2);
        Flux<ResourceDto> dtoFlux = converter.convertDocumentFluxToDtoFlux(documentFlux, ResourceDto.class);

        assertNotNull(dtoFlux);
        assertEquals(2, dtoFlux.collectList().block().size());
    }

    @Test
    void convertDtoToDocument_WhenNullValues_ReturnsCorrectDocument() {

        ResourceDto dto = ResourceDto.builder()
                .resourceId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .title(null)
                .description(null)
                .url(null)
                .topic(null)
                .contentType(null)
                .challengeIds(null)
                .build();


        ResourceDocument document = converter.convertDtoToDocument(dto, ResourceDocument.class);


        assertNotNull(document);
        assertEquals(dto.getResourceId(), document.getResourceId());
        assertNull(document.getTitle());
        assertNull(document.getDescription());
        assertNull(document.getUrl());
        assertNull(document.getTopic());
        assertNull(document.getContentType());
        assertNull(document.getChallengeIds());
    }

    @Test
    void convertDocumentToDto_WhenNullValues_ReturnsCorrectDto() {
        ResourceDocument document = ResourceDocument.builder()
                .resourceId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .title(null)
                .description(null)
                .url(null)
                .topic(null)
                .contentType(null)
                .challengeIds(null)
                .build();

        ResourceDto dto = converter.convertDocumentToDto(document, ResourceDto.class);

        assertNotNull(dto);
        assertEquals(document.getResourceId(), dto.getResourceId());
        assertNull(dto.getTitle());
        assertNull(dto.getDescription());
        assertNull(dto.getUrl());
        assertNull(dto.getTopic());
        assertNull(dto.getContentType());
        assertNull(dto.getChallengeIds());
    }

    @Test
    void convertDocumentFluxToDtoFlux_WhenMultipleDocuments_ReturnsCorrectDtoFlux() {
        ResourceDocument document1 = ResourceDocument.builder()
                .resourceId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .title("Debugging 101")
                .description("An introductory guide to debugging")
                .url("https://example.com/debugging101")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of(UUID.randomUUID()))
                .build();

        ResourceDocument document2 = ResourceDocument.builder()
                .resourceId(UUID.fromString("789e4567-e89b-12d3-a456-426614174111"))
                .title("Advanced Debugging")
                .description("A deep dive into debugging")
                .url("https://example.com/advanceddebugging")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .build();

        Flux<ResourceDocument> documentFlux = Flux.just(document1, document2);


        Flux<ResourceDto> dtoFlux = converter.convertDocumentFluxToDtoFlux(documentFlux, ResourceDto.class);

        List<ResourceDto> dtoList = dtoFlux.collectList().block();
        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals(document1.getResourceId(), dtoList.get(0).getResourceId());
        assertEquals(document2.getResourceId(), dtoList.get(1).getResourceId());
    }
    @Test
    void convertDocumentToDto_WhenChallengeIdsEmpty_ReturnsCorrectDto() {

        ResourceDocument document = ResourceDocument.builder()
                .resourceId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .title("Debugging Guide")
                .description("A complete guide to debugging")
                .url("https://debuggingguide.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of())
                .build();

        ResourceDto dto = converter.convertDocumentToDto(document, ResourceDto.class);

        assertNotNull(dto);
        assertEquals(document.getResourceId(), dto.getResourceId());
        assertEquals(document.getTitle(), dto.getTitle());
        assertEquals(document.getDescription(), dto.getDescription());
        assertEquals(document.getUrl(), dto.getUrl());
        assertEquals(document.getTopic(), dto.getTopic());
        assertEquals(document.getContentType(), dto.getContentType());
        assertTrue(dto.getChallengeIds().isEmpty());
    }

    @Test
    void convertDtoToDocument_WhenChallengeIdsEmpty_ReturnsCorrectDocument() {
        ResourceDto dto = ResourceDto.builder()
                .resourceId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .title("Debugging Guide")
                .description("A complete guide to debugging")
                .url("https://debuggingguide.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of())
                .build();

        ResourceDocument document = converter.convertDtoToDocument(dto, ResourceDocument.class);

        assertNotNull(document);
        assertEquals(dto.getResourceId(), document.getResourceId());
        assertEquals(dto.getTitle(), document.getTitle());
        assertEquals(dto.getDescription(), document.getDescription());
        assertEquals(dto.getUrl(), document.getUrl());
        assertEquals(dto.getTopic(), document.getTopic());
        assertEquals(dto.getContentType(), document.getContentType());
        assertTrue(document.getChallengeIds().isEmpty());
    }

}

