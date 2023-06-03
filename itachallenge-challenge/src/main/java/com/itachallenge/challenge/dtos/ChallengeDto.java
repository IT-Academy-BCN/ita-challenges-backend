package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChallengeDto{

    @JsonProperty(value = "id_challenge", index = 0)
    private UUID challengeId;

    @JsonProperty(index = 1)
    @JsonUnwrapped
    private ChallengeBasicInfoDto basicInfo;

    //private constructor: to force instantiation with custom builder
    private ChallengeDto(UUID challengeId) {
        this.challengeId = challengeId;
    }

    /*
    TODO: ADD more fields "on demand"
     */

    private ChallengeDto() {
        /*
        private no args because:
        required for deserialization. but not needed/used in our logic (at least till now)
         */
    }

    public static ChallengeDtoBuilder builder(UUID challengeId){
        return new ChallengeDtoBuilder(challengeId);
    }

    public static class ChallengeDtoBuilder{
        private ChallengeDto challenge;

        public ChallengeDtoBuilder(UUID id) {
            challenge = new ChallengeDto(id);
        }

        public ChallengeDtoBuilder basicInfo(ChallengeBasicInfoDto basicInfo){
            challenge.basicInfo=basicInfo;
            return this;
        }

        public ChallengeDto build(){
            return challenge;
        }
    }
}