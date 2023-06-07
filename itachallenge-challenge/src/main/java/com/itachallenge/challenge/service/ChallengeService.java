package com.itachallenge.challenge.service;

import com.itachallenge.challenge.repository.ChallengeRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeService {
    @Autowired
    private ChallengeRepository challengeRepository;
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
