package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.mapper.GamificationMapper;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final UserScoreRepository userScoreRepository;
    private final GamificationMapper gamificationMapper;

    @Override
    public Mono<PointsHistoryResponseDto> getUserPointsHistory(UUID userId) {
        return userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .collectList()
                .map(this::buildHistoryResponse);
    }

    private PointsHistoryResponseDto buildHistoryResponse(List<UserScoreDocument> docs) {
        int totalPoints = docs.stream()
                .map(UserScoreDocument::getPoints)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        List<PointHistoryEntryDto> history = docs.stream()
                .map(gamificationMapper::toPointEntryDto)
                .collect(Collectors.toList());

        return PointsHistoryResponseDto.builder()
                .username(docs.isEmpty() ? "" : docs.getFirst().getUsername())
                .totalPoints(totalPoints)
                .history(history)
                .build();
    }
}
