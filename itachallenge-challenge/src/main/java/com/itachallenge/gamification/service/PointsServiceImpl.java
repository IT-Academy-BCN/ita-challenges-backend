package com.itachallenge.gamification.service;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PointsServiceImpl implements PointsService {

    private final UserScoreRepository userScoreRepository;

    public PointsServiceImpl(UserScoreRepository userScoreRepository) {
        this.userScoreRepository = userScoreRepository;
    }

    @Override
    public Mono<Void> recordPoints(UUID userId, UUID challengeId, int points) {
        UserScoreDocument doc = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId)
                .points(points)
                .createdAt(LocalDateTime.now())
                .build();
        return userScoreRepository.save(doc).then();
    }
}
