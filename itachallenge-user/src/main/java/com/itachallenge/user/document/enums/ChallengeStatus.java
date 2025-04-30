package com.itachallenge.user.document.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChallengeStatus {ENDED("ENDED"),
    IN_PROGRESS("IN_PROGRESS");

    private final String value;

    ChallengeStatus(String value) {
        this.value = value;
    }

    public static ChallengeStatus challengeStatusFromString(String status) {
        ChallengeStatus output = null;
        if (status != null) {
            output = Arrays.stream(ChallengeStatus.values())
                    .filter(s -> status.equalsIgnoreCase(s.getValue()))
                    .findFirst()
                    .orElse(null);
        }
        return output;
    }

}
