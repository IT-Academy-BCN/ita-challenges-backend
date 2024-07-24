package com.itachallenge.challenge.config.dbchangelog;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class DataBaseAddDocumentTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10")
            .withExposedPorts(27017)
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl("challenges"));
    }

    private ReactiveMongoTemplate reactiveMongoTemplate;
    private MongoClient mongoClient;

    @Autowired
    private DataBaseAddDocument dataBaseAddDocument;

    @BeforeEach
    void setUp() {
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl("challenges"));
        reactiveMongoTemplate = new ReactiveMongoTemplate(mongoClient, "challenges");
        dataBaseAddDocument.createCollection(mongoClient);
    }

    @Test
    void BeforeExecutionTest() {

        dataBaseAddDocument.createCollection(mongoClient);
        assertEquals("mongockDemo", "mongockDemo", "The collection name should be mongockDemo");
    }

    @Test
    void testExecution() {
        dataBaseAddDocument.execution();

        String expectedCollectionName = "mongockDemo";
        String actualCollectionName = reactiveMongoTemplate.getCollection("mongockDemo")
                .map(collection -> collection.getNamespace().getCollectionName())
                .block();
        assertEquals(expectedCollectionName, actualCollectionName, "The collection name should be mongockDemo");

        reactiveMongoTemplate.getCollection("mongockDemo")
                .flatMapMany(collection -> collection.find().first())
                .doOnNext(document -> {
                    assertNotNull(document, "The document should not be null");
                    assertNotNull(document.get("JAVA"), "The field 'JAVA' should exist");
                })
                .blockLast();
    }

    @Test
    void testRollBackExecution() {
        dataBaseAddDocument.execution();
        dataBaseAddDocument.rollBackExecution();

        reactiveMongoTemplate.getCollection("mongockDemo")
                .flatMapMany(collection -> collection.find().first())
                .doOnNext(document -> fail("The document should have been removed"))
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reactiveMongoTemplate.dropCollection("mongockDemo").block();
    }
}