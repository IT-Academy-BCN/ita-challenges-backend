package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dtos.ChallengeDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ModelMapperConverters {

    @Autowired
    private ModelMapper modelMapper;

    public Mono<ChallengeDto> convertChallengeToDTO(Mono<Challenge> challenge) {
        return challenge.map(c -> modelMapper.map(c, ChallengeDto.class));
    }

    public Flux<ChallengeDto> convertChallengesToDTO(Flux<Challenge> challengesList) {
       return challengesList.map(c -> modelMapper.map(c, ChallengeDto.class));
    }
}
