package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.LeaderboardEntryDto;
import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final UserScoreRepository userScoreRepository;

    @Override
    public Mono<LeaderboardResponseDto> getLeaderboard() {
        return userScoreRepository.aggregateUserScores()
                .map(aggregation -> LeaderboardEntryDto.builder()
                        .username(aggregation.getUsername() != null ? aggregation.getUsername() : "Anonymous")
                        .totalPoints(aggregation.getTotalPoints())
                        .build())
                .collectList()
                .map(entries -> LeaderboardResponseDto.builder()
                        .leaderboard(entries)
                        .build());
    }
}
