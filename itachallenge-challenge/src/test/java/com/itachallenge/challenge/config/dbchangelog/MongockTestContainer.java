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
     * Works with:
     * - mongodb://host:27017/test?directConnection=true
     * - mongodb://host:27017/?directConnection=true
     * - mongodb://host:27017 (no db)
     *
     * and produces:
     * - mongodb://host:27017/<dbName>?directConnection=true
     */
    public static String getMongoUri(String dbName) {
        String uri = getMongoUri();
        if (dbName == null || dbName.isBlank()) return uri;

        int q = uri.indexOf('?');
        String base = (q >= 0) ? uri.substring(0, q) : uri;
        String query = (q >= 0) ? uri.substring(q) : "";

        int schemeIdx = base.indexOf("://");
        int afterScheme = schemeIdx >= 0 ? schemeIdx + 3 : 0;

        int pathSlash = base.indexOf('/', afterScheme);

        if (pathSlash < 0) {
            base = base + "/" + dbName;
        } else {
            base = base.substring(0, pathSlash + 1) + dbName;
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
