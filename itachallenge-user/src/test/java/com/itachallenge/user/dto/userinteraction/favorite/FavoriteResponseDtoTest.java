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

    @Test
    void equals_WithSameReference_test() {
        FavoriteResponseDto dto = new FavoriteResponseDto();
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    void equals_WithDifferentUuid_test() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(UUID.randomUUID(), userId, challengeId, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(UUID.randomUUID(), userId, challengeId, createdAt);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithDifferentUserId_test() {
        UUID uuid = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, UUID.randomUUID(), challengeId, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, UUID.randomUUID(), challengeId, createdAt);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithDifferentChallengeId_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, userId, UUID.randomUUID(), createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, userId, UUID.randomUUID(), createdAt);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithDifferentCreatedAt_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, userId, challengeId, LocalDateTime.now());
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, userId, challengeId, LocalDateTime.now().plusDays(1));

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithNullUuidInBoth_test() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(null, userId, challengeId, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(null, userId, challengeId, createdAt);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void equals_WithNullUserIdInBoth_test() {
        UUID uuid = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, null, challengeId, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, null, challengeId, createdAt);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void equals_WithNullChallengeIdInBoth_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, userId, null, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, userId, null, createdAt);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void equals_WithNullCreatedAtInBoth_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, userId, challengeId, null);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, userId, challengeId, null);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void equals_WithNullUuidInOne_test() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(null, userId, challengeId, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(UUID.randomUUID(), userId, challengeId, createdAt);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithNullUserIdInOne_test() {
        UUID uuid = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, null, challengeId, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, UUID.randomUUID(), challengeId, createdAt);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithNullChallengeIdInOne_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, userId, null, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, userId, UUID.randomUUID(), createdAt);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithNullCreatedAtInOne_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, userId, challengeId, null);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, userId, challengeId, LocalDateTime.now());

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_AllFieldsNull_test() {
        FavoriteResponseDto dto1 = new FavoriteResponseDto(null, null, null, null);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(null, null, null, null);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void hashCode_WithAllNulls_test() {
        FavoriteResponseDto dto = new FavoriteResponseDto(null, null, null, null);
        int hashCode = dto.hashCode();

        assertThat(hashCode).isNotNull();
    }

    @Test
    void hashCode_EqualObjectsHaveEqualHashCodes_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void canEqual_WithSameClass_test() {
        FavoriteResponseDto dto1 = new FavoriteResponseDto();
        FavoriteResponseDto dto2 = new FavoriteResponseDto();

        assertThat(dto1.canEqual(dto2)).isTrue();
    }

    @Test
    void canEqual_WithDifferentClass_test() {
        FavoriteResponseDto dto = new FavoriteResponseDto();

        assertThat(dto.canEqual(new Object())).isFalse();
    }

    @Test
    void builder_EmptyBuild_test() {
        FavoriteResponseDto dto = FavoriteResponseDto.builder().build();

        assertThat(dto).isNotNull();
        assertThat(dto.getUuid()).isNull();
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getChallengeId()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    void builder_WithOnlyUuid_test() {
        UUID uuid = UUID.randomUUID();
        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .uuid(uuid)
                .build();

        assertThat(dto.getUuid()).isEqualTo(uuid);
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getChallengeId()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    void builder_WithOnlyCreatedAt_test() {
        LocalDateTime createdAt = LocalDateTime.now();
        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .createdAt(createdAt)
                .build();

        assertThat(dto.getUuid()).isNull();
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getChallengeId()).isNull();
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void setters_HandleNullValues_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);

        dto.setUuid(null);
        dto.setUserId(null);
        dto.setChallengeId(null);
        dto.setCreatedAt(null);

        assertThat(dto.getUuid()).isNull();
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getChallengeId()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    void toString_ContainsAllFields_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);
        String toString = dto.toString();

        assertThat(toString)
                .contains("FavoriteResponseDto")
                .contains("uuid=" + uuid)
                .contains("userId=" + userId)
                .contains("challengeId=" + challengeId)
                .contains("createdAt=" + createdAt);
    }

    @Test
    void toString_AllNullFields_test() {
        FavoriteResponseDto dto = new FavoriteResponseDto(null, null, null, null);
        String str = dto.toString();

        assertThat(str)
                .contains("FavoriteResponseDto")
                .contains("uuid=null")
                .contains("userId=null")
                .contains("challengeId=null")
                .contains("createdAt=null");
    }

    @Test
    void jsonSerialization_WithPartialFields_test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .userId(userId)
                .challengeId(challengeId)
                .build();

        String json = mapper.writeValueAsString(dto);
        FavoriteResponseDto deserialized = mapper.readValue(json, FavoriteResponseDto.class);

        assertThat(deserialized.getUuid()).isNull();
        assertThat(deserialized.getUserId()).isEqualTo(userId);
        assertThat(deserialized.getChallengeId()).isEqualTo(challengeId);
        assertThat(deserialized.getCreatedAt()).isNull();
    }

    @Test
    void jsonDeserialization_WithMissingFields_test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String json = "{\"user_id\":\"" + UUID.randomUUID() + "\"}";

        FavoriteResponseDto deserialized = mapper.readValue(json, FavoriteResponseDto.class);

        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getUserId()).isNotNull();
        assertThat(deserialized.getUuid()).isNull();
        assertThat(deserialized.getChallengeId()).isNull();
        assertThat(deserialized.getCreatedAt()).isNull();
    }

    @Test
    void builderThenSetters_Modification_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .build();

        dto.setChallengeId(challengeId);
        dto.setCreatedAt(createdAt);

        assertThat(dto.getUuid()).isEqualTo(uuid);
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getChallengeId()).isEqualTo(challengeId);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void equals_Symmetric_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto2).isEqualTo(dto1);
    }

    @Test
    void equals_Transitive_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteResponseDto dto1 = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);
        FavoriteResponseDto dto3 = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto2).isEqualTo(dto3);
        assertThat(dto1).isEqualTo(dto3);
    }

    @Test
    void hashCode_ConsistentAcrossMultipleCalls_test() {
        UUID uuid = UUID.randomUUID();
        FavoriteResponseDto dto = new FavoriteResponseDto(uuid, UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());

        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        int hash3 = dto.hashCode();

        assertThat(hash1).isEqualTo(hash2).isEqualTo(hash3);
    }

    @Test
    void jsonSerialization_PreservesAllData_test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());


        LocalDateTime createdAtWithNano = LocalDateTime.now().withNano(0);

        FavoriteResponseDto original = new FavoriteResponseDto(uuid, userId, challengeId, createdAtWithNano);

        String json = mapper.writeValueAsString(original);
        FavoriteResponseDto deserialized = mapper.readValue(json, FavoriteResponseDto.class);

        assertThat(deserialized.getUuid()).isEqualTo(original.getUuid());
        assertThat(deserialized.getUserId()).isEqualTo(original.getUserId());
        assertThat(deserialized.getChallengeId()).isEqualTo(original.getChallengeId());

    }



}
