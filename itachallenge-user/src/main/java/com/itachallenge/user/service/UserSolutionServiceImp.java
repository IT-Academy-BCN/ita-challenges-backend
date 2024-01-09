package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ch.qos.logback.classic.spi.ThrowableProxyVO.build;

@Service
public class UserSolutionServiceImp implements IUserSolutionService {
    @Autowired
    private IUserSolutionRepository userSolutionRepository;

    @Autowired
    private ConverterDocumentToDto converter;

    public Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String idUser, String idChallenge, String idLanguage) {
        UUID userUuid = UUID.fromString(idUser);
        UUID challengeUuid = UUID.fromString(idChallenge);
        UUID languageUuid = UUID.fromString(idLanguage);

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

    public Mono<UserSolutionScoreDto> addSolution(String idUser, String idChallenge,
                                                  String idLanguage, String solutionText) {

        if (Boolean.TRUE.equals(userSolutionRepository.findByUserId(UUID.fromString(idUser))
                .filter(userScore -> userScore.getChallengeId().equals(UUID.fromString(idChallenge)))
                .hasElements().block())) {
            return Mono.error(new IllegalArgumentException("User already has a solution for this challenge"));
        }

        UUID userUuid = UUID.fromString(idUser);
        UUID challengeUuid = UUID.fromString(idChallenge);
        UUID languageUuid = UUID.fromString(idLanguage);

        List<SolutionDocument> solutionDocuments = new ArrayList<>();
        SolutionDocument solutionDoc = new SolutionDocument();
        solutionDoc.setUuid(UUID.randomUUID());
        solutionDoc.setSolutionText(solutionText);

        UserSolutionDocument userSolutionDocument = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status("PENDING")
                .score(13)
                .solutionDocument(solutionDocuments)
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

