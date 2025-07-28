package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.enums.AssociationType;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import jakarta.validation.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


class ResourceDtoTest {

    @Test
    void rightSerializationTest() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("json/ResourceSerialized.json");

        Assertions.assertNotNull(is, "JSON not found!");

        String jsonContent = new BufferedReader(new InputStreamReader(is))
                .lines()
                .collect(Collectors.joining("\n"));

        System.out.println("JSON correct\n" + jsonContent);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        ResourceDto resource = objectMapper.readValue(jsonContent, ResourceDto.class);

        Assertions.assertNotNull(resource, "ResourceDto nout deserialized correctly!");

        Assertions.assertEquals("123e4567-e89b-12d3-a456-426614174000", resource.getResourceId().toString());
        Assertions.assertEquals("DEBUGGING FOR THE FIRST TIME", resource.getTitle());
        Assertions.assertEquals("A guide on how to start debugging", resource.getDescription());
        Assertions.assertEquals("https://youtubetutorial.com/debugging", resource.getUrl());

        Assertions.assertEquals(Topic.DEBUGGING, resource.getTopic(), "Incorrect topic");

        Assertions.assertEquals(ResourceContentType.BLOG, resource.getContentType());

        Assertions.assertNotNull(resource.getChallengeIds(), "Els IDs de challenge no són vàlids!");
        Assertions.assertEquals(2, resource.getChallengeIds().size(), "El nombre de challengeIds no és el correcte!");

        List<String> challengeIdsAsString = resource.getChallengeIds().stream()
                .map(UUID::toString)
                .collect(Collectors.toList());

        Assertions.assertTrue(challengeIdsAsString.contains("f47ac10b-58cc-4372-a567-0e02b2c3d479"), "El challengeId esperat no es troba!");
        Assertions.assertTrue(challengeIdsAsString.contains("550e8400-e29b-41d4-a716-446655440000"), "El challengeId esperat no es troba!");
    }


    @Test
    void rightDeserializationTest() throws Exception {
        String jsonContent = """
    {
        "resourceId": "123e4567-e89b-12d3-a456-426614174000",
        "title": "DEBUGGING FOR THE FIRST TIME",
        "description": "A guide on how to start debugging",
        "url": "https://youtubetutorial.com/debugging",
        "topic": "DEBUGGING", 
        "contentType": "BLOG",
        "challengeIds": [
            "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            "550e8400-e29b-41d4-a716-446655440000"
        ]
    }
    """;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        ResourceDto resource = objectMapper.readValue(jsonContent, ResourceDto.class);

        Assertions.assertNotNull(resource);
        Assertions.assertEquals("123e4567-e89b-12d3-a456-426614174000", resource.getResourceId().toString());
        Assertions.assertEquals("DEBUGGING FOR THE FIRST TIME", resource.getTitle());
        Assertions.assertEquals("A guide on how to start debugging", resource.getDescription());
        Assertions.assertEquals("https://youtubetutorial.com/debugging", resource.getUrl());
        Assertions.assertEquals(Topic.DEBUGGING, resource.getTopic());
        Assertions.assertEquals(ResourceContentType.BLOG, resource.getContentType());

        Assertions.assertNotNull(resource.getChallengeIds());
        Assertions.assertEquals(2, resource.getChallengeIds().size());
        Assertions.assertTrue(resource.getChallengeIds().contains(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479")));
        Assertions.assertTrue(resource.getChallengeIds().contains(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")));
    }


    @Test
    void builderAndGettersSettersTest() {
        UUID resourceId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        List<UUID> challengeIds = List.of(
                UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        );

        ResourceDto resource = ResourceDto.builder()
                .resourceId(resourceId)
                .title("DEBUGGING FOR THE FIRST TIME")
                .description("A guide on how to start debugging")
                .url("https://youtubetutorial.com/debugging")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(challengeIds)
                .build();

        Assertions.assertEquals(resourceId, resource.getResourceId());
        Assertions.assertEquals("DEBUGGING FOR THE FIRST TIME", resource.getTitle());

        resource.setTitle("UPDATED TITLE");
        Assertions.assertEquals("UPDATED TITLE", resource.getTitle());
    }

    @Test
    void equalsAndHashCodeTest() {
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        ResourceDto resource1 = ResourceDto.builder()
                .resourceId(id)
                .title("DEBUGGING FOR THE FIRST TIME")
                .description("A guide on how to start debugging")
                .url("https://youtubetutorial.com/debugging")
                .topic(Topic.COMPONENTS)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of(
                        UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
                ))
                .build();

        ResourceDto resource2 = ResourceDto.builder()
                .resourceId(id)
                .title("DEBUGGING FOR THE FIRST TIME")
                .description("A guide on how to start debugging")
                .url("https://youtubetutorial.com/debugging")
                .topic(Topic.COMPONENTS)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of(
                        UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
                ))
                .build();

        Assertions.assertEquals(resource1, resource2);
        Assertions.assertEquals(resource1.hashCode(), resource2.hashCode());
    }

    @Test
    void testInvalidResourceDto() {
        ResourceDto invalidResource = ResourceDto.builder()
                .resourceId(null)
                .title("")
                .description(null)
                .url("https://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of(UUID.randomUUID()))
                .associationType(AssociationType.NONE)
                .build();


        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ResourceDto>> violations = validator.validate(invalidResource);

        assertFalse("ResourceDto invalid", violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cannot be empty")));
    }

    @Test
    void testToBuilder() {
        UUID resourceId = UUID.randomUUID();
        ResourceDto resource = ResourceDto.builder()
                .resourceId(resourceId)
                .title("Initial Title")
                .description("Initial Description")
                .url("https://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of(UUID.randomUUID()))
                .associationType(AssociationType.NONE)
                .build();

        ResourceDto modifiedResource = resource.toBuilder()
                .title("Modified Title")
                .build();

        Assertions.assertEquals("Initial Title", resource.getTitle());
        Assertions.assertEquals("Modified Title", modifiedResource.getTitle());
    }

    @Test
    void testSerializationWithNullFields() throws Exception {
        ResourceDto resource = ResourceDto.builder()
                .resourceId(UUID.randomUUID())
                .title("Test Title")
                .description("Test Description")
                .url("https://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of(UUID.randomUUID()))
                .associationType(AssociationType.NONE)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        String jsonContent = objectMapper.writeValueAsString(resource);
        Assertions.assertFalse(jsonContent.contains("\"associationType\":null"), " associationType should not be here");
    }



}

