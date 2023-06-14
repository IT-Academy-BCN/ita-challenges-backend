package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.dto.ChallengeDto;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMapper {

    public ChallengeDto mapToChallengeDto(Challenge challenge) {
        return ChallengeDto.builder()
                .uuid(challenge.getUuid())
                .title(challenge.getTitle())
                .relatedChallenges(challenge.getRelatedChallenges())
                .build();
    }
    public Challenge mapToChallenge(ChallengeDto challengeDto) {
        return Challenge.builder()
                .uuid(challengeDto.getUuid())
                .title(challengeDto.getTitle())
                .relatedChallenges(challengeDto.getRelatedChallenges())
                .build();
    }
}
