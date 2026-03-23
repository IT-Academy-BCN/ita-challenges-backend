package com.itachallenge.challenge.dto.gamification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class LeaderboardEntryDtoTest {

    @Autowired
    private JacksonTester<LeaderboardEntryDto> json;

    private static final String TEST_USERNAME = "testUser";
    private static final int TEST_POINTS = 150;

    @Test
    void givenValidLeaderboardEntry_whenSerialize_thenCorrectJsonStructure() throws IOException {
        LeaderboardEntryDto dto = LeaderboardEntryDto.builder()
                .username(TEST_USERNAME)
                .totalPoints(TEST_POINTS)
                .build();

        JsonContent<LeaderboardEntryDto> result;
        result = json.write(dto);

        assertThat(result)
                .hasJsonPathStringValue("@.username", TEST_USERNAME)
                .hasJsonPathNumberValue("@.total_points", TEST_POINTS);
    }

    @Test
    void givenValidJson_whenDeserialize_thenCorrectLeaderboardEntry() throws IOException {
        String jsonContent = String.format("{\"username\":\"%s\",\"total_points\":%d}",
                TEST_USERNAME, TEST_POINTS);

        LeaderboardEntryDto dto;
        dto = json.parseObject(jsonContent);

        assertThat(dto.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(dto.getTotalPoints()).isEqualTo(TEST_POINTS);
    }
}
