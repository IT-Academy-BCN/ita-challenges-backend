package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
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

        UserScoreDocument user1Score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId1)
                .username("old_username")
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(now.minusDays(2))
                .build();

        UserScoreDocument user1Score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId1)
                .username(USERNAME_1)
                .challengeId(UUID.randomUUID())
                .pointsEarned(15)
                .createdAt(now.minusDays(1))
                .build();

        UserScoreDocument user1Score3 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId1)
                .username(USERNAME_1)
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(now)
                .build();

        UserScoreDocument user2Score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId2)
                .username(USERNAME_2)
                .challengeId(UUID.randomUUID())
                .pointsEarned(20)
                .createdAt(LocalDateTime.now())
                .build();

        UserScoreDocument user2Score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId2)
                .username(USERNAME_2)
                .challengeId(UUID.randomUUID())
                .pointsEarned(5)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        UserScoreDocument user3Score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId3)
                .username(USERNAME_3)
                .challengeId(UUID.randomUUID())
                .pointsEarned(50)
                .createdAt(LocalDateTime.now())
                .build();

        userScoreRepository.saveAll(Flux.just(
                user1Score1, user1Score2, user1Score3,
                user2Score1, user2Score2,
                user3Score1
        )).blockLast();
    }

    @Test
    void givenUserWithMultipleScores_whenFindByUserIdOrderByCreatedAtAsc_thenReturnsInAscendingOrder() {
        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId1))
                .expectNextMatches(doc -> doc.getPointsEarned() == 10) // minusDays(2)
                .expectNextMatches(doc -> doc.getPointsEarned() == 15) // minusDays(1)
                .expectNextMatches(doc -> doc.getPointsEarned() == 10) // now
                .verifyComplete();
    }
    @SuppressWarnings("java:S2699") // to be extended with activityType when #275/#276 are merged
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
}
