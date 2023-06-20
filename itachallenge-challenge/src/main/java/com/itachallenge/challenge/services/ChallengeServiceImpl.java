package com.itachallenge.challenge.services;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChallengeServiceImpl implements ChallengeService {


	public Set<UUID>getRelatedChallenge(UUID challenge_id) {
		
		Set<UUID> challengerelated= new HashSet<UUID>();
		challengerelated.add(UUID.fromString("40728c9c-a557-4d12-bf8f-3747d0924197"));
		challengerelated.add(UUID.fromString("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0"));
		challengerelated.add(UUID.fromString("5f71e51d-1e3e-44a2-bc97-158021f1a344"));
	
		
		return challengerelated;
	}
}
