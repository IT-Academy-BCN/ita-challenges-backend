package com.itachallenge.user.repository;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

@DataMongoTest
@Testcontainers
@ExtendWith(SpringExtension.class)
class UserSolutionRepositoryZmqTest {

    @Autowired
    private IUserSolutionRepository userSolutionRepository;

    private int score;
    private String errors;
    private UserSolutionDocument userSolutionDoc;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:3.0.6");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    public void setUp() {

        UUID uuid = UUID.randomUUID();
        UUID userUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UUID challengeUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        UUID languageUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
        score = 99;
        errors = "Error test";
        List<SolutionDocument> solutionDocument = List.of(SolutionDocument.builder().solutionText("This is a solution text").build());

        userSolutionDoc = UserSolutionDocument.builder()
                .uuid(uuid)
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .solutionDocument(solutionDocument)
                .build();
    }

    @DisplayName("Should persist score and errors in existing document when receiving data from ZMQ")
    @Test
    void testSaveScoreAndErrorsInExistingDocument() {

        Mono<UserSolutionDocument> savedInitialDocument = userSolutionRepository.save(userSolutionDoc);

        StepVerifier.create(savedInitialDocument)
                .expectNextMatches(doc -> doc.getScore() == 0 && (doc.getErrors() == null || doc.getErrors().isEmpty()))
                .verifyComplete();

        Mono<UserSolutionDocument> existingDocument = userSolutionRepository.findByUuid(userSolutionDoc.getUuid());

        Mono<UserSolutionDocument> updatedDocument = existingDocument
                .map(doc -> {
                    doc.setScore(score);
                    doc.setErrors(errors);
                    return doc;
                })
                .flatMap(userSolutionRepository::save);

        StepVerifier.create(updatedDocument)
                .expectNextMatches(doc -> doc.getScore() == score && doc.getErrors().equals(errors))
                .verifyComplete();

        StepVerifier.create(userSolutionRepository.findByUuid(userSolutionDoc.getUuid()))
                .expectNextMatches(doc -> doc.getScore() == score && doc.getErrors().equals(errors))
                .verifyComplete();
    }
}