package com.itachallenge.challenge.config.dbchangelog;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public final class MongockTestContainer {

    private static MongoDBContainer mongo;

    private MongockTestContainer() {}

    public static synchronized String getMongoUri() {

        String uri = System.getenv("MONGODB_URI");
        if (uri != null && !uri.isBlank()) {
            return uri;
        }

        if (mongo == null) {
            mongo = new MongoDBContainer(
                    DockerImageName.parse("mongo:7.0.8")
            );
            mongo.start();
        }

        return mongo.getReplicaSetUrl();
    }

    public static String getMongoUri(String dbName) {
        String base = getMongoUri();
        return base.endsWith("/") ? base + dbName : base + "/" + dbName;
    }
}
