package com.itachallenge.challenge.dto.gamification;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class PointsHistoryResponseDtoTest {

    @Test
    void testDtoStructure() {
        PointHistoryEntryDto entry = PointHistoryEntryDto.builder().points(10).build();

        PointsHistoryResponseDto dto = PointsHistoryResponseDto.builder()
                .username("testUser")
                .totalPoints(10)
                .history(List.of(entry))
                .build();

        assertThat(dto.getUsername()).isEqualTo("testUser");
        assertThat(dto.getTotalPoints()).isEqualTo(10);
        assertThat(dto.getHistory()).hasSize(1);
    }

}
