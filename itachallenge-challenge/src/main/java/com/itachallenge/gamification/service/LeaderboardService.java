package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.challenge.dto.gamification.WeeklyLeaguesResponseDto;
import reactor.core.publisher.Mono;

public interface LeaderboardService {
    Mono<LeaderboardResponseDto> getLeaderboard();
    Mono<WeeklyLeaguesResponseDto> getWeeklyLeagues();
    Mono<WeeklyLeaguesResult> getWeeklyLeagues();
}
