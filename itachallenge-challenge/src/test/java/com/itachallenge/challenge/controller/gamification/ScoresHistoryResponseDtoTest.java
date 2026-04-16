package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.ScoresHistoryResponseDto;
import com.itachallenge.challenge.dto.gamification.WeeklyPointsDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ScoresHistoryResponseDtoTest {

    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldBuildDtoCorrectlyUsingBuilder() {
        WeeklyPointsDto entry = WeeklyPointsDto.builder()
                .period("2024-W10")
                .pointsEarned(150)
                .accumulatedAtEnd(150)
                .build();

        ScoresHistoryResponseDto dto = ScoresHistoryResponseDto.builder()
                .userId(userId)
                .totalPoints(150)
                .aggregationType("WEEKLY")
                .history(List.of(entry))
                .build();

        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getTotalPoints()).isEqualTo(150);
        assertThat(dto.getAggregationType()).isEqualTo("WEEKLY");
        assertThat(dto.getHistory()).hasSize(1);
        assertThat(dto.getHistory().getFirst().getPeriod()).isEqualTo("2024-W10");
        assertThat(dto.getHistory().getFirst().getPointsEarned()).isEqualTo(150);
        assertThat(dto.getHistory().getFirst().getAccumulatedAtEnd()).isEqualTo(150);
    }

    @Test
    void shouldBuildDtoWithEmptyHistory() {
        ScoresHistoryResponseDto dto = ScoresHistoryResponseDto.builder()
                .userId(userId)
                .totalPoints(0)
                .aggregationType("WEEKLY")
                .history(List.of())
                .build();

        assertThat(dto.getTotalPoints()).isZero();
        assertThat(dto.getHistory()).isEmpty();
    }
}