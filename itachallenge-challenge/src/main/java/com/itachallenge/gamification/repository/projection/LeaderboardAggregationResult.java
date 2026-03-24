package com.itachallenge.gamification.repository.projection;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class LeaderboardAggregationResult {
    private String username;
    private Integer totalPoints;
}
