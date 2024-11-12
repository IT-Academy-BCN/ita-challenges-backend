package com.itachallenge.user.helper;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

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

//    public Flux<UserCompletedChallengeDto> fromUserSolutionDocumentToUserCompletedChallengeDto(UserSolutionDocument document) {
    public UserCompletedChallengeDto mapUserSolutionDocumentToUserCompletedChallengeDto(UserSolutionDocument document) {

        // vER MANEJO DE ERRORES
//        if (document == null) {
//            // Aquí podrías lanzar una excepción o retornar un DTO vacío
//            throw new IllegalArgumentException("UserSolutionDocument cannot be null");
//        }
        return UserCompletedChallengeDto.builder()
                .challengeId(document.getChallengeId().toString())
                .score(document.getScore())
                .build();
//        );
    }

    public UserStatisticsDto mapUserSolutionsToUserStatisticsDto (UserCompletedAndSavedChallengeDto userCompletedChallengeAndSavedDto,
                                                                  String idUser,
                                                                  String idLanguage) {

        UserStatisticsDto userStatisticsDto = new UserStatisticsDto();
        userStatisticsDto.setUserId(idUser);
        userStatisticsDto.setLanguageId(idLanguage);
        userStatisticsDto.setCompletedAndSavedChallenges(userCompletedChallengeAndSavedDto);
        return userStatisticsDto;

//        UserStatisticsDto.builder()
//                .userId(idUser)
//                .languageId(idLanguage)
//                .completedAndSavedChallenges(userCompletedChallengeAndSavedDto);

    }

}
