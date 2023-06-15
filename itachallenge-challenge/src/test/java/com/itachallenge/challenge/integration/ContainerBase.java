package com.itachallenge.challenge.integration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ContainerBase {

    @Container
    public static MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:5.0")
            .withExposedPorts(27017)
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin_challenge")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD","BYBcMJEEWw5egRUo");
}
