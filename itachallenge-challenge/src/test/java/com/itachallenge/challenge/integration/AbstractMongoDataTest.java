package com.itachallenge.challenge.integration;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataMongoTest
@EnableReactiveMongoRepositories(basePackages = "com.itachallenge.challenge.repository")
@ComponentScan(basePackages = "com.itachallenge.challenge")
public abstract class AbstractMongoDataTest {

    @Container
    protected static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:7.0.8")
                    .withStartupAttempts(3)
                    .withStartupTimeout(java.time.Duration.ofSeconds(60))
                    .withReuse(false);

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

}
