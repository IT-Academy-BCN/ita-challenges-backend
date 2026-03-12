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
    void givenValidLeaderboardResponse_whenSerialize_thenCorrectJsonStructure() {
        List<LeaderboardEntryDto> leaderboard = List.of(
                createTestEntry("user1", 200),
                createTestEntry("user2", 300)
        );

        LeaderboardResponseDto dto = LeaderboardResponseDto.builder()
                .leaderboard(leaderboard)
                .build();

        JsonContent<LeaderboardResponseDto> result;
        try {
            result = json.write(dto);
        } catch (IOException e) {
            fail("Serialization failed: " + e.getMessage());
            return;
        }

        assertThat(result)
                .hasJsonPathArrayValue("@.leaderboard")
                .hasJsonPathStringValue("@.leaderboard[0].username", "user1")
                .hasJsonPathNumberValue("@.leaderboard[0].total_points", 200)
                .hasJsonPathStringValue("@.leaderboard[1].username", "user2")
                .hasJsonPathNumberValue("@.leaderboard[1].total_points", 300);
    }

    @Test
    void givenValidJson_whenDeserialize_thenCorrectLeaderboardResponse() {
        String jsonContent = "{\"leaderboard\": [{\"username\": \"user1\", \"total_points\": 200}]}";

        LeaderboardResponseDto dto;
        try {
            dto = json.parseObject(jsonContent);
        } catch (IOException e) {
            fail("Deserialization failed: " + e.getMessage());
            return;
        }

        assertThat(dto.getLeaderboard()).hasSize(1);
        assertThat(dto.getLeaderboard().getFirst().getTotalPoints()).isEqualTo(200);
    }

    @Test
    void givenEmptyLeaderboard_whenSerialize_thenCorrectJsonWithEmptyArray() {
        LeaderboardResponseDto dto = LeaderboardResponseDto.builder()
                .leaderboard(List.of())
                .build();

        JsonContent<LeaderboardResponseDto> result;
        try {
            result = json.write(dto);
        } catch (IOException e) {
            fail("Serialization failed: " + e.getMessage());
            return;
        }

        assertThat(result).hasJsonPathArrayValue("@.leaderboard");
    }

    @Test
    void givenBuilderPattern_whenCreateDto_thenLeaderboardIsSet() {
        List<LeaderboardEntryDto> leaderboard = List.of(
                createTestEntry("user1", 200)
        );

        LeaderboardResponseDto dto = LeaderboardResponseDto.builder()
                .leaderboard(leaderboard)
                .build();

        assertThat(dto.getLeaderboard()).hasSize(1);
    }

    @Test
    void givenAllArgsConstructor_whenCreateDto_thenFieldsAreSet() {
        List<LeaderboardEntryDto> leaderboard = List.of(
                createTestEntry("user1", 200),
                createTestEntry("user2", 300)
        );

        LeaderboardResponseDto dto = new LeaderboardResponseDto(leaderboard);

        assertThat(dto.getLeaderboard()).hasSize(2);
        assertThat(dto.getLeaderboard().getFirst().getUsername()).isEqualTo("user1");
        assertThat(dto.getLeaderboard().getFirst().getTotalPoints()).isEqualTo(200);
    }

    @Test
    void givenExistingDto_whenModifyWithSetters_thenFieldsAreUpdated() {
        List<LeaderboardEntryDto> leaderboard = List.of(createTestEntry("user1", 200));
        LeaderboardResponseDto dto = LeaderboardResponseDto.builder()
                .leaderboard(leaderboard)
                .build();

        List<LeaderboardEntryDto> newLeaderboard = List.of(createTestEntry("new_user", 500));
        dto.setLeaderboard(newLeaderboard);

        assertThat(dto.getLeaderboard()).isEqualTo(newLeaderboard);
    }
}
