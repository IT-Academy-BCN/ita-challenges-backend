package com.itachallenge.user.enums;

import lombok.Getter;

@Getter
public enum ChallengeStatus {
    STARTED("STARTED"), EMPTY("EMPTY"), SENDED("SENDED"), SCORE_PENDING("SCORE_PENDING"), ENDED("ENDED");

    private final String value;

    ChallengeStatus(String value) {
        this.value = value;
    }

}
