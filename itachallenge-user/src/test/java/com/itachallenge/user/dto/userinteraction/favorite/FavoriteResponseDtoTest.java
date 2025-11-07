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
    private FavoriteResponseDto dto1;
    private FavoriteResponseDto dto2;
    private FavoriteResponseDto dto3;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        userId = UUID.randomUUID();
        challengeId = UUID.randomUUID();
        createdAt = LocalDateTime.now();

        dto1 = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        dto2 = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        dto3 = FavoriteResponseDto.builder()
                .uuid(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .challengeId(UUID.randomUUID())
                .createdAt(createdAt.plusDays(1))
                .build();
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

    @Test
    void equals_test() {
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1).isNotEqualTo(null);
        assertThat(dto1).isNotEqualTo("string");
    }

    @Test
    void hashCode_test() {
        assertThat(dto1).hasSameHashCodeAs(dto2);
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    }

    @Test
    void toString_test() {
        String toStringResult = dto1.toString();

        assertThat(toStringResult)
                .isNotNull()
                .isNotEmpty()
                .contains(uuid.toString())
                .contains(userId.toString())
                .contains(challengeId.toString());
    }

    @Test
    void noArgsConstructor_test() {
        FavoriteResponseDto dto = new FavoriteResponseDto();

        assertThat(dto).isNotNull();
        assertThat(dto.getUuid()).isNull();
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getChallengeId()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    void allArgsConstructor_test() {
        FavoriteResponseDto dto = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);

        assertThat(dto.getUuid()).isEqualTo(uuid);
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getChallengeId()).isEqualTo(challengeId);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void builder_PartialFields_test() {
        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .userId(userId)
                .challengeId(challengeId)
                .build();

        assertThat(dto.getUuid()).isNull();
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getChallengeId()).isEqualTo(challengeId);
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    void toString_WithNullFields_test() {
        FavoriteResponseDto dto = new FavoriteResponseDto();
        dto.setUserId(userId);

        String str = dto.toString();
        assertThat(str).isNotNull()
                .isNotEmpty()
                .contains("userId=" + userId)
                .contains("uuid=")
                .contains("challengeId=")
                .contains("createdAt=");
    }

    @Test
    void equals_NullAndDifferentType_test() {
        assertThat(dto1).isNotEqualTo(null);
        assertThat(dto1).isNotEqualTo("some string");

        FavoriteResponseDto other = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(null)
                .createdAt(createdAt)
                .build();

        assertThat(dto1).isNotEqualTo(other);
        assertThat(dto1.hashCode()).isNotEqualTo(other.hashCode());
    }

    @Test
    void hashCode_Consistency_test() {
        int hash1 = dto1.hashCode();
        int hash2 = dto1.hashCode();
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void serialization_deserialization_with_nulls_test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .uuid(null)
                .userId(null)
                .challengeId(null)
                .createdAt(null)
                .build();

        String json = mapper.writeValueAsString(dto);
        assertThat(json).contains("uuid_favorite").contains("user_id").contains("challenge_id").contains("created_at");

        FavoriteResponseDto deserialized = mapper.readValue(json, FavoriteResponseDto.class);
        assertThat(deserialized.getUuid()).isNull();
        assertThat(deserialized.getUserId()).isNull();
        assertThat(deserialized.getChallengeId()).isNull();
        assertThat(deserialized.getCreatedAt()).isNull();
    }




}
