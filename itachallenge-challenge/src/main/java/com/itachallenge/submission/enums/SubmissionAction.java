package com.itachallenge.submission.enums;

import com.itachallenge.common.exception.BadRequestException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SubmissionAction {

    SAVE("SAVE"),
    SUBMIT("SUBMIT"),
    GIVE_UP("GIVE_UP");

    private final String value;

    SubmissionAction(String value) {
        this.value = value;
    }

    public static SubmissionAction fromString(String action) {
        if (action == null || action.isBlank()) {
            throw new BadRequestException("action is required");
        }

        return Arrays.stream(SubmissionAction.values())
                .filter(a -> action.equalsIgnoreCase(a.getValue()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid action: " + action));
    }

    public SubmissionStatus toStatus() {
        return switch (this) {
            case SAVE -> SubmissionStatus.IN_PROGRESS;
            case SUBMIT -> SubmissionStatus.SUBMITTED_COMPLETE;
            case GIVE_UP -> SubmissionStatus.SUBMITTED_INCOMPLETE;
        };
    }
}
