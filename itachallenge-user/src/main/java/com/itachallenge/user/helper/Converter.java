package com.itachallenge.user.helper;

import com.itachallenge.user.document.UserScoreDocument;
import com.itachallenge.user.dtos.UserScoreDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


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
            .solutionsSize(userScoreDocument.getSolutionDocument().size())
            .build();
    }
}
