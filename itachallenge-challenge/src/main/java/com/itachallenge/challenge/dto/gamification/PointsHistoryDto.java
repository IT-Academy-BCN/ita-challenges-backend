package com.itachallenge.challenge.dto.gamification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointsHistoryDto {
    private int totalPoints;
    private List<PointEntryDto> history;
}
