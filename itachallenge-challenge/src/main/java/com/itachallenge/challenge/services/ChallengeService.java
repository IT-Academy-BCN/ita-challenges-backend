package com.itachallenge.challenge.services;


import java.util.Set;
import java.util.UUID;

public interface ChallengeService {

    public Set<UUID>getRelatedChallenge(UUID challenge_id);
}