package com.itachallenge.challenge.services;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dtos.ChallengeDto;

import com.itachallenge.challenge.helpers.ChallengeMapper;
import com.itachallenge.challenge.repositories.ChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ChallengeServiceImp implements ChallengeService {
    private static final Logger log = LoggerFactory.getLogger(ChallengeServiceImp.class);
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private ChallengeMapper challengeMapper;

    @Override
    public Mono<ChallengeDto> getChallengeId(UUID id) {
        Mono<Challenge> challenge = challengeRepository.findById(id);
        //control
        log.info("Object challenge in getChallengeId(): " + challenge.blockOptional().isPresent());
        // Comprueba si existe el desafío
        return challenge.map(challengeMapper::mapToChallengeDto).switchIfEmpty(Mono.error(new IllegalArgumentException("ID challenge: " + id + " does not exist in the database.")));
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