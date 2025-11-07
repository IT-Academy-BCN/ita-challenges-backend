package com.itachallenge.user.dto.userinteraction.favorite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class FavoriteResponseDtoTest {
    private UUID uuid;
    private UUID userId;
    private UUID challengeId;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        userId = UUID.randomUUID();
        challengeId = UUID.randomUUID();
        createdAt = LocalDateTime.now();
    }

    @Test
    void settersAndGetters_test(){
        FavoriteResponseDto dto = new FavoriteResponseDto();

        dto.setUuid(uuid);
        dto.setUserId(userId);
        dto.setChallengeId(challengeId);
        dto.setCreatedAt(createdAt);

        assertThat(dto.getUuid()).isEqualTo(uuid);
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getChallengeId()).isEqualTo(challengeId);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void builder_test(){
        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertThat(dto.getUuid()).isEqualTo(uuid);
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getChallengeId()).isEqualTo(challengeId);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void  jsonSerialization_test() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // enable LocalDatetime handling

        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("uuid_favorite")
                        .contains("user_id")
                        .contains("challenge_id")
                        .contains("created_at");

        FavoriteResponseDto deserialized = mapper.readValue(json, FavoriteResponseDto.class);

        assertThat(deserialized).isEqualTo(dto);

    }


}
