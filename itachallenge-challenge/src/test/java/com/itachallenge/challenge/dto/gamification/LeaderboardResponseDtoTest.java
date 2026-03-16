package com.itachallenge.challenge.dto.gamification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@JsonTest
class LeaderboardResponseDtoTest {

    @Autowired
    private JacksonTester<LeaderboardResponseDto> json;

    private LeaderboardEntryDto createTestEntry(String username, int totalPoints) {
        return LeaderboardEntryDto.builder()
                .username(username)
                .totalPoints(totalPoints)
                .build();
    }

    @Test
    void givenValidLeaderboardResponse_whenSerialize_thenCorrectJsonStructure() throws IOException {
        List<LeaderboardEntryDto> leaderboard = List.of(
                createTestEntry("user1", 200),
                createTestEntry("user2", 300)
        );

        LeaderboardResponseDto dto = LeaderboardResponseDto.builder()
                .leaderboard(leaderboard)
                .build();

        JsonContent<LeaderboardResponseDto> result;
        result = json.write(dto);

        assertThat(result)
                .hasJsonPathArrayValue("@.leaderboard")
                .hasJsonPathStringValue("@.leaderboard[0].username", "user1")
                .hasJsonPathNumberValue("@.leaderboard[0].total_points", 200)
                .hasJsonPathStringValue("@.leaderboard[1].username", "user2")
                .hasJsonPathNumberValue("@.leaderboard[1].total_points", 300);
    }

    @Test
    void givenValidJson_whenDeserialize_thenCorrectLeaderboardResponse() throws IOException {
        String jsonContent = "{\"leaderboard\": [{\"username\": \"user1\", \"total_points\": 200}]}";

        LeaderboardResponseDto dto;
        dto = json.parseObject(jsonContent);

        assertThat(dto.getLeaderboard()).hasSize(1);
        assertThat(dto.getLeaderboard().getFirst().getTotalPoints()).isEqualTo(200);
    }

    @Test
    void givenEmptyLeaderboard_whenSerialize_thenCorrectJsonWithEmptyArray() throws IOException {
        LeaderboardResponseDto dto = LeaderboardResponseDto.builder()
                .leaderboard(List.of())
                .build();

        JsonContent<LeaderboardResponseDto> result;
        result = json.write(dto);

        assertThat(result).hasJsonPathArrayValue("@.leaderboard");
        assertThat(result).hasEmptyJsonPathValue("@.leaderboard");    }
}