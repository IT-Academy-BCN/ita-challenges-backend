package com.itachallenge.challenge.dto.gamification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@AllArgsConstructor
public class LeaderboardEntryDto {
    private final String username;

    @JsonProperty("total_points")
    private final Integer totalPoints;
}
