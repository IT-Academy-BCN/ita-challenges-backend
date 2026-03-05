package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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
    @Override
    public Mono<Void> recordPoints(UUID userId, UUID challengeId, int points) {
        return userScoreRepository.existsByUserIdAndChallengeId(userId, challengeId)
                .flatMap(alreadyAwarded -> Boolean.TRUE.equals(alreadyAwarded)
                        ? Mono.<Void>empty()
                        : userScoreRepository.save(
                        UserScoreDocument.builder()
                                .id(UUID.randomUUID())
                                .userId(userId)
                                .challengeId(challengeId)
                                .pointsEarned(points)
                                .createdAt(LocalDateTime.now())
                                .build()
                ).then());
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
}
