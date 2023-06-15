package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.dtos.ChallengeDto;

public interface ChallengeDocConverter {

    ChallengeDto toChallengeDto(Float percentage, Integer popularity);
}