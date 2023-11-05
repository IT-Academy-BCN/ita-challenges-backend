package com.itachallenge.user.helper;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.OneSolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class ConverterDocumentToDto {

    public Flux<UserScoreDto> fromUserScoreDocumentToUserScoreDto(Flux<UserSolutionDocument> just) {
    return just.map(this::toUserScoreDto);
    }

    public Mono<OneSolutionUserDto> fromUserSolutionDocumentToOneSolutionUserDto(Mono<UserSolutionDocument> userSolutionDocument, String solutionText) {
        return userSolutionDocument.map(userDoc -> fromDocumentToDto(userDoc, solutionText));
    }

    private OneSolutionUserDto fromDocumentToDto(UserSolutionDocument userSolution, String solutionText){
        return OneSolutionUserDto.builder()
                .challengeId(userSolution.getChallengeId())
                .languageId(userSolution.getLanguajeId())
                .userId(userSolution.getUserId())
                .solutionText(solutionText)
                .score(userSolution.getScore())
                .build();
    }

    private UserScoreDto toUserScoreDto(UserSolutionDocument userScoreDocument) {
    return UserScoreDto.builder()
            .challengeId(userScoreDocument.getChallengeId())
            .languageID(userScoreDocument.getLanguajeId())
            .userId(userScoreDocument.getUserId())
            .solutions(userScoreDocument.getSolutionDocument())
            .build();
    }

}
