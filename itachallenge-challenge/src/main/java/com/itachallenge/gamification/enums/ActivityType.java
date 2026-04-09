package com.itachallenge.gamification.enums;

public enum ActivityType {
    CODE_REVIEW(5),
    PRESENTATION(10),
    CHALLENGE_COMPLETED(20);

    private final int points;

    ActivityType(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
