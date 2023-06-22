package com.itachallenge.challenge.services;


import java.util.Set;
import java.util.UUID;

import com.itachallenge.challenge.dtos.RelatedDto;

public interface IChallengeService {

 
    public Set<RelatedDto> getRelatedChallenge(UUID challenge_id);

}
