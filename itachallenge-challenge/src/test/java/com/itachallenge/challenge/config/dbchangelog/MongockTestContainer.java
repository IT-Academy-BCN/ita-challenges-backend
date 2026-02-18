package com.itachallenge.challenge.config.dbchangelog;

import org.testcontainers.containers.MongoDBContainer;

import java.time.Duration;

public final class MongockTestContainer {

    private static final String ENV_MONGODB_URI = "MONGODB_URI";
    private static MongoDBContainer mongo; // lazy

    private MongockTestContainer() {}

    /**
     * Returns an external MongoDB URI if provided (CI), otherwise starts/returns a Testcontainers Mongo.
     */
    public static String getMongoUri() {
        String external = System.getenv(ENV_MONGODB_URI);
        if (external != null && !external.isBlank()) {
            return external;
        }
        return getMongo().getReplicaSetUrl();
    }

    /**
     * Lazy-start MongoDBContainer only when actually needed (local).
     */
    public static synchronized MongoDBContainer getMongo() {
        if (mongo == null) {
            mongo = new MongoDBContainer("mongo:7.0.8")
                    .withStartupAttempts(1)
                    .withStartupTimeout(Duration.ofSeconds(180))
                    .withReuse(false);
            mongo.start();
        }
        return mongo;
    }
}
