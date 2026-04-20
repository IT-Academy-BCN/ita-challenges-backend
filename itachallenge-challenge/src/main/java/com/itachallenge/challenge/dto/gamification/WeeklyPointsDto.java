package com.itachallenge.challenge.dto.gamification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@AllArgsConstructor
public class WeeklyPointsDto {
    private String period;

    @JsonProperty("points_earned")
    private int pointsEarned;

    @JsonProperty("accumulated_at_end")
    private int accumulatedAtEnd;
}