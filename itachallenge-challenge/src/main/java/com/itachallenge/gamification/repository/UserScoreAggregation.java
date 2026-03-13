package com.itachallenge.gamification.repository;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class UserScoreAggregation {
    private String username;
    private Integer totalPoints;
}
