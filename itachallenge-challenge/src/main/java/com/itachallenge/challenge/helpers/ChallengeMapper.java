package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dtos.ChallengeDto;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMapper {

    public ChallengeDto mapToChallengeDto(Challenge challenge) {
        return ChallengeDto.builder()
                .uuid(challenge.getUuid())
                .build();
    }
}
