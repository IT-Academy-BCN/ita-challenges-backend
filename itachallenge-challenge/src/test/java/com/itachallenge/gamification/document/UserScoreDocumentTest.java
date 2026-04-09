package com.itachallenge.gamification.document;

import com.itachallenge.gamification.enums.ActivityType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserScoreDocumentTest {

    @Test
    void givenValidData_whenUserDocIsBuild_thenUserDocIsCorrectlyCreated() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String username = "Pepito";
        UUID challengeId = UUID.randomUUID();
        int points = 10;
        LocalDateTime createdAt = LocalDateTime.now();

        UserScoreDocument document = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .id(id)
                .userId(userId)
                .username(username)
                .challengeId(challengeId)
                .pointsEarned(points)
                .createdAt(createdAt)
                .build();

        assertThat(document.getId()).isEqualTo(id);
        assertThat(document.getUserId()).isEqualTo(userId);
        assertThat(document.getUsername()).isEqualTo(username);
        assertThat(document.getChallengeId()).isEqualTo(challengeId);
        assertThat(document.getPointsEarned()).isEqualTo(points);
        assertThat(document.getCreatedAt()).isEqualTo(createdAt);
    }
}
