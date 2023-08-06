package com.itachallenge.user.helper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserScoreDocument;
import com.itachallenge.user.dtos.UserScoreDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.UUID;

@Component
public class Converter {


    public Flux<UserScoreDto> fromUserScoreDocumentToUserScoreDto(Flux<UserScoreDocument> just) {
    return just.map(this::toUserScoreDto);
    }

    private UserScoreDto toUserScoreDto(UserScoreDocument userScoreDocument) {
    return UserScoreDto.builder()
            .challengeId(userScoreDocument.getChallengeId())
            .languageID(userScoreDocument.getLanguajeId())
            .userId(userScoreDocument.getUserId())
            .solutions(userScoreDocument.getSolutionDocument())
            .build();
    }
}
