package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dtos.ChallengeDto;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMapper {

    public ChallengeDto mapToChallengeDto(Challenge challenge) {
        return ChallengeDto.builder()
                .challengeId(challenge.getChallengeId())
                .title(challenge.getTitle())
                .creationDate(challenge.getCreationDate())
                .related(challenge.getRelated())
                .build();
    }
}
