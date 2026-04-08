package com.itachallenge.challenge.dto.gamification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Jacksonized
@AllArgsConstructor
public class ScoresHistoryResponseDto {
    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("total_points")
    private int totalPoints;

    @JsonProperty("aggregation_type")
    private String aggregationType;

    private List<WeeklyPointsDto> history;
}