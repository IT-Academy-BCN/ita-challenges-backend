package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointEntryDto;
import com.itachallenge.challenge.dto.gamification.PointsHistoryDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.mapper.GamificationMapper;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final UserScoreRepository userScoreRepository;
    private final GamificationMapper gamificationMapper;

    @Override
    public Mono<PointsHistoryDto> getUserPointsHistory(String username) {
        return userScoreRepository.findByUsernameOrderByCreatedAtAsc(username)
                .collectList()
                .map(this::buildHistoryResponse);
    }

    private PointsHistoryDto buildHistoryResponse(List<UserScoreDocument> docs) {
        int totalPoints = docs.stream()
                .map(UserScoreDocument::getPoints)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        List<PointEntryDto> history = docs.stream()
                .map(gamificationMapper::toPointEntryDto)
                .collect(Collectors.toList());

        return PointsHistoryDto.builder()
                .totalPoints(totalPoints)
                .history(history)
                .build();
    }
}
