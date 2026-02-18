package com.itachallenge.challenge.integration;

import com.itachallenge.challenge.config.dbchangelog.MongockTestContainer;
import com.itachallenge.challenge.config.dbchangelog.TestDatabaseInitializer;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("integration")
class MongockIntegrationTest extends AbstractMongoIntegrationTest {

    private final TestDatabaseInitializer testDatabaseInitializer;

    @Autowired
    MongockIntegrationTest(TestDatabaseInitializer testDatabaseInitializer) {
        this.testDatabaseInitializer = testDatabaseInitializer;
    }

    @Test
    void testExecutionAndRollback() {
        MongoClient mongoClient = MongoClients.create(MongockTestContainer.getMongoUri());
        MongoDatabase mongoDatabase = mongoClient.getDatabase("itachallenge_test");

        testDatabaseInitializer.createCollection(mongoDatabase);

        List<String> collectionNames = Flux.from(mongoDatabase.listCollectionNames())
                .collectList()
                .block();

        assertNotNull(collectionNames);
        assertTrue(collectionNames.contains("MongockTest"));

        testDatabaseInitializer.rollbackBeforeExecution(mongoDatabase);

        collectionNames = Flux.from(mongoDatabase.listCollectionNames())
                .collectList()
                .block();

        assertNotNull(collectionNames);
        assertFalse(collectionNames.contains("MongockTest"));
    }
}
