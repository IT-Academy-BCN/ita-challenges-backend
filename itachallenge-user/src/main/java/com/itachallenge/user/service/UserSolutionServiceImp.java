package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.document.UserSolutionScore;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserSolutionServiceImp implements IUserSolutionService {
    @Autowired
    private IUserSolutionRepository userScoreRepository;

    @Autowired
    private ConverterDocumentToDto converter;

    public Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String idUser, String idChallenge, String idLanguage) {
        UUID userUuid = convertToUUID(idUser);
        UUID challengeUuid = convertToUUID(idChallenge);
        UUID languageUuid = convertToUUID(idLanguage);

        return this.userScoreRepository.findByUserId(userUuid)
                .filter(userScore -> userScore.getChallengeId().equals(challengeUuid) && userScore.getLanguageId().equals(languageUuid))
                .flatMap(userScore -> converter.fromUserScoreDocumentToUserScoreDto(Flux.just(userScore)))
                .collectList()
                    .map(userScoreDtos -> {
                        SolutionUserDto<UserScoreDto> solutionUserDto = new SolutionUserDto<>();
                        solutionUserDto.setInfo(0, 1, 0, userScoreDtos.toArray(new UserScoreDto[0]));
                        return solutionUserDto;
                });
    }

    public UUID convertToUUID(String id) {
            return UUID.fromString(id);
    }

    public Mono<UserSolutionScoreDto> addSolution(String idUser, String idChallenge,
                                                  String idLanguage, String solutionText) {
        UUID userUuid = convertToUUID(idUser);
        UUID challengeUuid = convertToUUID(idChallenge);
        UUID languageUuid = convertToUUID(idLanguage);

        UserSolutionScore userSolutionScore = UserSolutionScore.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .solutionText(solutionText)
                .build();

        UserSolutionScoreDto userSolutionScoreDto = UserSolutionScoreDto.builder()
                .userId(userSolutionScore.getUserId())
                .challengeId(userSolutionScore.getChallengeId())
                .languageID(userSolutionScore.getLanguageId())
                .solutionText(userSolutionScore.getSolutionText())
                .score(13)
                .build();

        SolutionDocument solutionDocument = new SolutionDocument();
        solutionDocument.setSolutionText(userSolutionScore.getSolutionText());

        UserSolutionDocument userSolutionDocument = new UserSolutionDocument();
        userSolutionDocument.setUserId(userSolutionScore.getUserId());
        userSolutionDocument.setChallengeId(userSolutionScore.getChallengeId());
        userSolutionDocument.setLanguageId(userSolutionScore.getLanguageId());
        userSolutionDocument.getSolutionDocument().add(solutionDocument);

        return userScoreRepository.save(userSolutionDocument).thenReturn(userSolutionScoreDto);
    }

}

