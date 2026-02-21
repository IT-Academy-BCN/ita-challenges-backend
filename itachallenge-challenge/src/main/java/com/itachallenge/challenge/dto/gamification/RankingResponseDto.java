package com.itachallenge.challenge.dto.gamification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingResponseDto {

    private String username;
    private int points;
}
