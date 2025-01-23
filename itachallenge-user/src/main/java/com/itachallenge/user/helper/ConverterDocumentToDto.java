package com.itachallenge.user.helper;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.UserSolutionDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
public class ConverterDocumentToDto {

    public Flux<UserSolutionDto> fromUserSolutionDocumentToUserSolutionDto(UserSolutionDocument document) {
        return Flux.just(UserSolutionDto.builder()
                .userId(document.getUserId().toString())
                .challengeId(document.getChallengeId().toString())
                .languageId(document.getLanguageId().toString())
                .status(document.getStatus().toString())
                .solutionText(document.getSolutionDocument().get(0).getSolutionText())
                .build());
    }
}
