package com.itachallenge.gamification.service;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionScoreRecorderImplTest {

    @Mock
    private UserScoreRepository userScoreRepository;

    private SubmissionScoreRecorderImpl submissionScoreRecorder;

    @BeforeEach
    void setup() {
        submissionScoreRecorder = new SubmissionScoreRecorderImpl(userScoreRepository);
    }

    @Test
    void shouldPersistCompletedSubmissionScoreWithExpectedFields() {

        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        when(userScoreRepository.save(any(UserScoreDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(submissionScoreRecorder.recordCompletedSubmissionScore(userId, challengeId))
                .verifyComplete();

        ArgumentCaptor<UserScoreDocument> captor = ArgumentCaptor.forClass(UserScoreDocument.class);
        verify(userScoreRepository).save(captor.capture());

        UserScoreDocument savedDocument = captor.getValue();

        assertEquals(userId, savedDocument.getUserId());
        assertEquals(challengeId, savedDocument.getChallengeId());
        assertEquals(ActivityType.CHALLENGE_COMPLETED.getPoints(), savedDocument.getPointsEarned());
        assertNull(savedDocument.getUsername());


    }

}