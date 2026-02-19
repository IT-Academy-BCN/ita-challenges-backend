package com.itachallenge.challenge.config.dbchangelog;

public final class MongockTestContainer {

    private static final String ENV_MONGODB_URI = "MONGODB_URI";

    private MongockTestContainer() {}

    public static String getMongoUri() {
        String external = System.getenv(ENV_MONGODB_URI);
        if (external == null || external.isBlank()) {
            throw new IllegalStateException(
                    "MONGODB_URI is not set. " +
                            "For CI it is provided by GitHub Actions. " +
                            "For local tests, start MongoDB (docker compose / local) and export MONGODB_URI."
            );
        }
        return external;
    }
}
