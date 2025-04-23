package com.itachallenge.user.document.enums;

import lombok.Getter;

@Getter
public enum ChallengeStatus {ENDED("ENDED");

    private final String value;

    ChallengeStatus(String value) {
        this.value = value;
    }

    public static ChallengeStatus determineChallengeStatus(String status){
        return status != null  && status.equalsIgnoreCase(ChallengeStatus.ENDED.getValue()) ?
                ChallengeStatus.ENDED : null;
    }

}
