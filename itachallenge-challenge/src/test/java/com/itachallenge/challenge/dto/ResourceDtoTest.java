package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

class ResourceDtoTest {

    @Test
    void rightSerializationTest() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("json/ResourceSerialized.json");

        Assertions.assertNotNull(is, "El fitxer JSON no s'ha trobat!");

        String jsonContent = new BufferedReader(new InputStreamReader(is))
                .lines()
                .collect(Collectors.joining("\n"));

        System.out.println("JSON carregat correctament:\n" + jsonContent);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        ResourceDto resource = objectMapper.readValue(jsonContent, ResourceDto.class);

        Assertions.assertNotNull(resource, "L'objecte ResourceDto no ha estat deserialitzat correctament!");

        Assertions.assertEquals("123e4567-e89b-12d3-a456-426614174000", resource.getResourceId().toString());
        Assertions.assertEquals("DEBUGGING FOR THE FIRST TIME", resource.getTitle());
        Assertions.assertEquals("A guide on how to start debugging", resource.getDescription());
        Assertions.assertEquals("https://youtubetutorial.com/debugging", resource.getUrl());

        Assertions.assertEquals(Topic.DEBUGGING, resource.getTopic(), "El topic no és correcte");

        Assertions.assertEquals("TUTORIAL", resource.getContentType());

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
        "contentType": "TUTORIAL",
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
        Assertions.assertEquals("TUTORIAL", resource.getContentType());

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

}

