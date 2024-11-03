package com.itachallenge.user.dtos.zmq;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.UUID;

public class ScoreRequestDto {
    @JsonProperty(value = "parameters", index = 0)
    private Map<String, Object> parameters;

    private UUID userId;
    private UUID challengeId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(UUID challengeId) {
        this.challengeId = challengeId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}