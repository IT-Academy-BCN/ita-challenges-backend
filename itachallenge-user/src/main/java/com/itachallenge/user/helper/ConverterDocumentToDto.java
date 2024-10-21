package com.itachallenge.user.helper;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.dtos.UserSolutionDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public class ConverterDocumentToDto {


    public Flux<UserScoreDto> fromUserScoreDocumentToUserScoreDto(Flux<UserSolutionDocument> just) {
    return just.map(this::toUserScoreDto);
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

  /*  public Mono<UserSolutionScoreDto> fromUserSolutionDocumentToUserSolutionScoreDto(UserSolutionDocument document) {
        return Mono.just(UserSolutionScoreDto.builder()
                .userId(document.getUserId().toString())
                .challengeId(document.getChallengeId().toString())
                .languageId(document.getLanguageId().toString())
                .solutionId(document.getUuid().toString())
                .solutionText(document.getSolutionDocument().get(0).getSolutionText()) // Assuming there's at least one solution text
                .status(document.getStatus().toString())
                .score(document.getScore())
                .errors(document.getErrors()) // Assuming errors is a List<String>
                .build());
    }*/  // комменитрую этот метод чтобы попробовать другой пониже
  public UserSolutionScoreDto fromUserSolutionDocumentToUserSolutionScoreDto(UserSolutionDocument document, SolutionDocument solution) {
      return UserSolutionScoreDto.builder()
              .userId(document.getUserId().toString())
              .challengeId(document.getChallengeId().toString())
              .languageId(document.getLanguageId().toString())
              .solutionId(solution.getUuid().toString())
              .solutionText(solution.getSolutionText())
              .status(document.getStatus().name())
              .score(document.getScore())
              .errors(document.getErrors()) // Assuming errors is a List<String>
              .build();
  }
}
