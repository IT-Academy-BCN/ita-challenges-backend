package com.itachallenge.gamification.service;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointsServiceImplTest {
    @Mock
    private UserScoreRepository userScoreRepository;

    @InjectMocks
    private PointsServiceImpl pointsService;

    @Test
    void recordPoints_shouldSaveDocumentWithUserIdChallengeIdPointsAndCreatedAt() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        int points = 10;

        when(userScoreRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(false));
        when(userScoreRepository.save(any(UserScoreDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(pointsService.recordPoints(userId, challengeId, points))
                .verifyComplete();

        ArgumentCaptor<UserScoreDocument> captor = ArgumentCaptor.forClass(UserScoreDocument.class);
        verify(userScoreRepository).save(captor.capture());

        UserScoreDocument saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getChallengeId()).isEqualTo(challengeId);
        assertThat(saved.getPoints()).isEqualTo(points);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getId()).isNull();
    }

    @Test
    void recordPoints_shouldNotSaveWhenAlreadyExists() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        when(userScoreRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(true));

        StepVerifier.create(pointsService.recordPoints(userId, challengeId, 10))
                .verifyComplete();

        verify(userScoreRepository, never()).save(any(UserScoreDocument.class));
    }
    @Test
    void recordPoints_shouldPropagateErrorWhenRepositoryFails() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        when(userScoreRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(false));
        when(userScoreRepository.save(any(UserScoreDocument.class)))
                .thenReturn(Mono.error(new RuntimeException("mongo down")));

        StepVerifier.create(pointsService.recordPoints(userId, challengeId, 10))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException &&
                                ex.getMessage().contains("mongo down"))
                .verify();

        verify(userScoreRepository).save(any(UserScoreDocument.class));
    }
}
