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

    /**
     * Returns the same URI but forcing the database name, keeping query params intact.
     *
     * Example:
     *   mongodb://host:27017/test?directConnection=true
     * becomes:
     *   mongodb://host:27017/challenges?directConnection=true
     */
    public static String getMongoUri(String dbName) {
        String uri = getMongoUri();
        if (dbName == null || dbName.isBlank()) return uri;

        int q = uri.indexOf('?');
        String base = (q >= 0) ? uri.substring(0, q) : uri;
        String query = (q >= 0) ? uri.substring(q) : "";

        int lastSlash = base.lastIndexOf('/');
        if (lastSlash > "mongodb://".length() + 2) {
            base = base.substring(0, lastSlash + 1) + dbName;
        } else {
            base = base + "/" + dbName;
        }

        return base + query;
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
