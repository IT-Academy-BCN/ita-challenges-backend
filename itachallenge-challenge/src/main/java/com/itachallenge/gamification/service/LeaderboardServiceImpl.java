package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.LeaderboardEntryDto;
import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.gamification.repository.UserScoreRepository;
import com.itachallenge.gamification.repository.projection.LeaderboardAggregationResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private static final Logger log = LoggerFactory.getLogger(LeaderboardServiceImpl.class);
    private final UserScoreRepository userScoreRepository;

    @Override
    public Mono<LeaderboardResponseDto> getLeaderboard() {
        return userScoreRepository.aggregateUserScores()
                .map(this::mapToEntryDto)
                .collectList()
                .map(entries -> LeaderboardResponseDto.builder()
                        .leaderboard(entries)
                        .build())
                .onErrorResume(e -> {
                    log.error("Critical error in leaderboard service - database unavailable. {}", e.getMessage());
                    return Mono.error(new InternalServerErrorException(
                            "Unable to retrieve leaderboard data. Please try again later."
                    ));
                });
    }

    private LeaderboardEntryDto mapToEntryDto(LeaderboardAggregationResult agg) {
        return LeaderboardEntryDto.builder()
                .username(agg.getUsername() != null ? agg.getUsername() : "Anonymous")
                .totalPoints(agg.getTotalPoints() !=null ? agg.getTotalPoints() : 0)
                .build();
    }
}
