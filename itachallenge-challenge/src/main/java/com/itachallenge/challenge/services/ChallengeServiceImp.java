package com.itachallenge.challenge.services;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ChallengeServiceImp implements ChallengeService {
    @Autowired
    private ChallengeRepository challengeRepository;

    @Override
    public Mono<Challenge> getChallengeId(UUID id) {
        Mono<Challenge> challenge = challengeRepository.findById(id);

        // Comprueba si existe el desafío
        return challenge.switchIfEmpty(Mono.error(new IllegalArgumentException("ID challenge: " + id + " does not exist in the database.")));
    }

    @Override
    public boolean isValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}