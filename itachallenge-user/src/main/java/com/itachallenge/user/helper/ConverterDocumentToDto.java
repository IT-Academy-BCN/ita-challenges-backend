package com.itachallenge.user.helper;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.UserScoreDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
public class ConverterDocumentToDto {


    public UserScoreDto fromUserScoreDocumentToUserScoreDto(UserSolutionDocument userSolutionDocument) {
        return UserScoreDto.builder()
                .challengeId(userSolutionDocument.getChallengeId())
                .languageID(userSolutionDocument.getLanguageId())
                .userId(userSolutionDocument.getUserId())
                .solutions(userSolutionDocument.getSolutionDocument())
                .build();
    }

    private UserScoreDto toUserScoreDto(UserSolutionDocument userScoreDocument) {
    return UserScoreDto.builder()
            .challengeId(userScoreDocument.getChallengeId())
            .languageID(userScoreDocument.getLanguageId())
            .userId(userScoreDocument.getUserId())
            .solutions(userScoreDocument.getSolutionDocument())
            .build();
    }
}
