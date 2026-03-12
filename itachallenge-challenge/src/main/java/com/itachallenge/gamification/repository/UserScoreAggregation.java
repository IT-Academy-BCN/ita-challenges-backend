package com.itachallenge.gamification.repository;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserScoreAggregation {

    private String username;
    private int totalPoints;
}
