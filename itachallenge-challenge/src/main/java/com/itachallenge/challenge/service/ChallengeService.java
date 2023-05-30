package com.itachallenge.challenge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    public boolean removeResource(String idResource) {
        if(challengeRepository.existsByResourcesId(idResource)){
            challengeRepository.removeResourceFromAllChallenges();
            return true;
        }else {
            return false;
        }

        /*
        @Query("{$pull: {resources: {id: ?0}}}")
        void removeResourceFromAllChallenges(String resourceId);

        boolean existsByResourcesId(String resourceId);
         */

    }
}
