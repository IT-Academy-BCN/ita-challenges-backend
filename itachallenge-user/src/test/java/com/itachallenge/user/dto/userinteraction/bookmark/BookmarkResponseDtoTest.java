package com.itachallenge.user.dto.userinteraction.bookmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkResponseDtoTest {
    private final UUID testUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private final UUID testChallengeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    private final LocalDateTime testDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);


    @Test
    void testGettersAndSetters() {
        BookmarkResponseDto bookmarkResponseDto = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(testUserId)
                .challengeId(testChallengeId)
                .createdAt(testDateTime)
                .build();

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
    void fullCoverageTest() {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID challengeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);

        // no-args const + setters
        BookmarkResponseDto dto = new BookmarkResponseDto();
        dto.setUuid(uuid);
        dto.setUserId(userId);
        dto.setChallengeId(challengeId);
        dto.setCreatedAt(createdAt);

        assertEquals(uuid, dto.getUuid());
        assertEquals(userId, dto.getUserId());
        assertEquals(challengeId, dto.getChallengeId());
        assertEquals(createdAt, dto.getCreatedAt());

        // all-args const
        BookmarkResponseDto dtoAllArgs = new BookmarkResponseDto(uuid, userId, challengeId, createdAt);
        assertEquals(uuid, dtoAllArgs.getUuid());
        assertEquals(userId, dtoAllArgs.getUserId());
        assertEquals(challengeId, dtoAllArgs.getChallengeId());
        assertEquals(createdAt, dtoAllArgs.getCreatedAt());

        // @JsonProperty
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

        assertNotNull(dto.toString());
        assertNotNull(dtoAllArgs.toString());
    }
}
