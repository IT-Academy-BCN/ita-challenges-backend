package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import reactor.core.publisher.Mono;

public interface LeaderboardService {
    Mono<LeaderboardResponseDto> getLeaderboard();
}
