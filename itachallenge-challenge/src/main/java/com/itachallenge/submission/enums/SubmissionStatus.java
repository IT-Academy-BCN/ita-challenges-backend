package com.itachallenge.submission.enums;

import com.itachallenge.common.exception.BadRequestException;
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

    public static SubmissionStatus fromString(String status) {
        if (status == null || status.isBlank()) {
            throw new BadRequestException("status is required");
        }

        return Arrays.stream(SubmissionStatus.values())
                .filter(s -> status.equalsIgnoreCase(s.getValue()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid status: " + status));
    }
}
