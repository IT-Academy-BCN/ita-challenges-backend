package com.itachallenge.challenge.config.dbchangelog;

import org.testcontainers.containers.MongoDBContainer;

import java.time.Duration;

public final class MongockTestContainer {

    private static final MongoDBContainer MONGO =
            new MongoDBContainer("mongo:7.0.8")
                    .withStartupAttempts(1)
                    .withStartupTimeout(Duration.ofSeconds(180))
                    .withReuse(false);

    static {
        MONGO.start();
    }

    private MongockTestContainer() {}

    public static MongoDBContainer getMongo() {
        return MONGO;
    }
}
