package com.itachallenge.submission.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SubmissionStatus {
    SUBMITTED_COMPLETE("SUBMITTED_COMPLETE"),
    IN_PROGRESS("IN_PROGRESS"),
    SUBMITTED_INCOMPLETE("SUBMITTED_INCOMPLETE");

    private final String value;

    SubmissionStatus(String value) {
        this.value = value;
    }

    public static SubmissionStatus challengeStatusFromString(String status) {
        if (status == null) {
            return null;
        }

        return Arrays.stream(SubmissionStatus.values())
                .filter(s -> status.equalsIgnoreCase(s.getValue()))
                .findFirst()
                .orElse(null);
    }
}
