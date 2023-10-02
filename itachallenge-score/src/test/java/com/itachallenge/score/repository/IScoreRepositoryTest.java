package com.itachallenge.score.repository;


import com.itachallenge.score.document.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@PropertySource("classpath:persistence-test.properties")
class IScoreRepositoryTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
        .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("itac-scores"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("itac-scores"));
    }

    @Autowired
    private IScoreRepository scoreRepository;

    private Score score1, score2, score3;
    private UUID scoreID1, scoreID2, scoreID3;
    private UUID userID1, userID2, userID3;
    private UUID challengeID1, challengeID2, challengeID3;
    private UUID solutionID1, solutionID2, solutionID3;

    @BeforeEach
    public void setup() {
        String uuidString = "1020e1e1-e6b2-4c20-817b-70193b518b3f";
        scoreID1 = UUID.fromString(uuidString);
        String uuidString2 = "2020e1e1-e6b2-4c20-817b-70193b518b3f";
        scoreID2 = UUID.fromString(uuidString2);

        String uuidString3 = "3020e1e1-e6b2-4c20-817b-70193b518b3f";
        userID1 = UUID.fromString(uuidString3);
        String uuidString4 = "4020e1e1-e6b2-4c20-817b-70193b518b3f";
        userID2 = UUID.fromString(uuidString4);

        String uuidString5 = "5020e1e1-e6b2-4c20-817b-70193b518b3f";
        challengeID1 = UUID.fromString(uuidString5);
        String uuidString6 = "6020e1e1-e6b2-4c20-817b-70193b518b3f";
        challengeID2 = UUID.fromString(uuidString6);

        String uuidString7 = "7020e1e1-e6b2-4c20-817b-70193b518b3f";
        solutionID1 = UUID.fromString(uuidString7);
        String uuidString8 = "8020e1e1-e6b2-4c20-817b-70193b518b3f";
        solutionID2 = UUID.fromString(uuidString8);

        score1 = Score.builder()
                .scoreID(scoreID1)
                .userID(userID1)
                .challengeID(challengeID1)
                .solutionID(solutionID1).build();

        score2 = Score.builder()
                .scoreID(scoreID2)
                .userID(userID2)
                .challengeID(challengeID2)
                .solutionID(solutionID2).build();

        scoreRepository.deleteAll().block();
        scoreRepository.saveAll(Flux.just(score1, score2)).blockLast();
    }
    @Test
    void testFindByScoreID() {

        Mono<Score> scoreFound = scoreRepository.findByScoreID(scoreID1);
        scoreFound.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getScoreID(), scoreID1),
                () -> fail("Score with ID " + scoreID1 + " not found"));

        Mono<Score> score2Found = scoreRepository.findByScoreID(scoreID2);
        score2Found.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getScoreID(), scoreID2),
                () -> fail("Score with ID " + scoreID2 + " not found"));
    }

    @Test
    void testExistsByScoreID() {
        Boolean exists = scoreRepository.existsByScoreID(scoreID1).block();
        assertEquals(true, exists);

        Boolean exists2 = scoreRepository.existsByScoreID(scoreID2).block();
        assertEquals(true, exists2);
    }

    @Test
    void testExistsByUserID() {
        Boolean exists = scoreRepository.existsByUserID(userID1).block();
        assertEquals(true, exists);

        Boolean exists2 = scoreRepository.existsByUserID(userID2).block();
        assertEquals(true, exists2);
    }

    @Test
    void testScoreSaved() {
        String uuidString12 = "1220e1e1-e6b2-4c20-817b-70193b518b3f";
        scoreID3 = UUID.fromString(uuidString12);
        String uuidString13 = "4020e1e1-e6b2-4c20-817b-70193b518b3f";
        userID3 = UUID.fromString(uuidString13);
        String uuidString14 = "1320e1e1-e6b2-4c20-817b-70193b518b3f";
        challengeID3 = UUID.fromString(uuidString14);
        String uuidString15 = "1420e1e1-e6b2-4c20-817b-70193b518b3f";
        solutionID3 = UUID.fromString(uuidString15);

        score3 = Score.builder()
                .scoreID(scoreID3)
                .userID(userID3)
                .challengeID(challengeID3)
                .solutionID(solutionID3).build();

        Mono<Score> savedResult = scoreRepository.save(score3);

        StepVerifier.create(savedResult).expectNextMatches(savedScore -> {
            assertEquals(scoreID3, savedScore.getScoreID());
            assertEquals(userID3, savedScore.getUserID());
            assertEquals(challengeID3, savedScore.getChallengeID());
            assertEquals(solutionID3, savedScore.getSolutionID());
            return true;
        }).verifyComplete();
    }
}