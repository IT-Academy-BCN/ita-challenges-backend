package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.LeaderboardEntryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
@AllArgsConstructor
public class WeeklyLeaguesResult {
    private final List<LeaderboardEntryDto> gold;
    private final List<LeaderboardEntryDto> silver;
    private final List<LeaderboardEntryDto> bronze;
}
