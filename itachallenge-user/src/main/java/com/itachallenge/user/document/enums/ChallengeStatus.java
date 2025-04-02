package com.itachallenge.user.document.enums;

import lombok.Getter;

@Getter
public enum ChallengeStatus {ENDED("ENDED");

    private final String value;

    ChallengeStatus(String value) {
        this.value = value;
    }

}
