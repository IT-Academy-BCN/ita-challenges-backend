package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.gamification.repository.projection.LeaderboardAggregationResult;
import com.itachallenge.gamification.service.WeeklyWindow;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
    private static final ZoneId LEAGUE_ZONE = ZoneId.of("Europe/Madrid");
    private static final ZonedDateTime REFERENCE_WEEK_DATE_TIME =
            ZonedDateTime.of(2026, 4, 8, 12, 0, 0, 0, LEAGUE_ZONE);

    @BeforeEach
    void setUp() {
        userScoreRepository.deleteAll().block();
        createTestData();
    }

    @Test
    void givenMultipleUsersScores_whenAggregateUserScores_thenReturnUsersSortedByTotalPointsDesc() {
        Flux<LeaderboardAggregationResult> result = userScoreRepository.aggregateUserScores();

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getTotalPoints() == 50)
                .expectNextMatches(agg -> agg.getTotalPoints() == 35)
                .expectNextMatches(agg -> agg.getTotalPoints() == 25)
                .verifyComplete();
    }

    @Test
    void givenScoresWithoutUsername_whenAggregateUserScores_thenReturnResultsWithoutFailing() {
        Flux<LeaderboardAggregationResult> result = userScoreRepository.aggregateUserScores();

        StepVerifier.create(result)
                .expectNextCount(3)
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

    @Test
    void givenEmptyDatabase_whenAggregateUserScoresByPeriod_thenReturnsEmpty() {
        userScoreRepository.deleteAll().block();

        WeeklyWindow window = WeeklyWindow.fromReferenceDateTime(REFERENCE_WEEK_DATE_TIME);
        LocalDateTime from = window.getFromInclusive();
        LocalDateTime to = window.getToInclusive();

        StepVerifier.create(userScoreRepository.aggregateUserScoresByPeriod(from, to))
                .verifyComplete();
    }

    @Test
    void givenScoresInsideAndOutsidePeriod_whenAggregateUserScoresByPeriod_thenReturnsOnlyInsideRange() {
        userScoreRepository.deleteAll().block();

        UUID weeklyUserId = UUID.randomUUID();
        UUID outsideUserId = UUID.randomUUID();
        WeeklyWindow window = WeeklyWindow.fromReferenceDateTime(REFERENCE_WEEK_DATE_TIME);
        LocalDateTime from = window.getFromInclusive();
        LocalDateTime to = window.getToInclusive();

        UserScoreDocument insideFirst = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(weeklyUserId)
                .username("weekly_user")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(25)
                .createdAt(LocalDateTime.of(2026, 4, 8, 10, 0))
                .build();

        UserScoreDocument insideSecond = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(weeklyUserId)
                .username("weekly_user")
                .activityType(ActivityType.CHALLENGE_COMPLETED) 
                .challengeId(UUID.randomUUID())
                .pointsEarned(15)
                .createdAt(LocalDateTime.of(2026, 4, 10, 19, 30))
                .build();

        UserScoreDocument outside = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(outsideUserId)
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .username("outside_user")
                .challengeId(UUID.randomUUID())
                .pointsEarned(99)
                .createdAt(LocalDateTime.of(2026, 4, 20, 12, 0))
                .build();

        // insert() keeps fixed createdAt; save/saveAll triggers @CreatedDate auditing and overwrites dates
        mongoTemplate.insert(insideFirst).block();
        mongoTemplate.insert(insideSecond).block();
        mongoTemplate.insert(outside).block();

        StepVerifier.create(userScoreRepository.aggregateUserScoresByPeriod(from, to))
                .expectNextMatches(agg ->
                        agg.getUsername().equals("weekly_user") && agg.getTotalPoints() == 40)
                .verifyComplete();
    }

    @Test
    void givenScoreAtSundayLastNanosecond_whenAggregateUserScoresByPeriod_thenIncluded() {
        userScoreRepository.deleteAll().block();

        UUID userId = UUID.randomUUID();
        WeeklyWindow window = WeeklyWindow.fromReferenceDateTime(REFERENCE_WEEK_DATE_TIME);
        LocalDateTime from = window.getFromInclusive();
        LocalDateTime to = window.getToInclusive();

        UserScoreDocument atEndOfSunday = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .username("edge_user")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(7)
                .createdAt(to)
                .build();

        mongoTemplate.insert(atEndOfSunday).block();

        StepVerifier.create(userScoreRepository.aggregateUserScoresByPeriod(from, to))
                .expectNextMatches(agg ->
                        agg.getUsername().equals("edge_user") && agg.getTotalPoints() == 7)
                .verifyComplete();
    }

    @Test
    void givenScoresAtSundayEndAndMondayStart_whenAggregateUserScoresByPeriod_thenSundayIncludedAndMondayExcluded() {
        userScoreRepository.deleteAll().block();

        UUID boundaryUserId = UUID.randomUUID();
        WeeklyWindow window = WeeklyWindow.fromReferenceDateTime(REFERENCE_WEEK_DATE_TIME);
        LocalDateTime from = window.getFromInclusive();
        LocalDateTime to = window.getToInclusive();

        UserScoreDocument sundayLastInstant = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(boundaryUserId)
                .username("boundary_user")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(9)
                .createdAt(to)
                .build();

        UserScoreDocument mondayFirstInstant = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(boundaryUserId)
                .username("boundary_user")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(11)
                .createdAt(to.plusNanos(1))
                .build();

        mongoTemplate.insert(sundayLastInstant).block();
        mongoTemplate.insert(mondayFirstInstant).block();

        StepVerifier.create(userScoreRepository.aggregateUserScoresByPeriod(from, to))
                .expectNextMatches(agg ->
                        agg.getUsername().equals("boundary_user") && agg.getTotalPoints() == 9)
                .verifyComplete();
    }

    @Test
    void givenUsersWithSameTotalPoints_whenAggregateUserScores_thenTieBreaksByUsernameAscending() {
        userScoreRepository.deleteAll().block();

        UserScoreDocument alice = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .username("alice")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(20)
                .createdAt(LocalDateTime.of(2026, 4, 8, 10, 0))
                .build();

        UserScoreDocument bob = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .username("bob")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(20)
                .createdAt(LocalDateTime.of(2026, 4, 8, 11, 0))
                .build();

        UserScoreDocument charlie = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .username("charlie")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(LocalDateTime.of(2026, 4, 8, 12, 0))
                .build();

        mongoTemplate.insert(alice).block();
        mongoTemplate.insert(bob).block();
        mongoTemplate.insert(charlie).block();

        StepVerifier.create(userScoreRepository.aggregateUserScores())
                .expectNextMatches(agg ->
                        agg.getUsername().equals("alice") && agg.getTotalPoints() == 20)
                .expectNextMatches(agg ->
                        agg.getUsername().equals("bob") && agg.getTotalPoints() == 20)
                .expectNextMatches(agg ->
                        agg.getUsername().equals("charlie") && agg.getTotalPoints() == 10)
                .verifyComplete();
    }

    @Test
    void givenPeriodUsersWithSameTotalPoints_whenAggregateUserScoresByPeriod_thenTieBreaksByUsernameAscending() {
        userScoreRepository.deleteAll().block();

        WeeklyWindow window = WeeklyWindow.fromReferenceDateTime(REFERENCE_WEEK_DATE_TIME);
        LocalDateTime from = window.getFromInclusive();
        LocalDateTime to = window.getToInclusive();

        UserScoreDocument alice = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .username("alice")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(15)
                .createdAt(from.plusDays(1))
                .build();

        UserScoreDocument bob = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .username("bob")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(15)
                .createdAt(from.plusDays(2))
                .build();

        UserScoreDocument outside = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .username("outside")
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(99)
                .createdAt(to.plusNanos(1))
                .build();

        mongoTemplate.insert(alice).block();
        mongoTemplate.insert(bob).block();
        mongoTemplate.insert(outside).block();

        StepVerifier.create(userScoreRepository.aggregateUserScoresByPeriod(from, to))
                .expectNextMatches(agg ->
                        agg.getUsername().equals("alice") && agg.getTotalPoints() == 15)
                .expectNextMatches(agg ->
                        agg.getUsername().equals("bob") && agg.getTotalPoints() == 15)
                .verifyComplete();
    }

    private void createTestData() {
        LocalDateTime now = LocalDateTime.now();

        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        userId3 = UUID.randomUUID();

        UserScoreDocument user1Score1 = buildScore(userId1, 10, now.minusDays(2));
        UserScoreDocument user1Score2 = buildScore(userId1, 15, now.minusDays(1));
        UserScoreDocument user1Score3 = buildScore(userId1, 10, now);

        UserScoreDocument user2Score1 = buildScore(userId2, 20, LocalDateTime.now());
        UserScoreDocument user2Score2 = buildScore(userId2, 5, LocalDateTime.now().minusDays(1));

        UserScoreDocument user3Score1 = buildScore(userId3, 50, LocalDateTime.now());

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
                10,
                LocalDateTime.of(2024, 3, 1, 10, 0)
        );

        UserScoreDocument second = buildScore(
                testUserId,
                15,
                LocalDateTime.of(2024, 3, 5, 10, 0)
        );

        UserScoreDocument third = buildScore(
                testUserId,
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
            int points,
            LocalDateTime createdAt
    ) {
        return UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .challengeId(UUID.randomUUID())
                .pointsEarned(points)
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .createdAt(createdAt)
                .build();
    }
}
