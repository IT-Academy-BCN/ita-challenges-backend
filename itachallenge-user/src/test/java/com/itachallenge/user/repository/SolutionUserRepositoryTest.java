package com.itachallenge.user.repository;

import com.itachallenge.user.document.SolutionUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.UUID;

import static org.junit.Assert.*;


@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class SolutionUserRepositoryTest {
    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("user"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("user"));
    }

    @Autowired
    private SolutionUserRepository solutionUserRepository;

    @DisplayName("Repository not null Test")
    @Test
    void testDB() {

        assertNotNull(solutionUserRepository);

    }

    @Test
    void saveAndRetrieveSolutionUser() {

        UUID uuidSolutionUser = UUID.randomUUID();
        UUID uuidUser = UUID.randomUUID();
        UUID uuidChallenge = UUID.randomUUID();
        UUID uuidLanguage = UUID.randomUUID();
        String solution = "My Solution";

        SolutionUser solutionUser = new SolutionUser(uuidSolutionUser,uuidUser,uuidChallenge,uuidLanguage,solution);

        solutionUserRepository.save(solutionUser).block();

        SolutionUser retrievedSolutionUser = solutionUserRepository.findById(solutionUser.getUuid_solutionUser()).block();

        Assertions.assertNotNull(retrievedSolutionUser);
        Assertions.assertEquals(solutionUser.getUuid_solutionUser(), retrievedSolutionUser.getUuid_solutionUser());
    }

}