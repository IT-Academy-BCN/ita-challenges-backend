package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.gamification.repository.projection.LeaderboardAggregationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

@DataMongoTest
@Testcontainers(disabledWithoutDocker = true)
class UserScoreRepositoryIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(27017)
            .withReuse(true);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        if (mongoDBContainer.isRunning()) {
            registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        } else {
            registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/test");
        }
    }

    @Autowired
    private UserScoreRepository userScoreRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private UUID userId1, userId2, userId3;
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";
    private static final String USERNAME_3 = "user3";

    @BeforeEach
    void setUp() {
        userScoreRepository.deleteAll().block();
        createTestData();
    }

    @Test
    void givenMultipleUsersScores_whenAggregateUserScores_thenReturnUsersSortedByTotalPointsDesc() {
        Flux<LeaderboardAggregationResult> result = userScoreRepository.aggregateUserScores();

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getUsername().equals(USERNAME_3) && dto.getTotalPoints() == 50)
                .expectNextMatches(agg ->
                        agg.getUsername().equals(USERNAME_1) && agg.getTotalPoints() == 35)
                .expectNextMatches(agg ->
                        agg.getUsername().equals(USERNAME_2) && agg.getTotalPoints() == 25)
                .verifyComplete();
    }

    @Test
    void givenUserWithMultipleUsernames_whenAggregateUserScores_thenReturnMostRecentUsername() {
        Flux<LeaderboardAggregationResult> result = userScoreRepository.aggregateUserScores();

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getUsername().equals(USERNAME_3))
                .expectNextMatches(dto ->
                        dto.getUsername().equals(USERNAME_1))
                .expectNextMatches(dto ->
                        dto.getUsername().equals(USERNAME_2))
                .verifyComplete();
    }

    @Test
    void givenEmptyDatabase_whenAggregateUserScores_thenReturnZero() {
        userScoreRepository.deleteAll().block();

        Flux<LeaderboardAggregationResult> result = userScoreRepository.aggregateUserScores();

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    private void createTestData() {
        LocalDateTime now = LocalDateTime.now();

        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        userId3 = UUID.randomUUID();

        UserScoreDocument user1Score1 = buildScore(userId1, "old_username", 10, now.minusDays(2));
        UserScoreDocument user1Score2 = buildScore(userId1, USERNAME_1, 15, now.minusDays(1));
        UserScoreDocument user1Score3 = buildScore(userId1, USERNAME_1, 10, now);

        UserScoreDocument user2Score1 = buildScore(userId2, USERNAME_2, 20, LocalDateTime.now());
        UserScoreDocument user2Score2 = buildScore(userId2, USERNAME_2, 5, LocalDateTime.now().minusDays(1));

        UserScoreDocument user3Score1 = buildScore(userId3, USERNAME_3, 50, LocalDateTime.now());

        userScoreRepository.saveAll(Flux.just(
                user1Score1, user1Score2, user1Score3,
                user2Score1, user2Score2,
                user3Score1
        )).blockLast();
    }

    @Test
    void givenUserWithMultipleScores_whenFindByUserIdOrderByCreatedAtAsc_thenReturnsInAscendingOrder() {
        userScoreRepository.deleteAll().block();

        UUID testUserId = UUID.randomUUID();

        UserScoreDocument first = buildScore(
                testUserId,
                "testuser",
                10,
                LocalDateTime.of(2024, 3, 1, 10, 0)
        );

        UserScoreDocument second = buildScore(
                testUserId,
                "testuser",
                15,
                LocalDateTime.of(2024, 3, 5, 10, 0)
        );

        UserScoreDocument third = buildScore(
                testUserId,
                "testuser",
                20,
                LocalDateTime.of(2024, 3, 10, 10, 0)
        );

        mongoTemplate.insert(first, "user_score_history").block();
        mongoTemplate.insert(second, "user_score_history").block();
        mongoTemplate.insert(third, "user_score_history").block();

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtAsc(testUserId))
                .expectNextMatches(doc -> doc.getPointsEarned() == 10)
                .expectNextMatches(doc -> doc.getPointsEarned() == 15)
                .expectNextMatches(doc -> doc.getPointsEarned() == 20)
                .verifyComplete();
    }

    @SuppressWarnings("java:S2699")
    @Test
    void givenMultipleUsersScores_whenFindByUserIdOrderByCreatedAtAsc_thenReturnsOnlyRequestedUser() {
        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId1))
                .expectNextCount(3)
                .verifyComplete();

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId2))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void givenUserWithNoScores_whenFindByUserIdOrderByCreatedAtAsc_thenReturnsEmpty() {
        UUID unknownUserId = UUID.randomUUID();

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtAsc(unknownUserId))
                .verifyComplete();
    }

    private UserScoreDocument buildScore(
            UUID userId,
            String username,
            int points,
            LocalDateTime createdAt
    ) {
        return UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .username(username)
                .challengeId(UUID.randomUUID())
                .pointsEarned(points)
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .createdAt(createdAt)
                .build();
    }
}
