package com.itachallenge.challenge.service;

import com.itachallenge.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    public boolean removeResource(String idResource) {
        //TODO when resource service can be accessible add a if(resourceService.existsByID()) and return true or false depending on it
        challengeRepository.removeResourceFromAllChallenges(idResource);
        /*
        @Query("{$pull: {resources: {id: ?0}}}")
        void removeResourceFromAllChallenges(String resourceId);
         */
        return true;
    }
}
