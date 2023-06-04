package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dto.ChallengeDto;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMapper {

    public ChallengeDto mapToChallengeDto(Challenge challenge) {

        return ChallengeDto.builder()
                .uuid(challenge.getUuid())
                .title(challenge.getTitle())
                .creationDate(challenge.getCreationDate())
                //.languages(challenge.getLanguages())
                .relateds(challenge.getRelatedChallenges())
                .build();

    }
    public Challenge mapToChallenge(ChallengeDto challengeDto) {
        return Challenge.builder()
                .uuid(challengeDto.getUuid())
                .title(challengeDto.getTitle())
                .creationDate(challengeDto.getCreationDate())
                //.languages(challengeDto.getLanguages())
                .relatedChallenges(challengeDto.getRelateds())
                .build();
    }
}
