package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
public class UserSolutionServiceImp implements IUserSolutionService {
    @Autowired
    private IUserSolutionRepository userSolutionRepository;

    @Autowired
    private ConverterDocumentToDto converter;

    public Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String idUser, String idChallenge, String idLanguage) {
        UUID userUuid = convertToUUID(idUser);
        UUID challengeUuid = convertToUUID(idChallenge);
        UUID languageUuid = convertToUUID(idLanguage);

        return this.userSolutionRepository.findByUserId(userUuid)
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
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID string: " + id, e);
        }
    }

    public Mono<UserSolutionScoreDto> addSolution(String idUser, String idChallenge,
                                                  String idLanguage, String solutionText) {

        if (idUser == null || idChallenge== null || idLanguage == null) {
            return Mono.error(new NullPointerException("Some of these values is null"));
        }

        UUID userUuid;
        UUID challengeUuid;
        UUID languageUuid;

        try {
            userUuid = convertToUUID(idUser);
            challengeUuid = convertToUUID(idChallenge);
            languageUuid = convertToUUID(idLanguage);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Invalid UUID string", e));
        }

        SolutionDocument solutionDoc = new SolutionDocument();
        solutionDoc.setSolutionText(solutionText);

        UserSolutionDocument userSolutionDocument = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .score(13)
                .build();

        userSolutionDocument.getSolutionDocument().add(solutionDoc);

        if (userSolutionDocument.getUuid() == null) {
            userSolutionDocument.setUuid(UUID.randomUUID());
        }

        return userSolutionRepository.save(userSolutionDocument)
                .flatMap(savedDocument -> {
                    UserSolutionScoreDto userSolutionScoreDto = UserSolutionScoreDto.builder()
                            .userId(idUser)
                            .languageId(idLanguage)
                            .challengeId(idChallenge)
                            .solutionText(solutionText)
                            .score(savedDocument.getScore())
                            .build();

                    return Mono.just(userSolutionScoreDto);
                });
    }

}

