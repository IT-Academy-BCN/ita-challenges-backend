package com.itachallenge.user.dto.userinteraction.bookmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkResponseDtoTest {

    private BookmarkResponseDto bookmarkResponseDto;
    private final UUID testUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private final UUID testChallengeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    private final LocalDateTime testDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);

    @BeforeEach
    void setUp() {
        bookmarkResponseDto = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(testUserId)
                .challengeId(testChallengeId)
                .createdAt(testDateTime)
                .build();
    }

    @Test
    void testGettersAndSetters() {
        UUID newUuid = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();
        UUID newChallengeId = UUID.randomUUID();
        LocalDateTime newDateTime = LocalDateTime.now();

        bookmarkResponseDto.setUuid(newUuid);
        bookmarkResponseDto.setUserId(newUserId);
        bookmarkResponseDto.setChallengeId(newChallengeId);
        bookmarkResponseDto.setCreatedAt(newDateTime);

        assertEquals(newUuid, bookmarkResponseDto.getUuid());
        assertEquals(newUserId, bookmarkResponseDto.getUserId());
        assertEquals(newChallengeId, bookmarkResponseDto.getChallengeId());
        assertEquals(newDateTime, bookmarkResponseDto.getCreatedAt());
    }

   
   
    @Test
    void testJsonProperties() {
        assertEquals(testUuid, bookmarkResponseDto.getUuid());
        assertEquals(testUserId, bookmarkResponseDto.getUserId());
        assertEquals(testChallengeId, bookmarkResponseDto.getChallengeId());
        assertEquals(testDateTime, bookmarkResponseDto.getCreatedAt());
        
        try {
            java.lang.reflect.Field uuidField = BookmarkResponseDto.class.getDeclaredField("uuid");
            com.fasterxml.jackson.annotation.JsonProperty uuidAnnotation = uuidField.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            assertEquals("uuid_bookmark", uuidAnnotation.value());
            
            java.lang.reflect.Field userIdField = BookmarkResponseDto.class.getDeclaredField("userId");
            com.fasterxml.jackson.annotation.JsonProperty userIdAnnotation = userIdField.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            assertEquals("user_id", userIdAnnotation.value());
            
            java.lang.reflect.Field challengeIdField = BookmarkResponseDto.class.getDeclaredField("challengeId");
            com.fasterxml.jackson.annotation.JsonProperty challengeIdAnnotation = challengeIdField.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            assertEquals("challenge_id", challengeIdAnnotation.value());
            
            java.lang.reflect.Field createdAtField = BookmarkResponseDto.class.getDeclaredField("createdAt");
            com.fasterxml.jackson.annotation.JsonProperty createdAtAnnotation = createdAtField.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            assertEquals("created_at", createdAtAnnotation.value());
        } catch (NoSuchFieldException e) {
            fail("Field not found: " + e.getMessage());
        }
    }
    @Test
    void fullCoverageTest() {
        // valeurs initiales
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID challengeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);

        // constructeur no-args + setters
        BookmarkResponseDto dto = new BookmarkResponseDto();
        dto.setUuid(uuid);
        dto.setUserId(userId);
        dto.setChallengeId(challengeId);
        dto.setCreatedAt(createdAt);

        assertEquals(uuid, dto.getUuid());
        assertEquals(userId, dto.getUserId());
        assertEquals(challengeId, dto.getChallengeId());
        assertEquals(createdAt, dto.getCreatedAt());

        // constructeur all-args
        BookmarkResponseDto dtoAllArgs = new BookmarkResponseDto(uuid, userId, challengeId, createdAt);
        assertEquals(uuid, dtoAllArgs.getUuid());
        assertEquals(userId, dtoAllArgs.getUserId());
        assertEquals(challengeId, dtoAllArgs.getChallengeId());
        assertEquals(createdAt, dtoAllArgs.getCreatedAt());

        // test annotations @JsonProperty
        try {
            assertEquals("uuid_bookmark", BookmarkResponseDto.class.getDeclaredField("uuid")
                    .getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class).value());
            assertEquals("user_id", BookmarkResponseDto.class.getDeclaredField("userId")
                    .getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class).value());
            assertEquals("challenge_id", BookmarkResponseDto.class.getDeclaredField("challengeId")
                    .getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class).value());
            assertEquals("created_at", BookmarkResponseDto.class.getDeclaredField("createdAt")
                    .getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class).value());
        } catch (NoSuchFieldException e) {
            fail("Field not found: " + e.getMessage());
        }

        // test toString basique
        assertNotNull(dto.toString());
        assertNotNull(dtoAllArgs.toString());
    }
}
