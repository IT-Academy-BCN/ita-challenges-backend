package com.itachallenge.challenge.dto.gamification;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointsHistoryDto {
    private int totalPoints;
    private List<PointEntryDto> history;
}
