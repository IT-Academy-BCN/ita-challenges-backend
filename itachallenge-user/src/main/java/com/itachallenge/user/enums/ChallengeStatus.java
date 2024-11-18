package com.itachallenge.user.enums;

import lombok.Getter;

@Getter
public enum ChallengeStatus {
    STARTED("STARTED"), EMPTY("EMPTY"), ENDED("ENDED"), SENT ("SENT")
    , SCORE_PENDING ("SCORE_PENDING");

    private final String value;

    ChallengeStatus(String value) {
        this.value = value;
    }

}
