package com.itachallenge.challenge.dto.gamification;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LeaderboardResponseDto {
    private List<LeaderboardEntryDto> leaderboard;
}
