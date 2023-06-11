package com.itachallenge.challenge.services;

import com.itachallenge.challenge.dtos.ChallengeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ChallengeServiceImp implements IChallengeService {

    @Autowired
    private ChallengeDto challengeDto;

    @Override
    public Mono<ChallengeDto> getChallengeId(UUID id) {
        challengeDto.setId_challenge(id);

        return Mono.just(challengeDto).switchIfEmpty(Mono.error(new IllegalArgumentException("ID challenge: " + id + " does not exist in the database.")));
    }
/*
    @Override
    public Mono<ChallengeDto> getChallengeId(UUID id) {
        challengeDto.setId_challenge(id);

        // Aquí se debe retornar un Mono<ChallengeDto>
        return Mono.just(challengeDto);
    }
*/


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
