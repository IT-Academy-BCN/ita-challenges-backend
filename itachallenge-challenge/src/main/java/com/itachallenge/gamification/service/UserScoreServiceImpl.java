package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.exception.ServiceException;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserScoreServiceImpl implements UserScoreService {

    private final UserScoreRepository userScoreRepository;
    private static final Logger log = LoggerFactory.getLogger(UserScoreServiceImpl.class);

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
    public Flux<RankingResponseDto> getRankingDescOrder() {
        return userScoreRepository.findUsersRanking()
                .onErrorResume(DataAccessException.class, ex -> {
                    log.error("DB error fetching ranking: {}", ex.getMessage());
                    return Flux.error(new ServiceException("Could not retrieve ranking"));
                });
    }
}
