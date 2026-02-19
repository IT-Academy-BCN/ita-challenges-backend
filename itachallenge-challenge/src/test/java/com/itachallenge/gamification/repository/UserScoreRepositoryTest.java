package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserScoreRepositoryTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("challenges"));
    }

    @Autowired
    private UserScoreRepository userScoreRepository;

    @BeforeEach
    void setUp() {
        userScoreRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Repository is not null")
    void repositoryShouldNotBeNull() {
        assertNotNull(userScoreRepository);
    }

    @Test
    @DisplayName("Given existing scores, when findByUserIdOrderByCreatedAtDesc then returns sorted by descending dates")
    void givenExistingScores_whenFindByUserId_thenReturnsSortedByDescendingDates() {
        UUID userId = UUID.randomUUID();
<<<<<<< HEAD
=======
        UUID challenge1 = UUID.randomUUID();
        UUID challenge2 = UUID.randomUUID();
        LocalDateTime older = LocalDateTime.now().minusDays(3);
        LocalDateTime newer = LocalDateTime.now();

>>>>>>> 24225070c (creation of userScore Doc & ReÃpo and tests)
        UserScoreDocument score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .username("test-user")
                .challengeId(challenge1)
                .pointsEarned(5)
                .createdAt(older)
                .build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .username("test-user")
                .challengeId(challenge2)
                .pointsEarned(10)
                .createdAt(newer)
                .build();

        userScoreRepository.saveAll(List.of(score1, score2)).blockLast();

        Flux<UserScoreDocument> scoresDesc = userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId);
        StepVerifier.create(scoresDesc)
                .expectNextMatches((UserScoreDocument s) -> s.getPointsEarned() == 10)
                .expectNextMatches((UserScoreDocument s) -> s.getPointsEarned() == 5)
                .verifyComplete();
    }

    @Test
    @DisplayName("Given no scores, when findByUserIdOrderByCreatedAtDesc then returns empty")
    void givenNoScores_whenFindByUserId_thenReturnsEmpty() {
        UUID userId = UUID.randomUUID();

        Flux<UserScoreDocument> scoresDesc = userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId);
        StepVerifier.create(scoresDesc)
                .verifyComplete();
    }
}
