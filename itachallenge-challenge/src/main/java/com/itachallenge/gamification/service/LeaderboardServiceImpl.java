package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.LeaderboardEntryDto;
import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private static final Logger log = LoggerFactory.getLogger(LeaderboardServiceImpl.class);
    private final UserScoreRepository userScoreRepository;

    @Override
    public Mono<LeaderboardResponseDto> getLeaderboard() {
        return userScoreRepository.aggregateUserScores()
                .map(aggregation -> LeaderboardEntryDto.builder()
                        .username(aggregation.getUsername() != null ? aggregation.getUsername() : "Anonymous")
                        .totalPoints(aggregation.getTotalPoints() !=null ? aggregation.getTotalPoints() : 0)
                        .build())
                .collectList()
                .map(entries -> LeaderboardResponseDto.builder()
                        .leaderboard(entries)
                        .build())
                .onErrorResume(e -> {
                    log.error("Error retrieving leaderboard data: {}", e.getMessage());
                    return Mono.just(LeaderboardResponseDto.builder()
                            .leaderboard(Collections.emptyList())
                            .build());
                });
    }
}
