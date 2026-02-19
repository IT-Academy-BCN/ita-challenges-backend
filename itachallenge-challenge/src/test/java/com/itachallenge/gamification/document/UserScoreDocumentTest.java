package com.itachallenge.gamification.document;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserScoreDocumentTest {

    @Test
    void givenValidData_whenUserDocIsBuild_thenUserDocIsCorrectlyCreated() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        int points = 10;
        LocalDateTime createdAt = LocalDateTime.now();

        UserScoreDocument document = UserScoreDocument.builder()
                .id(id)
                .userId(userId)
                .challengeId(challengeId)
                .points(points)
                .createdAt(createdAt)
                .build();

        assertThat(document.getId()).isEqualTo(id);
        assertThat(document.getUserId()).isEqualTo(userId);
        assertThat(document.getChallengeId()).isEqualTo(challengeId);
        assertThat(document.getPoints()).isEqualTo(points);
        assertThat(document.getCreatedAt()).isEqualTo(createdAt);
    }
}
