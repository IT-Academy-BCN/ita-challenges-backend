package com.itachallenge.challenge.dto.gamification;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
@AllArgsConstructor
public class LeaderboardResponseDto {
    private final List<LeaderboardEntryDto> leaderboard;
}
