package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.ScoresHistoryResponseDto;
import com.itachallenge.challenge.dto.gamification.WeeklyPointsDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.temporal.WeekFields;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserScoreServiceImpl implements UserScoreService {

    private final UserScoreRepository userScoreRepository;
    private static final String AGGREGATION_TYPE_WEEKLY = "WEEKLY";

    @Override
    public Mono<ScoresHistoryResponseDto> getUserScoresHistoryChart(UUID userId) {
        return userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId)
                .collectList()
                .map(docs -> buildChartResponse(userId, docs));
    }

    @Override
    public Mono<Void> registerPoints(UUID userId, ActivityType type, UUID challengeId) {

        if (type == null) {
            return Mono.error(new IllegalArgumentException("ActivityType cannot be null"));
        }

        if (userId == null) {
            return Mono.error(new IllegalArgumentException("userId cannot be null"));
        }

        if (type == ActivityType.CHALLENGE_COMPLETED && challengeId == null) {
            return Mono.error(new IllegalArgumentException("challengeId is required for CHALLENGE_COMPLETED"));
        }

        if (type != ActivityType.CHALLENGE_COMPLETED && challengeId != null) {
            return Mono.error(new IllegalArgumentException("challengeId must be null for non-challenge activities"));
        }

        int points = type.getPoints();
        UUID finalChallengeId = (type == ActivityType.CHALLENGE_COMPLETED) ? challengeId : null;

        UserScoreDocument document = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .activityType(type)
                .pointsEarned(points)
                .challengeId(finalChallengeId)
                .build();

        return userScoreRepository.save(document).then();
    }

    @Override
    public Mono<Integer> assignPoints(UUID userId, ActivityType activityType) {

        if (userId == null) {
            return Mono.error(new IllegalArgumentException("userId cannot be null"));
        }

        if (activityType == null) {
            return Mono.error(new IllegalArgumentException("ActivityType cannot be null"));
        }

        return registerPoints(userId, activityType, null)
                .thenReturn(activityType.getPoints());
    }

    private ScoresHistoryResponseDto buildChartResponse(UUID userId, List<UserScoreDocument> docs) {

        if (docs.isEmpty()) {
            return ScoresHistoryResponseDto.builder()
                    .userId(userId)
                    .totalPoints(0)
                    .aggregationType(AGGREGATION_TYPE_WEEKLY)
                    .history(List.of())
                    .build();
        }

        Map<String, Integer> pointsByWeek = new LinkedHashMap<>();

        for (UserScoreDocument doc : docs) {
            if (doc.getCreatedAt() == null || doc.getPointsEarned() == null) {
                continue;
            }

            int weekYear = doc.getCreatedAt().get(WeekFields.ISO.weekBasedYear());
            int weekNumber = doc.getCreatedAt().get(WeekFields.ISO.weekOfWeekBasedYear());
            String week = String.format("%d-W%02d", weekYear, weekNumber);

            pointsByWeek.merge(week, doc.getPointsEarned(), Integer::sum);
        }

        List<WeeklyPointsDto> history = new ArrayList<>();
        int accumulated = 0;

        for (Map.Entry<String, Integer> entry : pointsByWeek.entrySet()) {
            accumulated += entry.getValue();

            history.add(WeeklyPointsDto.builder()
                    .period(entry.getKey())
                    .pointsEarned(entry.getValue())
                    .accumulatedAtEnd(accumulated)
                    .build());
        }

        return ScoresHistoryResponseDto.builder()
                .userId(userId)
                .totalPoints(accumulated)
                .aggregationType(AGGREGATION_TYPE_WEEKLY)
                .history(history)
                .build();
    }
}
