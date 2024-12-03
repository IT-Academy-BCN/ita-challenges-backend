package com.itachallenge.score.util;

import lombok.Getter;

@Getter
public enum ScoreResult {
    COMPILATION_ERROR(0, "Compilation failed"),
    PARTIAL_SUCCESS(50, "Code compiled but contains errors"),
    FULL_SUCCESS(75, "Code compiled and executed successfully");

    private final int score;
    private final String description;

    ScoreResult(int score, String description) {
        this.score = score;
        this.description = description;
    }

    public static ScoreResult fromTerminalOutput(String output) {
        if (output.contains("Score: 0")) {
            return COMPILATION_ERROR;
        } else if (output.contains("Score: 50")) {
            return PARTIAL_SUCCESS;
        } else if (output.contains("Score: 75")) {
            return FULL_SUCCESS;
        } else {
            throw new IllegalArgumentException("Unexpected output: " + output);
        }
    }
}

