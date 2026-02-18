package com.itachallenge.challenge.integration;

import com.itachallenge.challenge.config.dbchangelog.MongockTestContainer;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@DataMongoTest
@EnableReactiveMongoRepositories(basePackages = "com.itachallenge.challenge.repository")
@ComponentScan(basePackages = "com.itachallenge.challenge")
public abstract class AbstractMongoDataTest {

    protected static final MongoDBContainer mongo = MongockTestContainer.getMongo();

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }
}
