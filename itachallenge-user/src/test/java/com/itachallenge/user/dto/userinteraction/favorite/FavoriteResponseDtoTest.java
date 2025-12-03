package com.itachallenge.user.dto.userinteraction.favorite;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FavoriteResponseDtoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601

    @Test
    void builder_creaDtoConValoresCorrectos() {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID challengeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);

        FavoriteResponseDto.FavoriteResponseDtoBuilder builder = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt);

        assertNotNull(builder.toString());

        FavoriteResponseDto dto = builder.build();

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

        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        String json = MAPPER.writeValueAsString(dto);
        JsonNode root = MAPPER.readTree(json);

        assertEquals(uuid.toString(),        root.get("uuid_favorite").asText());
        assertEquals(userId.toString(),      root.get("user_id").asText());
        assertEquals(challengeId.toString(), root.get("challenge_id").asText());
        // comparar como LocalDateTime para evitar problemas de formato (segundos)
        assertEquals(createdAt, LocalDateTime.parse(root.get("created_at").asText()));

        // ida y vuelta
        FavoriteResponseDto back = MAPPER.readValue(json, FavoriteResponseDto.class);
        assertEquals(uuid, back.getUuid());
        assertEquals(userId, back.getUserId());
        assertEquals(challengeId, back.getChallengeId());
        assertEquals(createdAt, back.getCreatedAt());
    }

}
