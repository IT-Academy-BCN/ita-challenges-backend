package com.itachallenge.challenge.document;

import com.itachallenge.challenge.enums.AssociationType;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.Assert.assertNotNull;
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
        assertTrue(resource.getChallengeIds().isEmpty(), "ChallengeIds should be empty.");
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

    @Test
    void testResourceDocumentWithNonNullValues() {
        UUID resourceId = UUID.randomUUID();
        String title = "Test Title";
        String description = "Test Description";
        String url = "https://test.com";
        Topic topic = Topic.DEBUGGING;
        ResourceContentType contentType = ResourceContentType.VIDEO;
        List<UUID> challengeIds = List.of(UUID.randomUUID());
        AssociationType associationType = AssociationType.ALLSAMETOPIC;

        ResourceDocument resource = ResourceDocument.builder()
                .resourceId(resourceId)
                .title(title)
                .description(description)
                .url(url)
                .topic(topic)
                .contentType(contentType)
                .challengeIds(challengeIds)
                .associationType(associationType)
                .build();

        assertEquals(resourceId, resource.getResourceId());
        assertEquals(title, resource.getTitle());
        assertEquals(description, resource.getDescription());
        assertEquals(url, resource.getUrl());
        assertEquals(topic, resource.getTopic());
        assertEquals(contentType, resource.getContentType());
        assertEquals(challengeIds, resource.getChallengeIds());
        assertEquals(associationType, resource.getAssociationType());
    }

    @Test
    void testModifyResourceDocument() {
        ResourceDocument resource = ResourceDocument.builder()
                .resourceId(UUID.randomUUID())
                .title("Initial Title")
                .description("Initial Description")
                .url("https://initial.com")
                .build();

        resource.setTitle("Updated Title");
        resource.setDescription("Updated Description");

        assertEquals("Updated Title", resource.getTitle());
        assertEquals("Updated Description", resource.getDescription());
    }

    @Test
    void testResourceDocumentWithEmptyLists() {
        ResourceDocument resource = ResourceDocument.builder()
                .resourceId(UUID.randomUUID())
                .title("Title")
                .description("Description")
                .url("https://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .challengeIds(Collections.emptyList())
                .associationType(AssociationType.NONE)
                .build();

        assertNotNull(resource);
        assertTrue(resource.getChallengeIds().isEmpty(), "ChallengeIds should be empty.");
    }

    @Test
    void testDefaultValues() {
        ResourceDocument resource = new ResourceDocument();
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
    void testResourceDocumentWithNullTopic() {
        ResourceDocument resource = ResourceDocument.builder()
                .resourceId(UUID.randomUUID())
                .title("Title")
                .description("Description")
                .url("https://example.com")
                .topic(null)
                .contentType(ResourceContentType.BLOG)
                .build();

        assertNull(resource.getTopic());
    }




}

