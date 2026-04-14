package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.LeaderboardEntryDto;
import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.gamification.repository.UserScoreRepository;
import com.itachallenge.gamification.repository.projection.LeaderboardAggregationResult;
import com.itachallenge.gamification.util.WeeklyWindow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private static final Logger log = LoggerFactory.getLogger(LeaderboardServiceImpl.class);
    private static final ZoneId LEADERBOARD_ZONE = ZoneId.of("Europe/Madrid");
    private static final int GOLD_LIMIT = 10;
    private static final int SILVER_LIMIT = 20;
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

    @Override
    public Mono<WeeklyLeaguesResult> getWeeklyLeagues() {
        WeeklyWindow weeklyWindow = WeeklyWindow.fromReferenceDateTime(ZonedDateTime.now(LEADERBOARD_ZONE));
        return userScoreRepository.aggregateUserScoresByPeriod(
                        weeklyWindow.getFromInclusive(),
                        weeklyWindow.getToInclusive()
                )
                .map(this::mapToEntryDto)
                .collectList()
                .map(this::sortAndSplitByLeague)
                .onErrorResume(e -> {
                    log.error("Critical error in weekly leagues service - database unavailable. {}", e.getMessage());
                    return Mono.error(new InternalServerErrorException(
                            "Unable to retrieve weekly leagues data. Please try again later."
                    ));
                });
    }

    private LeaderboardEntryDto mapToEntryDto(LeaderboardAggregationResult agg) {
        return LeaderboardEntryDto.builder()
                .username(agg.getUsername() != null ? agg.getUsername() : "Anonymous")
                .totalPoints(agg.getTotalPoints() != null ? agg.getTotalPoints() : 0)
                .build();
    }

    /**
     * Orders weekly entries by points (desc) and username (asc, null-safe), then splits into leagues.
     */
    private WeeklyLeaguesResult sortAndSplitByLeague(List<LeaderboardEntryDto> entries) {
        List<LeaderboardEntryDto> ordered = new ArrayList<>(entries);
        ordered.sort(Comparator
                .comparing(LeaderboardEntryDto::getTotalPoints, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(LeaderboardEntryDto::getUsername, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

        int goldEnd = Math.min(GOLD_LIMIT, ordered.size());
        int silverEnd = Math.min(GOLD_LIMIT + SILVER_LIMIT, ordered.size());

        return WeeklyLeaguesResult.builder()
                .gold(new ArrayList<>(ordered.subList(0, goldEnd)))
                .silver(new ArrayList<>(ordered.subList(goldEnd, silverEnd)))
                .bronze(new ArrayList<>(ordered.subList(silverEnd, ordered.size())))
                .build();
    }
}
