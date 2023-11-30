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
    //aquest metode no cal ja que es pot fer servir UUID.fromString(string) i retorna un objecte UUID
  /*  public UUID convertToUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID string: " + id, e);
        }
    }*/

    public Mono<UserSolutionScoreDto> addSolution(String idUser, String idChallenge,
                                                  String idLanguage, String solutionText) {

        //if (idUser == null || idChallenge== null || idLanguage == null) {
         //   return Mono.error(new NullPointerException("Some of these values is null"));
        //}
/*
        UUID userUuid;
        UUID challengeUuid;
        UUID languageUuid;

        try {
            userUuid = UUID.fromString(idUser);
            challengeUuid = UUID.fromString(idChallenge);
            languageUuid = UUID.fromString(idLanguage);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Invalid UUID string", e));
        }*/

        //valida si ja existeix un usuari que ja a antregat una solucio per el challenge
        //la excepcio esta gestionada a la clase GlobalExceptionHandler
        if (Boolean.TRUE.equals(userSolutionRepository.findByUserId(UUID.fromString(idUser))
                .filter(userScore -> userScore.getChallengeId().equals(UUID.fromString(idChallenge)))
                .hasElements().block())) {
            return Mono.error(new IllegalArgumentException("User already has a solution for this challenge"));
        }

        //les validacions no calen ja que es fan a la clase controller amb @Valid i a la clase GenericUUIDValid
        UUID userUuid = UUID.fromString(idUser);
        UUID challengeUuid = UUID.fromString(idChallenge);
        UUID languageUuid = UUID.fromString(idLanguage);


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

