package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    @Override
    public Mono<Void> registerPoints(UUID userId, String username, ActivityType type, UUID challengeId) {

        if (type == null) {
            return Mono.error(new IllegalArgumentException("ActivityType cannot be null"));
        }

        if (username == null || username.isBlank()) {
            return Mono.error(new IllegalArgumentException("username cannot be null or blank"));
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
                .username(username)
                .activityType(type)
                .pointsEarned(points)
                .challengeId(finalChallengeId)
                .build();

        return userScoreRepository.save(document).then();
    }
}
