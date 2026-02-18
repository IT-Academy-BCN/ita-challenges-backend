package com.itachallenge.challenge.integration;

import com.itachallenge.challenge.config.dbchangelog.MongockTestContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractMongoIntegrationTest {
    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MongockTestContainer::getMongoUri);
    }
}
