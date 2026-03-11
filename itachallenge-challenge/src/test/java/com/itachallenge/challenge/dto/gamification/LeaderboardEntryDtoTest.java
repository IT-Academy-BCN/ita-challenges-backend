package com.itachallenge.challenge.dto.gamification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@JsonTest
class LeaderboardEntryDtoTest {

    @Autowired
    private JacksonTester<LeaderboardEntryDto> json;

    private static final String TEST_USERNAME = "testUser";
    private static final int TEST_POINTS = 150;

    @Test
    void givenValidLeaderboardEntry_whenSerialize_thenCorrectJsonStructure() {
        LeaderboardEntryDto dto = LeaderboardEntryDto.builder()
                .username(TEST_USERNAME)
                .totalPoints(TEST_POINTS)
                .build();

        JsonContent<LeaderboardEntryDto> result;
        try {
            result = json.write(dto);
        } catch (IOException e) {
            fail("Serialization failed: " + e.getMessage());
            return;
        }

        assertThat(result).hasJsonPathStringValue("@.username", TEST_USERNAME);
        assertThat(result).hasJsonPathNumberValue("@.total_points", TEST_POINTS);
    }

    @Test
    void givenValidJson_whenDeserialize_thenCorrectLeaderboardEntry() {
        String jsonContent = String.format("{\"username\":\"%s\",\"total_points\":%d}",
                TEST_USERNAME, TEST_POINTS);

        LeaderboardEntryDto dto;
        try {
            dto = json.parseObject(jsonContent);
        } catch (IOException e) {
            fail("Deserialization failed: " + e.getMessage());
            return;
        }

        assertThat(dto.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(dto.getTotalPoints()).isEqualTo(TEST_POINTS);
    }

    @Test
    void givenBuilderPattern_whenCreateDto_thenAllFieldsAreSet() {
        LeaderboardEntryDto dto = LeaderboardEntryDto.builder()
                .username(TEST_USERNAME)
                .totalPoints(TEST_POINTS)
                .build();

        assertThat(dto.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(dto.getTotalPoints()).isEqualTo(TEST_POINTS);
    }

    @Test
    void givenNoArgsConstructor_whenCreateDto_thenDtoIsNotNullAndFieldsAreSet() {
        LeaderboardEntryDto dto = new LeaderboardEntryDto();

        assertThat(dto).isNotNull();

        dto.setUsername(TEST_USERNAME);
        dto.setTotalPoints(TEST_POINTS);

        assertThat(dto.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(dto.getTotalPoints()).isEqualTo(TEST_POINTS);
    }

    @Test
    void givenAllArgsConstructor_whenCreateDto_thenAllFieldsAreSet() {
        LeaderboardEntryDto dto = new LeaderboardEntryDto(TEST_USERNAME, TEST_POINTS);

        assertThat(dto.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(dto.getTotalPoints()).isEqualTo(TEST_POINTS);
    }

    @Test
    void givenExistingDto_whenModifyWithSetters_thenFieldsAreUpdated() {
        LeaderboardEntryDto dto = LeaderboardEntryDto.builder()
                .username(TEST_USERNAME)
                .totalPoints(TEST_POINTS)
                .build();

        assertThat(dto.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(dto.getTotalPoints()).isEqualTo(TEST_POINTS);

        String newUsername = "updated_user";
        int newPoints = 400;

        dto.setUsername(newUsername);
        dto.setTotalPoints(newPoints);

        assertThat(dto.getUsername())
                .as("Username should be updated to: %s",  newUsername)
                .isEqualTo(newUsername);
        assertThat(dto.getTotalPoints())
                .as("Total points should be updated to: %d",  newPoints)
                .isEqualTo(newPoints);

        assertThat(dto.getUsername()).isNotEqualTo(TEST_USERNAME);
        assertThat(dto.getTotalPoints()).isNotEqualTo(TEST_POINTS);
    }

    @Test
    void givenZeroPoints_whenCreateDto_thenPointsAreZero() {
        LeaderboardEntryDto dto = LeaderboardEntryDto.builder()
                .username(TEST_USERNAME)
                .totalPoints(0)
                .build();

        assertThat(dto.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(dto.getTotalPoints()).isEqualTo(0);
    }

    @Test
    void givenDtoWithSameValues_whenCompare_thenEqualsAndHashCodeWork() {
        LeaderboardEntryDto dto1 = new LeaderboardEntryDto(TEST_USERNAME, TEST_POINTS);
        LeaderboardEntryDto dto2 = new LeaderboardEntryDto(TEST_USERNAME, TEST_POINTS);
        LeaderboardEntryDto dto3 = new LeaderboardEntryDto("newUser", 111);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1).isNotSameAs(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
    }
}
