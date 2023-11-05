package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.OneSolutionUserDto;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
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

    @Override
    public Mono<OneSolutionUserDto> postOneSolutionUser(String idChallenge, String idLanguage, String idUser, String solution, int score) {
        UUID challengeId = UUID.fromString(idChallenge);
        UUID languageId = UUID.fromString(idLanguage);
        UUID userId = UUID.fromString(idUser);

        SolutionDocument solutionDocument = new SolutionDocument(UUID.randomUUID(), solution);

        return userScoreRepository.findByChallengeIdAndLanguajeIdAndUserId(challengeId, languageId, userId)
                .switchIfEmpty(Mono.defer(() -> {
                    UserSolutionDocument newUserSolutionDocument = UserSolutionDocument.builder()
                            .uuid(UUID.randomUUID())
                            .userId(userId)
                            .challengeId(challengeId)
                            .languageId(languageId)
                            .bookmarked(false) // Add logic to determine if it's marked as a favorite
                            .status("pending") // Add logic to determine the status
                            .score(score)
                            .solutionDocument(new ArrayList<>(Collections.singletonList(solutionDocument)))
                            .build();
                    return userScoreRepository.save(newUserSolutionDocument);
                }))
                .flatMap(userSolutionDocument -> {
                    userSolutionDocument.getSolutionDocument().add(solutionDocument);
                    return userScoreRepository.save(userSolutionDocument)
                            .then(converter.fromUserSolutionDocumentToOneSolutionUserDto(Mono.just(userSolutionDocument), solution));
                });
    }

    public UUID convertToUUID(String id) {
            return UUID.fromString(id);
    }
}
