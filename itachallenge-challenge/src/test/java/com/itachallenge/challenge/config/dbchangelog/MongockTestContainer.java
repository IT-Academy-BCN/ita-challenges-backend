package com.itachallenge.challenge.config.dbchangelog;

import com.itachallenge.challenge.integration.AbstractMongoIntegrationTest;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class MongockTestContainer extends AbstractMongoIntegrationTest {

    private ReactiveMongoTemplate reactiveMongoTemplate;
    private MongoClient mongoClient;

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @BeforeEach
    void setUp() {
        mongoClient = MongoClients.create(mongo.getReplicaSetUrl("challenges"));
        reactiveMongoTemplate = new ReactiveMongoTemplate(mongoClient, "challenges");

        databaseInitializer.createCollection(mongoClient.getDatabase("challenges"));
        databaseInitializer.execution(reactiveMongoTemplate);
    }

    @Test
    void testCollectionCreation() {
        databaseInitializer.execution(reactiveMongoTemplate);

        String collectionName = reactiveMongoTemplate.getCollection("mongockDemo")
                .map(collection -> collection.getNamespace().getCollectionName())
                .block();

        assertEquals("mongockDemo", collectionName, "The collection name should be mongockDemo");
    }

    @Test
    void testDocumentCreation() {
        String actualCollectionName = reactiveMongoTemplate.getCollection("mongockDemo")
                .map(collection -> collection.getNamespace().getCollectionName())
                .block();

        assertNotNull(actualCollectionName, "The collection name should not be null");
        assertEquals("mongockDemo", actualCollectionName, "The collection name should be 'mongockDemo'");

        reactiveMongoTemplate.getCollection("mongockDemo")
                .flatMapMany(collection -> collection.find().first())
                .doOnNext(document -> {
                    assertNotNull(document, "The document should not be null");
                    assertNotNull(document.get("language_name"), "The field 'language_name' should exist");
                })
                .blockLast();
    }

    @Test
    void testUpdateOperation() {
        String actualCollectionName = reactiveMongoTemplate.getCollection("mongockDemo")
                .map(collection -> collection.getNamespace().getCollectionName())
                .block();

        assertNotNull(actualCollectionName, "The collection name should not be null");
        assertEquals("mongockDemo", actualCollectionName, "The collection name should be 'mongockDemo'");

        databaseInitializer.execution(reactiveMongoTemplate);

        reactiveMongoTemplate.getCollection(actualCollectionName)
                .flatMapMany(collection -> collection.find().first())
                .doOnNext(document -> {
                    assertNotNull(document, "The document should not be null");
                    assertTrue(document.containsKey("language_name_updated"),
                            "The field 'language_name_updated' should exist");
                })
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reactiveMongoTemplate.collectionExists("mongockDemo")
                .flatMap(exists -> exists ? reactiveMongoTemplate.dropCollection("mongockDemo")
                        : reactiveMongoTemplate.getMongoDatabase())
                .onErrorResume(e -> reactiveMongoTemplate.getMongoDatabase())
                .block();
    }
}
