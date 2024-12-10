package com.itachallenge.score.domain;

import lombok.Getter;
@Getter
public enum ScoreResult {
    COMPILATION_ERROR(0, "Compilation failed"),
    PARTIAL_SUCCESS(25, "Code compiled, but tests did not pass"),
    INCOMPLETE_SUCCESS(50, "Code compiled, but not all tests passed"),
    FULL_SUCCESS(75, "Code compiled and executed successfully, all tests passed");

    private final int score;
    private final String description;

    // Constructor to initialize the score and description
    ScoreResult(int score, String description) {
        this.score = score;
        this.description = description;
    }

    /**
     * This method takes the terminal output from the container and maps it to a ScoreResult.
     */
    public static ScoreResult fromTerminalOutput(String output) {
        if (output.contains("status 0")) {
            return COMPILATION_ERROR;
        } else if (output.contains("status 1")) {
            return PARTIAL_SUCCESS;
        } else if (output.contains("status 2")) {
            return INCOMPLETE_SUCCESS;
        } else if (output.contains("status 3")) {
            return FULL_SUCCESS;
        } else {
            throw new IllegalArgumentException("Unexpected output: " + output);
        }
    }
}

