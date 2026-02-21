package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class UserScoreRepositoryTest {

    @Autowired
    private UserScoreRepository userScoreRepository;

    @BeforeEach
    void setUp() {
        userScoreRepository.deleteAll().block();
    }

    @Test
    void givenExistingScores_whenFindByUsername_thenReturnsSortedByAscendingDates() {

        String username = "Pepito";
        UserScoreDocument score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .username(username)
                .points(3)
                .createdAt(LocalDateTime.now())
                .build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .username(username)
                .points(1)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        UserScoreDocument score3 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .username(username)
                .points(2)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        userScoreRepository.saveAll(List.of(score1, score2, score3)).blockLast();

        StepVerifier.create(userScoreRepository.findByUsernameOrderByCreatedAtAsc(username))
                .expectNextMatches(s -> s.getPoints() == 1)
                .expectNextMatches(s -> s.getPoints() == 2)
                .expectNextMatches(s -> s.getPoints() == 3)
                .verifyComplete();
    }
}
