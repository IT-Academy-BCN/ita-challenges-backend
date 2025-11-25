package com.itachallenge.user.document.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum SolutionAction {
    SAVE(ChallengeStatus.IN_PROGRESS),
    GIVE_UP(ChallengeStatus.SUBMITTED_INCOMPLETE),
    SUBMIT(ChallengeStatus.SUBMITTED_COMPLETE);

    private final ChallengeStatus mappedStatus;

    SolutionAction(ChallengeStatus mappedStatus) {
        this.mappedStatus = mappedStatus;
    }

    public ChallengeStatus toChallengeStatus() {
        return mappedStatus;
    }

    @JsonCreator
    public static SolutionAction fromString(String action) {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Action cannot be null or blank");
        }
        return Arrays.stream(SolutionAction.values())
                .filter(e -> e.name().equalsIgnoreCase(action.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Action must be one of: " + Arrays.stream(values())
                                .map(Enum::name)
                                .collect(Collectors.joining(", "))
                ));
    }
}