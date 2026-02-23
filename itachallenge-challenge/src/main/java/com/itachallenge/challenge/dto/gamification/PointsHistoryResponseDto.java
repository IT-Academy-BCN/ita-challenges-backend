package com.itachallenge.challenge.dto.gamification;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointsHistoryResponseDto {
    private String username;
    private int totalPoints;
    private List<PointHistoryEntryDto> history;
}
