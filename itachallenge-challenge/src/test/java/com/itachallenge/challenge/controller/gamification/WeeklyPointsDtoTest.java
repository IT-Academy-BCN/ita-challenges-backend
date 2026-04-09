package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.WeeklyPointsDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WeeklyPointsDtoTest {

    @Test
    void shouldBuildDtoCorrectlyUsingBuilder() {
        WeeklyPointsDto dto = WeeklyPointsDto.builder()
                .period("2024-W10")
                .pointsEarned(150)
                .accumulatedAtEnd(225)
                .build();

        assertThat(dto.getPeriod()).isEqualTo("2024-W10");
        assertThat(dto.getPointsEarned()).isEqualTo(150);
        assertThat(dto.getAccumulatedAtEnd()).isEqualTo(225);
    }

    @Test
    void shouldBuildDtoWithZeroPoints() {
        WeeklyPointsDto dto = WeeklyPointsDto.builder()
                .period("2024-W10")
                .pointsEarned(0)
                .accumulatedAtEnd(0)
                .build();

        assertThat(dto.getPointsEarned()).isEqualTo(0);
        assertThat(dto.getAccumulatedAtEnd()).isEqualTo(0);
    }
}