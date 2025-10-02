package com.itachallenge.user.document.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChallengeStatus {
    SUBMITTED_COMPLETED("SUBMITTED_COMPLETED"),
    IN_PROGRESS("IN_PROGRESS"),
    SUBMITTED_INCOMPLETE("SUBMITTED_INCOMPLETE");

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
