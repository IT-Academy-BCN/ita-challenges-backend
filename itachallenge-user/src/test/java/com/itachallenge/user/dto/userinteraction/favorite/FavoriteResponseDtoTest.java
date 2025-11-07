package com.itachallenge.user.dto.userinteraction.favorite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FavoriteResponseDtoTest {
    private UUID uuid;
    private UUID userId;
    private UUID challengeId;
    private LocalDateTime createdAt;
    private FavoriteResponseDto dto1;
    private FavoriteResponseDto dto1Clone;
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

        dto1Clone = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        dto2 = FavoriteResponseDto.builder()
                .uuid(UUID.randomUUID())
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
        assertAll(
                () -> assertThat(dto1).isEqualTo(dto1Clone),
                () -> assertThat(dto1).isNotEqualTo(dto3),
                () -> assertThat(dto1).isNotEqualTo(null),
                () -> assertThat(dto1).isNotEqualTo("string")
        );
    }

    @Test
    void hashCode_test() {
        assertThat(dto1).hasSameHashCodeAs(dto1Clone);
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
        FavoriteResponseDto other = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(null)
                .createdAt(createdAt)
                .build();

        assertAll(
                () -> assertThat(dto1).isNotEqualTo(null),
                () -> assertThat(dto1).isNotEqualTo("some string"),
                () -> assertThat(dto1).isNotEqualTo(other),
                () -> assertThat(dto1.hashCode()).isNotEqualTo(other.hashCode())
        );

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
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithDifferentUserId_test() {
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithDifferentChallengeId_test() {
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithDifferentCreatedAt_test() {
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithNullUuidInBoth_test() {
        FavoriteResponseDto dtoA = FavoriteResponseDto.builder()
                .uuid(null)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        FavoriteResponseDto dtoB = FavoriteResponseDto.builder()
                .uuid(null)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertThat(dtoA).isEqualTo(dtoB);
    }

    @Test
    void equals_WithNullUserIdInBoth_test() {
        FavoriteResponseDto dtoA = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(null)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        FavoriteResponseDto dtoB = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(null)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertThat(dtoA).isEqualTo(dtoB);
    }

    @Test
    void equals_WithNullChallengeIdInBoth_test() {
        FavoriteResponseDto dtoA = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(null)
                .createdAt(createdAt)
                .build();

        FavoriteResponseDto dtoB = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(null)
                .createdAt(createdAt)
                .build();

        assertThat(dtoA).isEqualTo(dtoB);
    }

    @Test
    void equals_WithNullCreatedAtInBoth_test() {
        FavoriteResponseDto dtoA = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(null)
                .build();

        FavoriteResponseDto dtoB = FavoriteResponseDto.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(null)
                .build();

        assertThat(dtoA).isEqualTo(dtoB);
    }

    @Test
    void equals_WithNullUuidInOne_test() {
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithNullUserIdInOne_test() {
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithNullChallengeIdInOne_test() {
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_WithNullCreatedAtInOne_test() {
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_AllFieldsNull_test() {
        FavoriteResponseDto dtoA = new FavoriteResponseDto(null, null, null, null);
        FavoriteResponseDto dtoB = new FavoriteResponseDto(null, null, null, null);

        assertThat(dtoA).isEqualTo(dtoB);
    }

    @Test
    void hashCode_WithAllNulls_test() {
        FavoriteResponseDto dtoA = new FavoriteResponseDto(null, null, null, null);
        int hashCode = dtoA.hashCode();

        assertThat(dtoA.hashCode()).isEqualTo(hashCode);
    }

    @Test
    void hashCode_EqualObjectsHaveEqualHashCodes_test() {
        FavoriteResponseDto dtoA = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);
        FavoriteResponseDto dtoB = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);

        assertThat(dtoA).hasSameHashCodeAs(dtoB);
    }

    @Test
    void canEqual_WithSameClass_test() {
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
        FavoriteResponseDto dto = FavoriteResponseDto.builder()
                .uuid(randomUuid)
                .build();

        assertThat(dto.getUuid()).isEqualTo(uuid);
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getChallengeId()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    void builder_WithOnlyCreatedAt_test() {
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
        dto1.setUuid(null);
        dto1.setUserId(null);
        dto1.setChallengeId(null);
        dto1.setCreatedAt(null);

        assertThat(dto1.getUuid()).isNull();
        assertThat(dto1.getUserId()).isNull();
        assertThat(dto1.getChallengeId()).isNull();
        assertThat(dto1.getCreatedAt()).isNull();
    }

    @Test
    void toString_ContainsAllFields_test() {
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
        FavoriteResponseDto dtoA = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);
        FavoriteResponseDto dtoB = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);

        assertThat(dtoA).isEqualTo(dtoB);
        assertThat(dtoB).isEqualTo(dtoA);
    }

    @Test
    void equals_Transitive_test() {
        FavoriteResponseDto dtoA = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);
        FavoriteResponseDto dtoB = new FavoriteResponseDto(uuid, userId, challengeId, createdAt);
        FavoriteResponseDto dtoC= new FavoriteResponseDto(uuid, userId, challengeId, createdAt);

        assertThat(dtoA).isEqualTo(dtoB);
        assertThat(dtoB).isEqualTo(dtoC);
        assertThat(dtoA).isEqualTo(dtoC);
    }

    @Test
    void hashCode_ConsistentAcrossMultipleCalls_test() {
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
