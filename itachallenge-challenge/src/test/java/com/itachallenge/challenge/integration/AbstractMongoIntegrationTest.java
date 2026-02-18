package com.itachallenge.challenge.integration;

import com.itachallenge.challenge.config.dbchangelog.MongockTestContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public abstract class AbstractMongoIntegrationTest {

    protected static final MongoDBContainer mongo = MongockTestContainer.getMongo();

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }
}
