package com.itachallenge.submission.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum UserSubmissionAction {
    SAVE,
    GIVE_UP,
    SUBMIT;

    @JsonCreator
    public static UserSubmissionAction fromString(String action) {
        return Arrays.stream(UserSubmissionAction.values())
                .filter(e -> e.name().equalsIgnoreCase(action.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Action must be one of: " + Arrays.stream(values())
                                .map(Enum::name)
                                .collect(Collectors.joining(", "))
                ));
    }
}
