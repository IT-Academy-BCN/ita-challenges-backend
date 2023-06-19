package com.itachallenge.challenge.service;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChallengeService {

    private ChallengeRepository challengeRepository;

    public Flux<ChallengeDto> getChallenges () {
        Flux<Challenge> challengesList = challengeRepository.findAll();
        Flux<ChallengeDto> challengesDtoList = null;
        return challengesDtoList;
    }
}
