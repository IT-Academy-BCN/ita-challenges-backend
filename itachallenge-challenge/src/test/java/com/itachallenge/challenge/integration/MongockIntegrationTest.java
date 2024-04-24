package com.itachallenge.challenge.integration;

import com.itachallenge.challenge.config.dbchangelog.TestDatabaseInitializer;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("mongockTest")
class MongockIntegrationTest {
    @Mock
    MongoDatabase mongoDatabase;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .waitingFor(Wait.forListeningPort());
    ;

    private final TestDatabaseInitializer testDatabaseInitializer;

    @Autowired
    public MongockIntegrationTest(TestDatabaseInitializer testDatabaseInitializer) {
        this.testDatabaseInitializer = testDatabaseInitializer;
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void testExecutionAndRollback() throws InterruptedException {
        MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");

        // Test execution
        testDatabaseInitializer.createCollection(mongoDatabase);
        Thread.sleep(1000); // wait for 1 second
        List<String> collectionNames = getCollectionNames(mongoDatabase.listCollectionNames());
        assertTrue(collectionNames.contains("MongockTest"));

        // Test rollback
        testDatabaseInitializer.rollbackBeforeExecution(mongoDatabase);
        Thread.sleep(1000); // wait for 1 second
        collectionNames = getCollectionNames(mongoDatabase.listCollectionNames());
        assertFalse(collectionNames.contains("mongockTest"));
    }

    private List<String> getCollectionNames(Publisher<String> publisher) throws InterruptedException {
        List<String> result = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        publisher.subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String t) {
                result.add(t);
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await(); // wait for the operation to complete
        return result;
    }


}
