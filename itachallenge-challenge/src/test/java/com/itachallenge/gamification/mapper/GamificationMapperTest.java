package com.itachallenge.gamification.mapper;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GamificationMapperTest {

    private final GamificationMapper mapper = new GamificationMapper();

    @Test
    void givenValidDoc_whenToPointEntryDto_thenDtoICorrect() {
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument doc = UserScoreDocument.builder()
                .points(10)
                .createdAt(now)
                .build();

        PointHistoryEntryDto dto = mapper.toPointEntryDto(doc);

        assertThat(dto).isNotNull();
        assertThat(dto.getPoints()).isEqualTo(10);
        assertThat(dto.getCreatedAt()).isEqualTo(now.toString());
    }

    @Test
    void givenNullDocument_whenToPointEntryDto_thenReturnsNull() {
        assertThat(mapper.toPointEntryDto(null)).isNull();
    }

    @Test
    void givenDocWithNullFields_whenToPointEntryDto_thenReturnsDefaultValues() {
        UserScoreDocument doc = UserScoreDocument.builder()
                .points(null)
                .createdAt(null)
                .build();

        PointHistoryEntryDto dto = mapper.toPointEntryDto(doc);

        assertThat(dto.getPoints()).isZero();
        assertThat(dto.getCreatedAt()).isEmpty();

    }

}
