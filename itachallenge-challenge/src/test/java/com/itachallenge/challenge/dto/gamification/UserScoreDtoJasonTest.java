package com.itachallenge.challenge.dto.gamification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@JsonTest
class UserScoreDtoJasonTest {

    @Autowired
    private JacksonTester<PointsHistoryResponseDto> json;

    @Test
    void testSerializationContract() {
        try {
            PointHistoryEntryDto entry = PointHistoryEntryDto.builder()
                    .createdAt("2011-01-11T11:11:11")
                    .points(10)
                    .build();

            PointsHistoryResponseDto dto = PointsHistoryResponseDto.builder()
                    .username("testUser")
                    .totalPoints(10)
                    .history(List.of(entry))
                    .build();

            JsonContent<PointsHistoryResponseDto> result = json.write(dto);

            assertThat(result).hasJsonPathStringValue("@.username", "testUser");
            assertThat(result).hasJsonPathNumberValue("@.totalPoints", 10);
            assertThat(result).hasJsonPathStringValue("@.history[0].date", "2011-01-11T11:11:11");
            assertThat(result).hasJsonPathNumberValue("@.history[0].points", 10);
            assertThat(result).doesNotHaveJsonPath("@.history[0].createdAt");
        } catch (Exception e) {
            fail("Serialization failed: " + e.getMessage());
        }
    }
}
