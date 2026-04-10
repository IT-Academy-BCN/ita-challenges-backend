package com.itachallenge.gamification.service;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SubmissionScoreRecorderImpl implements SubmissionScoreRecorder {

    private final UserScoreRepository userScoreRepository;

    public SubmissionScoreRecorderImpl(UserScoreRepository userScoreRepository) {
        this.userScoreRepository = userScoreRepository;
    }

    @Override
    public Mono<Void> recordCompletedSubmissionScore(UUID userId, UUID challengeId) {
        UserScoreDocument scoreDocument = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .username(null)
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(challengeId)
                .pointsEarned(ActivityType.CHALLENGE_COMPLETED.getPoints())
                .createdAt(LocalDateTime.now())
                .build();

        return userScoreRepository.save(scoreDocument).then();
    }
}
