package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.document.ReadUuid;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.ReadUuidDto;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ChallengeMapper {

    public ChallengeDto mapToChallengeDto(Challenge challenge) {

        // challenges challenge.getRelatedChallenges(), set de ReadUuid;
        Set<ReadUuidDto> relateds = challenge.getRelatedChallenges()
                .stream().map(this::read)
                .collect(Collectors.toSet());

        return ChallengeDto.builder()
                .uuid(challenge.getUuid())
                .title(challenge.getTitle())
                .related(relateds)
                .build();
    }
    public Challenge mapToChallenge(ChallengeDto challengeDto) {
        return Challenge.builder()
                .uuid(challengeDto.getUuid())
                .title(challengeDto.getTitle())
               // .relatedChallenges(challengeDto.getRelated())
                .build();
    }

    public ReadUuidDto read(ReadUuid readUuid){
        return ReadUuidDto.builder()
                .uuid(readUuid.getUuid())
                .build();
    }
}
