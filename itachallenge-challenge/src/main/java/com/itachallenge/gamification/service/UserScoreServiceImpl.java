package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.challenge.dto.gamification.ScoresHistoryResponseDto;
import com.itachallenge.challenge.dto.gamification.WeeklyPointsDto;
import com.itachallenge.gamification.document.UserScoreDocument;
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

    @Override
    public Mono<PointsHistoryResponseDto> getUserPointsHistory(UUID userId) {
        return userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .collectList()
                .map(this::buildHistoryResponse);
    }

    @Override
    public Mono<ScoresHistoryResponseDto> getUserScoresHistoryChart(UUID userId) {
        return userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId)
                .collectList()
                .map(docs -> buildChartResponse(userId, docs));
    }

    private PointsHistoryResponseDto buildHistoryResponse(List<UserScoreDocument> docs) {
        int totalPoints = docs.stream()
                .map(UserScoreDocument::getPointsEarned)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        List<PointHistoryEntryDto> history = docs.stream()
                .sorted(Comparator.comparing(UserScoreDocument::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(doc -> PointHistoryEntryDto.builder()
                        .createdAt(doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : "")
                        .points(doc.getPointsEarned() != null ? doc.getPointsEarned() : 0)
                        .build())
                .toList();

        return PointsHistoryResponseDto.builder()
                .username(docs.isEmpty() ? "" : docs.getFirst().getUsername())
                .totalPoints(totalPoints)
                .history(history)
                .build();
    }

    private ScoresHistoryResponseDto buildChartResponse(UUID userId, List<UserScoreDocument> docs) {
        if (docs.isEmpty()) {
            return ScoresHistoryResponseDto.builder()
                    .userId(userId)
                    .totalPoints(0)
                    .aggregationType("WEEKLY")
                    .history(List.of())
                    .build();
        }

        Map<String, Integer> pointsByWeek = new LinkedHashMap<>();

        for (UserScoreDocument doc : docs) {
            if (doc.getCreatedAt() == null || doc.getPointsEarned() == null) continue;
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
                .aggregationType("WEEKLY")
                .history(history)
                .build();
    }
}
