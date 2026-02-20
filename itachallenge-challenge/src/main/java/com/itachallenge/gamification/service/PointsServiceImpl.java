package com.itachallenge.gamification.service;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final UserScoreRepository userScoreRepository;

    @Override
    public Mono<Void> recordPoints(UUID userId, UUID challengeId, int points) {
        return userScoreRepository.existsByUserIdAndChallengeId(userId, challengeId)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.empty();
                    }

                    UserScoreDocument doc = UserScoreDocument.builder()
                            .userId(userId)
                            .challengeId(challengeId)
                            .points(points)
                            .createdAt(LocalDateTime.now())
                            .build();

                    return userScoreRepository.save(doc).then();
                });
    }
}

