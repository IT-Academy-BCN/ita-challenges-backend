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

    private static final int PAGE = 0;
    private static final int SIZE = 20;
    private static final long TOTAL_ELEMENTS = 2;
    private static final int TOTAL_PAGES = 1;
    private static final int NEW_PAGE = 2;
    private static final int NEW_SIZE = 10;
    private static final long NEW_TOTAL_ELEMENTS = 25;
    private static final int NEW_TOTAL_PAGES = 3;

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
                .currentPage(PAGE)
                .pageSize(SIZE)
                .totalElements(TOTAL_ELEMENTS)
                .totalPages(TOTAL_PAGES)
                .build();

        JsonContent<LeaderboardResponseDto> result;
        try {
            result = json.write(dto);
        } catch (IOException e) {
            fail("Serialization failed: " + e.getMessage());
            return;
        }

        assertThat(result).hasJsonPathNumberValue("@.page", PAGE);
        assertThat(result).hasJsonPathNumberValue("@.size", SIZE);
        assertThat(result).hasJsonPathNumberValue("@.totalElements", TOTAL_ELEMENTS);
        assertThat(result).hasJsonPathNumberValue("@.totalPages", TOTAL_PAGES);

        assertThat(result).hasJsonPathArrayValue("@.leaderboard");
        assertThat(result).hasJsonPathStringValue("@.leaderboard[0].username", "user1");
        assertThat(result).hasJsonPathNumberValue("@.leaderboard[0].total_points", 200);
        assertThat(result).hasJsonPathStringValue("@.leaderboard[1].username", "user2");
        assertThat(result).hasJsonPathNumberValue("@.leaderboard[1].total_points", 300);
    }

    @Test
    void givenValidJson_whenDeserialize_thenCorrectLeaderboardResponse() {
        String jsonContent = """
                {
                "leaderboard": [
                {"username": "user1", "total_points": 200},
                {"username": "user2", "total_points": 300}
                ],
                "page": 0,
                "size": 20,
                "totalElements": 2,
                "totalPages": 1
                }
                """;

        LeaderboardResponseDto dto;
        try {
            dto = json.parseObject(jsonContent);
        } catch (IOException e) {
            fail("Deserialization failed: " + e.getMessage());
            return;
        }

        assertThat(dto.getCurrentPage()).isEqualTo(PAGE);
        assertThat(dto.getPageSize()).isEqualTo(SIZE);
        assertThat(dto.getTotalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(dto.getTotalPages()).isEqualTo(TOTAL_PAGES);
        assertThat(dto.getLeaderboard()).hasSize(2);
        assertThat(dto.getLeaderboard().get(0).getUsername()).isEqualTo("user1");
        assertThat(dto.getLeaderboard().get(0).getTotalPoints()).isEqualTo(200);
        assertThat(dto.getLeaderboard().get(1).getUsername()).isEqualTo("user2");
        assertThat(dto.getLeaderboard().get(1).getTotalPoints()).isEqualTo(300);
    }

    @Test
    void givenEmptyLeaderboard_whenSerialize_thenCorrectJsonWithEmptyArray() {
        LeaderboardResponseDto dto = LeaderboardResponseDto.builder()
                .leaderboard(List.of())
                .currentPage(PAGE)
                .pageSize(SIZE)
                .totalElements(0)
                .totalPages(0)
                .build();

        JsonContent<LeaderboardResponseDto> result;
        try {
            result = json.write(dto);
        } catch (IOException e) {
            fail("Serialization failed: " + e.getMessage());
            return;
        }

        assertThat(result).hasJsonPathArrayValue("@.leaderboard");
        assertThat(result).hasJsonPathNumberValue("@.totalElements", 0);
    }

    @Test
    void givenBuilderPattern_whenCreateDto_thenAllFieldsAreSet() {
        List<LeaderboardEntryDto> leaderboard = List.of(
                createTestEntry("user1", 200)
        );

        LeaderboardResponseDto dto = LeaderboardResponseDto.builder()
                .currentPage(PAGE)
                .pageSize(SIZE)
                .totalElements(TOTAL_ELEMENTS)
                .totalPages(TOTAL_PAGES)
                .leaderboard(leaderboard)
                .build();

        assertThat(dto.getCurrentPage()).isEqualTo(PAGE);
        assertThat(dto.getPageSize()).isEqualTo(SIZE);
        assertThat(dto.getTotalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(dto.getTotalPages()).isEqualTo(TOTAL_PAGES);
        assertThat(dto.getLeaderboard()).hasSize(1);
    }

    @Test
    void givenAllArgsConstructor_whenCreateDto_thenAllFieldsAreSet() {
        List<LeaderboardEntryDto> leaderboard = List.of(
                createTestEntry("user1", 200),
                createTestEntry("user2", 300)
        );

        LeaderboardResponseDto dto = new LeaderboardResponseDto(leaderboard, PAGE, SIZE, TOTAL_ELEMENTS, TOTAL_PAGES);

        assertThat(dto.getLeaderboard()).hasSize(2);
        assertThat(dto.getCurrentPage()).isEqualTo(PAGE);
        assertThat(dto.getPageSize()).isEqualTo(SIZE);
        assertThat(dto.getTotalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(dto.getTotalPages()).isEqualTo(TOTAL_PAGES);
    }

    @Test
    void givenExistingDto_whenModifyWithSetters_thenFieldsAreUpdated() {
        List<LeaderboardEntryDto> leaderboard = List.of(createTestEntry("user1", 200));
        LeaderboardResponseDto dto = LeaderboardResponseDto.builder()
                .leaderboard(leaderboard)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .totalElements(TOTAL_ELEMENTS)
                .totalPages(TOTAL_PAGES)
                .build();

        assertThat(dto.getCurrentPage()).isEqualTo(PAGE);
        assertThat(dto.getPageSize()).isEqualTo(SIZE);

        List<LeaderboardEntryDto> newLeaderboard = List.of(createTestEntry("new_user", 500));
        dto.setLeaderboard(newLeaderboard);
        dto.setCurrentPage(NEW_PAGE);
        dto.setPageSize(NEW_SIZE);
        dto.setTotalElements(NEW_TOTAL_ELEMENTS);
        dto.setTotalPages(NEW_TOTAL_PAGES);

        assertThat(dto.getLeaderboard()).isEqualTo(newLeaderboard);
        assertThat(dto.getCurrentPage()).isEqualTo(NEW_PAGE);
        assertThat(dto.getPageSize()).isEqualTo(NEW_SIZE);
        assertThat(dto.getTotalElements()).isEqualTo(NEW_TOTAL_ELEMENTS);
        assertThat(dto.getTotalPages()).isEqualTo(NEW_TOTAL_PAGES);
    }

    @Test
    void givenDtoWithSameValues_whenCompare_thenEqualsAndHashCodeWork() {
        List<LeaderboardEntryDto> leaderboard = List.of(createTestEntry("user1", 200));

        LeaderboardResponseDto dto1 = new LeaderboardResponseDto(leaderboard, PAGE, SIZE, TOTAL_ELEMENTS, TOTAL_PAGES);
        LeaderboardResponseDto dto2 = new LeaderboardResponseDto(leaderboard, PAGE, SIZE, TOTAL_ELEMENTS, TOTAL_PAGES);
        LeaderboardResponseDto dto3 = new LeaderboardResponseDto(leaderboard, NEW_PAGE, NEW_SIZE, NEW_TOTAL_ELEMENTS, NEW_TOTAL_PAGES);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1).isNotSameAs(dto2);
    }
}
