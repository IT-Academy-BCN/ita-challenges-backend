package com.itachallenge.challenge.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;

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
}

