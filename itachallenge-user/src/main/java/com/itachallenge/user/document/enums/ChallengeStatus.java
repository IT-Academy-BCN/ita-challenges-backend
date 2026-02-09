package com.itachallenge.user.document.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChallengeStatus {
    SUBMITTED_COMPLETE("SUBMITTED_COMPLETE"),
    IN_PROGRESS("IN_PROGRESS"),
    SUBMITTED_INCOMPLETE("SUBMITTED_INCOMPLETE");

    private final String value;

    ChallengeStatus(String value) {
        this.value = value;
    }



}
