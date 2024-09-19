package com.itachallenge.user.helper;

import com.itachallenge.user.document.ErrorsDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.dtos.UserSolScoreDto;
import com.itachallenge.user.dtos.UserSolutionDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;


@Component
public class ConverterDocumentToDto {

    public Flux<UserScoreDto> fromUserScoreDocumentToUserScoreDto(Flux<UserSolutionDocument> just) {
        return just.map(this::toUserScoreDto);
    }

   /* private UserScoreDto toUserScoreDto(UserSolutionDocument userScoreDocument) {
        return UserScoreDto.builder()
                .challengeId(userScoreDocument.getChallengeId())
                .languageID(userScoreDocument.getLanguageId())
                .userId(userScoreDocument.getUserId())
                .solutions(userScoreDocument.getSolutionDocument())
                .build();
    }
}*/


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

    public Flux<UserSolutionDto> fromUserSolutionDocumentToUserSolutionDto(UserSolutionDocument document) {
        return Flux.just(UserSolutionDto.builder()
                .userId(document.getUserId().toString())
                .challengeId(document.getChallengeId().toString())
                .languageId(document.getLanguageId().toString())
                .status(document.getStatus().toString())
                .solutionText(document.getSolutionDocument().get(0).getSolutionText())
                .build());
    }

    public UserSolScoreDto fromUserSolutionDocumentToUserSolScoreDto(UserSolutionDocument userSolutionDocument)
    {
        return UserSolScoreDto.builder()
                .userId(userSolutionDocument.getUserId())
                .challengeId(userSolutionDocument.getChallengeId())
                .languageId(userSolutionDocument.getLanguageId())
                .solutionId(userSolutionDocument.getSolutionDocument().get(0).getUuid())
                .status(userSolutionDocument.getStatus())
                .score(userSolutionDocument.getScore())
                .errors(userSolutionDocument.getErrors())
                .build();
    }

}
