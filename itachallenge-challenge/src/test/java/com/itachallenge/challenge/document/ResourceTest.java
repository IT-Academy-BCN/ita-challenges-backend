package com.itachallenge.challenge.document;

import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceTest {

    // utilitzo el builder per no haver de posar tots els valors nulls
    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        ResourceDocument resource = ResourceDocument.builder()
                .resourceId(uuid)
                .build();
        assertEquals(uuid, resource.getResourceId());
    }

    @Test
    void getTitle() {
        String title = "Sample Title";
        ResourceDocument resource = ResourceDocument.builder()
                .title(title)
                .build();
        assertEquals(title, resource.getTitle());
    }

    @Test
    void getDescription() {
        String description = "Sample Description";
        ResourceDocument resource = ResourceDocument.builder()
                .description(description)
                .build();
        assertEquals(description, resource.getDescription());
    }

    @Test
    void getUrl() {
        String url = "https://example.com";
        ResourceDocument resource = ResourceDocument.builder()
                .url(url)
                .build();
        assertEquals(url, resource.getUrl());
    }

    @Test
    void getTopic() {
        Topic topic = Topic.DEBUGGING;
        ResourceDocument resource = ResourceDocument.builder()
                .topic(topic)
                .build();
        assertEquals(topic, resource.getTopic());
    }

    @Test
    void getContentType() {
        ResourceContentType contentType = ResourceContentType.BLOG;
        ResourceDocument resource = ResourceDocument.builder()
                .contentType(contentType)
                .build();
        assertEquals(contentType, resource.getContentType());
    }

    @Test
    void getChallengeIds() {
        List<UUID> challengeIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        ResourceDocument resource = ResourceDocument.builder()
                .challengeIds(challengeIds)
                .build();
        assertEquals(challengeIds, resource.getChallengeIds());
    }

    @Test
    void testEmptyResourceDocument() {
        ResourceDocument resource = ResourceDocument.builder().build();

        assertNull(resource.getResourceId());
        assertNull(resource.getTitle());
        assertNull(resource.getDescription());
        assertNull(resource.getUrl());
        assertNull(resource.getTopic());
        assertNull(resource.getContentType());
        assertNull(resource.getChallengeIds());
        assertNull(resource.getAssociationType());
    }

    @Test
    void testEmptyChallengeIds() {
        ResourceDocument resource = ResourceDocument.builder()
                .challengeIds(List.of())
                .build();
        assertTrue(resource.getChallengeIds().isEmpty(), "Els challengeIds haurien de ser buits.");
    }

    @Test
    void testNullFields() {
        ResourceDocument resource = ResourceDocument.builder()
                .resourceId(null)
                .title(null)
                .description(null)
                .url(null)
                .topic(null)
                .contentType(null)
                .challengeIds(null)
                .associationType(null)
                .build();

        assertNull(resource.getResourceId());
        assertNull(resource.getTitle());
        assertNull(resource.getDescription());
        assertNull(resource.getUrl());
        assertNull(resource.getTopic());
        assertNull(resource.getContentType());
        assertNull(resource.getChallengeIds());
        assertNull(resource.getAssociationType());
    }
}

