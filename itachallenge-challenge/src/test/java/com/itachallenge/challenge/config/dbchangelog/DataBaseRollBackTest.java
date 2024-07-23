package com.itachallenge.challenge.config.dbchangelog;

import com.itachallenge.challenge.document.LanguageDocument;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Testcontainers
class DataBaseRollBackTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10")
            .withExposedPorts(27017)
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl("challenges"));
    }

    @Mock
    private Logger mockLogger;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @InjectMocks
    private DataBaseRollback dataBaseRollback;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        reactiveMongoTemplate = new ReactiveMongoTemplate(mongoClient, "challenges");
        reactiveMongoTemplate.createCollection("mongockDemo").block();
        dataBaseRollback = new DataBaseRollback(reactiveMongoTemplate);
    }

    @AfterEach
    void tearDown() {
        reactiveMongoTemplate.dropCollection("mongockDemo").block();
    }


    @DisplayName("This test verify when try @Execution method in DataBaseRollback, works @RollbackExecution")
    @Test
    void testExecution() {
        assertThrows(IllegalArgumentException.class, this::execute);
    }


    private void execute() {
        dataBaseRollback.execution(MongoClients.create(mongoDBContainer.getReplicaSetUrl()));
    }
}
