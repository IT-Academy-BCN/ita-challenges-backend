package com.itachallenge.user.enums;

import lombok.Getter;

@Getter
public enum ChallengeStatus {
    STARTED("STARTED"), EMPTY("EMPTY"), SENT("SENT"), SCORE_PENDING("SCORE_PENDING"), ENDED("ENDED");

    private final String value;

    ChallengeStatus(String value) {
        this.value = value;
    }

    public static ChallengeStatus fromValue(String status) {
        for (ChallengeStatus challengeStatus : ChallengeStatus.values()) {
            if (challengeStatus.value.equals(status)) {
                return challengeStatus;
            }
        }
        throw new IllegalArgumentException("Invalid challenge status value: " + status);
    }
}


