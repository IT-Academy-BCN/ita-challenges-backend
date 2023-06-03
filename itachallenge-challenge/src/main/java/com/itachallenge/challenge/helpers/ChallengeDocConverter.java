package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.dtos.ChallengeDto;

public interface ChallengeDocConverter {

    ChallengeDto toChallengeDtoWithOnlyBasicInfo(Float percentage, Integer popularity);
}