package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
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
import reactor.core.publisher.Mono;
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

    private UUID userId1;
    private UUID userId2;
    private UUID userId3;
    private static final String OLD_USERNAME = "user";
    private static final String CURRENT_USERNAME = "user1";
    private static final String USERNAME_2 = "user2";
    private static final String USERNAME_3 = "user3";

    @BeforeEach
    void setUp() {
        if (userScoreRepository != null) {
            userScoreRepository.deleteAll().block();
        }
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        userId3 = UUID.randomUUID();

        if (userScoreRepository != null) {
            createTestData();
        }
    }

    private void createTestData() {
        LocalDateTime now = LocalDateTime.now();

        userScoreRepository.deleteAll().block();

        UserScoreDocument user1Score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId1)
                .username(OLD_USERNAME)
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(now.minusDays(2))
                .build();

        UserScoreDocument user1Score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId1)
                .username(CURRENT_USERNAME)
                .challengeId(UUID.randomUUID())
                .pointsEarned(15)
                .createdAt(now.minusDays(1))
                .build();

        UserScoreDocument user1Score3 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId1)
                .username(CURRENT_USERNAME)
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
    void givenMultipleUsersScores_whenAggregateUserScores_thenReturnUsersSortedByTotalPointsDesc() {
        Flux<UserScoreAggregation> result = userScoreRepository.aggregateUserScores(0, 10);

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getUsername().equals(USERNAME_3) && dto.getTotalPoints() == 50)
                .expectNextMatches(agg ->
                        agg.getUsername().equals(CURRENT_USERNAME) && agg.getTotalPoints() == 35)
                .expectNextMatches(agg ->
                        agg.getUsername().equals(USERNAME_2) && agg.getTotalPoints() == 25)
                .verifyComplete();
    }

    @Test
    void givenUserWithMultipleUsernames_whenAggregateUserScores_thenReturnMostRecentUsername() {
        Flux<UserScoreAggregation> result = userScoreRepository.aggregateUserScores(0, 10);

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getUsername().equals(USERNAME_3) &&
                                dto.getUserId() != null &&
                                dto.getUserId().equals(userId3))
                .expectNextMatches(dto ->
                        dto.getUsername().equals(CURRENT_USERNAME) &&
                                dto.getUserId() != null &&
                                dto.getUserId().equals(userId1))
                .expectNextMatches(dto ->
                        dto.getUsername().equals(USERNAME_2) &&
                                dto.getUserId() != null &&
                                dto.getUserId().equals(userId2))
                .verifyComplete();
    }

    @Test
    void givenPaginationParameters_whenAggregateUserScores_thenReturnCorrectPage() {
        Flux<UserScoreAggregation> firstPage = userScoreRepository.aggregateUserScores(0, 2);

        StepVerifier.create(firstPage)
                .expectNextMatches(dto -> dto.getUsername().equals(USERNAME_3))
                .expectNextMatches(dto -> dto.getUsername().equals(CURRENT_USERNAME))
                .verifyComplete();

        Flux<UserScoreAggregation> secondPage = userScoreRepository.aggregateUserScores(2, 2);

        StepVerifier.create(secondPage)
                .expectNextMatches(dto -> dto.getUsername().equals(USERNAME_2))
                .verifyComplete();

        Flux<UserScoreAggregation> thirdPage = userScoreRepository.aggregateUserScores(3, 2);

        StepVerifier.create(thirdPage)
                .verifyComplete();
    }

    @Test
    void givenSkipAndLimitParameters_whenAggregateUserScores_thenRespectPagination() {
        Flux<UserScoreAggregation> result = userScoreRepository.aggregateUserScores(1, 1);

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getUsername().equals(CURRENT_USERNAME) &&
                        dto.getTotalPoints() == 35)
                .verifyComplete();
    }

    @Test
    void givenExistingUsers_whenCountDistinctUsers_thenReturnTotalNumberOfUsers() {
        Mono<Long> count = userScoreRepository.countDistinctUsers();

        StepVerifier.create(count)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void givenUserWithMultipleScores_whenAggregateUserScores_thenSumPointsCorrectly() {
        Flux<UserScoreAggregation> result = userScoreRepository.aggregateUserScores(0, 10);

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getUsername().equals(USERNAME_3) && dto.getTotalPoints() == 50)
                .expectNextMatches(dto ->
                        dto.getUsername().equals(CURRENT_USERNAME) && dto.getTotalPoints() == 35)
                .expectNextMatches(dto ->
                        dto.getUsername().equals(USERNAME_2) && dto.getTotalPoints() == 25)
                .verifyComplete();
    }

    @Test
    void givenEmptyDatabase_whenAggregateUserScores_thenReturnZero() {
        userScoreRepository.deleteAll().block();

        Flux<UserScoreAggregation> result = userScoreRepository.aggregateUserScores(0, 10);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        Mono<Long> count = userScoreRepository.countDistinctUsers()
                .defaultIfEmpty(0L);
        StepVerifier.create(count)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void givenSkipBeyondTotalUsers_whenAggregateUserScores_thenReturnEmpty() {
        Flux<UserScoreAggregation> result = userScoreRepository.aggregateUserScores(5, 10);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void givenEmptyDatabase_whenCountDistinctUsers_thenReturnZero() {
        userScoreRepository.deleteAll().block();

        Mono<Long> count = userScoreRepository.countDistinctUsers()
                .defaultIfEmpty(0L);

        StepVerifier.create(count)
                .expectNext(0L)
                .verifyComplete();
    }
}
