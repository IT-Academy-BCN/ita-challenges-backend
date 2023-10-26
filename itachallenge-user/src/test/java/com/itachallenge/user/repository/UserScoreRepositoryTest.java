package com.itachallenge.user.repository;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserScoreRepositoryTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withExposedPorts(27017)
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("users"));
    }

    @Autowired
    private IUserSolutionRepository userScoreRepository;

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120001");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120002");
    UUID uuid_3 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    UUID userId1 = UUID.fromString("442b8e6e-5d57-4d12-9be2-3ff4f26e7d79");
    UUID userId2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    @BeforeEach
    public void setUp() {

        userScoreRepository.deleteAll().block();

        UUID challengeId1 = UUID.fromString("7fc6a737-dc36-4e1b-87f3-120d81c548aa");
        UUID challengeId2 = UUID.fromString("5c1a97e5-1cca-4144-9981-2de1fb73b178");

        UUID languageId1 = UUID.fromString("1e047ea2-b787-49e7-acea-d79e92be3909");
        UUID languageId2 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");

        UUID solutionId1 = UUID.fromString("1e047ea2-b787-49e7-acea-d79e92be3909");
        UUID solutionId2 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");

        String solutionText1 = "Ipsum.. 1";
        String solutionText2 = "Ipsum.. 2";

        SolutionDocument solutionDocument1 = new SolutionDocument(solutionId1, solutionText1);
        SolutionDocument solutionDocument2 = new SolutionDocument(solutionId2, solutionText2);

        List<SolutionDocument> solutionDocumentList = List.of(solutionDocument1, solutionDocument2);

        UserSolutionDocument userScoreDocument1 = new UserSolutionDocument(uuid_1, userId1, challengeId1, languageId1, true, "medium", 90, solutionDocumentList);
        UserSolutionDocument userScoreDocument2 = new UserSolutionDocument(uuid_2, userId2, challengeId2, languageId2, true, "medium", 90, solutionDocumentList);
        UserSolutionDocument userScoreDocument3 = new UserSolutionDocument(uuid_3, userId1, challengeId1, languageId1, true, "medium", 90, solutionDocumentList);

        userScoreRepository.saveAll(Flux.just(userScoreDocument1, userScoreDocument2, userScoreDocument3)).blockLast();
    }

    @DisplayName("Repository not null Test")
    @Test
    void testDB(){
        assertNotNull(userScoreRepository);
    }

    @DisplayName("Count users by id")
    @Test
    void CountUserById(){
        Flux<UserSolutionDocument> userScoreDocTest = userScoreRepository.findByUserId(userId1);

        StepVerifier.create(userScoreDocTest)
                .expectNextCount(2)
                .verifyComplete();
    }
}