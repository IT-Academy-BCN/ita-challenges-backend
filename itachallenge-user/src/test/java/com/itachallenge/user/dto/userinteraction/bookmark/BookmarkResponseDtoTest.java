package com.itachallenge.user.dto.userinteraction.bookmark;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookmarkResponseDtoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601

    @Test
    void builder_creaDtoConValoresCorrectos() {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID challengeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);

        BookmarkResponseDto.BookmarkResponseDtoBuilder builder = BookmarkResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt);

        assertNotNull(builder.toString());

        BookmarkResponseDto dto = builder.build();

        assertEquals(uuid, dto.getUuid());
        assertEquals(userId, dto.getUserId());
        assertEquals(challengeId, dto.getChallengeId());
        assertEquals(createdAt, dto.getCreatedAt());
    }

    @Test
    void jsonContract_serializaYDeserializa() throws Exception {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID challengeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);

        BookmarkResponseDto dto = BookmarkResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        String json = MAPPER.writeValueAsString(dto);
        JsonNode root = MAPPER.readTree(json);

        assertEquals(uuid.toString(),        root.get("uuid_bookmark").asText());
        assertEquals(userId.toString(),      root.get("user_id").asText());
        assertEquals(challengeId.toString(), root.get("challenge_id").asText());
        // comparar como LocalDateTime para evitar problemas de formato (segundos)
        assertEquals(createdAt, LocalDateTime.parse(root.get("created_at").asText()));

        // ida y vuelta
        BookmarkResponseDto back = MAPPER.readValue(json, BookmarkResponseDto.class);
        assertEquals(uuid, back.getUuid());
        assertEquals(userId, back.getUserId());
        assertEquals(challengeId, back.getChallengeId());
        assertEquals(createdAt, back.getCreatedAt());
    }
}