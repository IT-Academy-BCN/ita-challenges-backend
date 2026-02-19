package com.itachallenge.challenge.config.dbchangelog;

import org.testcontainers.containers.MongoDBContainer;

import java.time.Duration;

public final class MongockTestContainer {

    private static final String ENV_MONGODB_URI = "MONGODB_URI";
    private static MongoDBContainer mongo; // lazy

    private MongockTestContainer() {}

     public static String getMongoUri() {
        String external = System.getenv(ENV_MONGODB_URI);
        if (external != null && !external.isBlank()) {
            return external;
        }
        return getMongo().getReplicaSetUrl();
    }

    public static String getMongoUri(String dbName) {
        String uri = getMongoUri();
        if (dbName == null || dbName.isBlank()) return uri;
        return uri.endsWith("/") ? uri + dbName : uri + "/" + dbName;
    }

    public static synchronized MongoDBContainer getMongo() {
        if (mongo == null) {
            mongo = new MongoDBContainer("mongo:7.0.8")
                    .withStartupAttempts(1)
                    .withStartupTimeout(Duration.ofSeconds(180))
                    .withReuse(false);
        }
        if (!mongo.isRunning()) {
            mongo.start();
        }
        return mongo;
    }
}
