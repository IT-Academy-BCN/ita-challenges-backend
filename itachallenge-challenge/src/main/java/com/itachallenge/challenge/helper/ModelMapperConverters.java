package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.dto.ChallengeDto;
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
