package com.itachallenge.submission.enums;

import com.itachallenge.common.exception.BadRequestException;

public enum SubmissionAction {

    SAVE,
    SUBMIT,
    GIVE_UP;

    public static SubmissionAction fromString(String action) {
        try {
            return SubmissionAction.valueOf(action.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid submission action: " + action);
        }
    }

    public SubmissionStatus toStatus() {
        return switch (this) {
            case SAVE -> SubmissionStatus.IN_PROGRESS;
            case SUBMIT -> SubmissionStatus.SUBMITTED_COMPLETE;
            case GIVE_UP -> SubmissionStatus.SUBMITTED_INCOMPLETE;
        };
    }
}
