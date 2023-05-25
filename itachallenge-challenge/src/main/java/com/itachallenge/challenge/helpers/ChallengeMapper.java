package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dtos.ChallengeDto;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMapper {
    public Challenge mapToChallenge(ChallengeDto challengeDto) {
        return Challenge.builder()
                .title(challengeDto.getTitle())
                .creationDate(challengeDto.getCreationDate())
                .related(challengeDto.getRelated())
                .build();
    }

    public ChallengeDto mapToChallengeDto(Challenge challenge) {
        return ChallengeDto.builder()
                .challengeId(challenge.getChallengeId())
                .title(challenge.getTitle())
                .creationDate(challenge.getCreationDate())
                .related(challenge.getRelated())
                .build();
    }
}
