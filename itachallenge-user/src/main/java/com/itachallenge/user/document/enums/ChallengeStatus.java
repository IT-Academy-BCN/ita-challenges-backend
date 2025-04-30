package com.itachallenge.user.document.enums;

import lombok.Getter;

@Getter
public enum ChallengeStatus {ENDED("ENDED"),
    IN_PROGRESS("IN_PROGRESS");

    private final String value;

    ChallengeStatus(String value) {
        this.value = value;
    }

    public static ChallengeStatus determineChallengeStatus(String status){
        ChallengeStatus output = null;
        if(status != null  && status.equalsIgnoreCase(ChallengeStatus.ENDED.getValue())){
            output = ChallengeStatus.ENDED;
        } else if (status != null && status.equalsIgnoreCase(ChallengeStatus.IN_PROGRESS.getValue())) {
            output = ChallengeStatus.IN_PROGRESS;
        }
        return output;
    }

}
