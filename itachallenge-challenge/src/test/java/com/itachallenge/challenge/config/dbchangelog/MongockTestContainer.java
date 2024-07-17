package com.itachallenge.challenge.config.dbchangelog;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@Testcontainers
@ActiveProfiles("mongockTest")
public class MongockTestContainer {

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
    private DatabaseInitializer databaseInitializer = new DatabaseInitializer();

    @BeforeEach
    void setUp() {
        MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        MongoDatabase mongoDatabase = mongoClient.getDatabase("challenges");
        reactiveMongoTemplate = new ReactiveMongoTemplate(mongoClient, "challenges");
        databaseInitializer.createCollection(mongoDatabase);
        databaseInitializer.execution(reactiveMongoTemplate);
    }


    @Test
    void testCollectionCreation() {

        String collectionName = reactiveMongoTemplate.getCollection("mongockDemo")
                .map(collection -> collection.getNamespace().getCollectionName())
                .block();

        reactiveMongoTemplate.collectionExists("mongockDemo")
                .doOnNext(exists -> assertTrue(exists, "The collection mongockDemo should exist"))
                .block();

        assertEquals("mongockDemo", collectionName, "The collection name should be mongockDemo");
    }



    @Test
    void testDocumentCreation() {
        String expectedCollectionName = "mongockDemo";

        reactiveMongoTemplate.getCollection("mongockDemo")
                .map(collection -> collection.getNamespace().getCollectionName())
                .doOnNext(actualCollectionName -> {
                    assertNotNull(actualCollectionName, "The collection name should not be null");
                    assertEquals(expectedCollectionName, actualCollectionName, " The collection name should be 'mongockDemo'");
                })
                .flatMapMany(actualCollectionName -> reactiveMongoTemplate.getCollection(actualCollectionName).flatMapMany(collection -> collection.find().first()))
                .doOnNext(document -> {
                    assertNotNull(document, "The document should not be null");
                    assertNotNull(document.get("language_name"), "The field 'language_name' should exist");
                })
                .blockLast();
    }


    @Test
    void testUpdateOperation() {
        String expectedCollectionName = "mongockDemo";
        String actualCollectionName = reactiveMongoTemplate.getCollection("mongockDemo")
                .map(collection -> collection.getNamespace().getCollectionName())
                .block();

        assertNotNull(actualCollectionName, "El nombre de la colección no debería ser nulo");
        assertEquals(expectedCollectionName, actualCollectionName, "El nombre de la colección debe ser 'mongockDemo'");

        // Perform the update operation or invoke the method that performs the update
        databaseInitializer.execution(reactiveMongoTemplate);

        reactiveMongoTemplate.getCollection(actualCollectionName)
                .flatMapMany(collection -> collection.find().first())
                .doOnNext(document -> {
                    assertNotNull(document, "The document should not be null");
                    assertTrue(document.containsKey("language_name_updated"), "The field 'language_name_updated' should exist");
                })
                .blockLast();
    }



    @AfterEach
    void tearDown() {
        reactiveMongoTemplate.dropCollection("mongockDemo").block();
    }
}

