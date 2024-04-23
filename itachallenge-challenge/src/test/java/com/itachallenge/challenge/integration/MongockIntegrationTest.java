package com.itachallenge.challenge.integration;

import com.itachallenge.challenge.config.dbchangelog.DatabaseInitializer;
import com.itachallenge.challenge.document.LanguageDocument;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.modelmapper.internal.bytebuddy.matcher.ElementMatchers.any;

@SpringBootTest
//@ActiveProfiles("test")
@Testcontainers
public class MongockIntegrationTest {

    @Mock
    private MongoDatabase mongoDatabase;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @InjectMocks
    DatabaseInitializer databaseInitializer;

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("challenges"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());
        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("challenges"));
    }

    @BeforeEach
    void setUp() {
        Publisher<Void> publisher = Mono.empty();
        when(mongoDatabase.createCollection(anyString())).thenReturn(publisher);
        when(reactiveMongoTemplate.save(ArgumentMatchers.any(LanguageDocument.class), ArgumentMatchers.any())).thenReturn(Mono.just(new LanguageDocument()));

        databaseInitializer.createCollection(mongoDatabase);
        databaseInitializer.execution(reactiveMongoTemplate);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void testDatabaseConnectionAndInitialization() {
        System.out.println(mongoTemplate.collectionExists("mongockTest"));
        //assertTrue(mongoTemplate.collectionExists("mongockTest"));
        //assertTrue(mongoTemplate.collectionExists("mongockChangeLog"));
        //assertTrue(mongoTemplate.collectionExists("mongockLock"));

    }

}
