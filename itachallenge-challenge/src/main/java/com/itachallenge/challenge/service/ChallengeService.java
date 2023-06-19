package com.itachallenge.challenge.service;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.helpers.ModelMapperConverters;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private ModelMapperConverters modelMapper;

    public Flux<ChallengeDto> getChallenges () {
        Flux<Challenge> challengesList = challengeRepository.findAll();
        Flux<ChallengeDto> challengesDtoList = modelMapper.convertChallengesToDTO(challengesList);
        return challengesDtoList;
    }
}
