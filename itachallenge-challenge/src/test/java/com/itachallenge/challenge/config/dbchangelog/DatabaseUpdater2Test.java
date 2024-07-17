package com.itachallenge.challenge.config.dbchangelog;

import com.itachallenge.challenge.document.LanguageDocument;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.eq;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Testcontainers
@ActiveProfiles("mongockTest")
class DatabaseUpdater2Test {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10")
            .withExposedPorts(27017)
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl("challenges"));
    }

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private DatabaseUpdater2 databaseUpdater2 = new DatabaseUpdater2(reactiveMongoTemplate, mock(Logger.class));

    @BeforeEach
    void setUp() {
        MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        reactiveMongoTemplate = new ReactiveMongoTemplate(mongoClient, "challenges");
    }

    @AfterEach
    void tearDown() {
        reactiveMongoTemplate.dropCollection("mongockDemo").block();
    }

    @Test
    void testExecution() { //This test should be throw an exception
        assertThrows(RuntimeException.class, this::execute);

        assertEquals(0, reactiveMongoTemplate.findAll(LanguageDocument.class).count().block());
    }

    @Test
    void testExecutionLogMessages() {

        Logger mockLogger = mock(Logger.class);
        DatabaseUpdater2 updater2 = new DatabaseUpdater2(mock(ReactiveMongoTemplate.class), mockLogger);


        try {
            updater2.execution(MongoClients.create(mongoDBContainer.getReplicaSetUrl()));
        } catch (Exception e) {
            System.out.println("-----------------------------------------------------------");
            System.out.println("Error intentional during execution");
        }
        verify(mockLogger).error(eq("Error occurred during execution"), any(Exception.class));
    }

    private void execute() {
        databaseUpdater2.execution(MongoClients.create(mongoDBContainer.getReplicaSetUrl()));
    }
}

