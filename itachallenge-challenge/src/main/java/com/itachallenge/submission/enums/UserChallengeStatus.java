package com.itachallenge.submission.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserChallengeStatus {
    SUBMITTED_COMPLETE("SUBMITTED_COMPLETE"),
    IN_PROGRESS("IN_PROGRESS"),
    SUBMITTED_INCOMPLETE("SUBMITTED_INCOMPLETE");

    private final String value;

    UserChallengeStatus(String value) {
        this.value = value;
    }

    public static UserChallengeStatus challengeStatusFromString(String status) {
        if (status == null) {
            return null;
        }

        return Arrays.stream(UserChallengeStatus.values())
                .filter(s -> status.equalsIgnoreCase(s.getValue()))
                .findFirst()
                .orElse(null);
    }
    }
