package com.itachallenge.challenge.dto.gamification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
@AllArgsConstructor
public class WeeklyLeaguesResponseDto {
    private final List<LeaderboardEntryDto> gold;
    private final List<LeaderboardEntryDto> silver;
    private final List<LeaderboardEntryDto> bronze;
}
