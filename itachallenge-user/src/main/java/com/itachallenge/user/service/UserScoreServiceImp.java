package com.itachallenge.user.service;

import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.exception.UserScoreNotFoundException;
import com.itachallenge.user.helper.Converter;
import com.itachallenge.user.repository.IUserScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class UserScoreServiceImp implements IUserScoreService {
    @Autowired
    private IUserScoreRepository userScoreRepository;

    @Autowired
    private Converter converter;

    public Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String idUser, String idChallenge, String idLanguage) {
        UUID userId = convertToUUID(idUser);
        UUID challengeId = convertToUUID(idChallenge);
        UUID languageId = convertToUUID(idLanguage);

        return this.userScoreRepository.findByUserId(idUser)
                .filter(s -> s.getChallengeId().equals(challengeId) && s.getLanguajeId().equals(languageId))
                .switchIfEmpty(Mono.error(new UserScoreNotFoundException("No challenges for user with id " + idUser + " and language " + idLanguage + " were found")))
                .flatMap(userScore -> converter.fromUserScoreDocumentToUserScoreDto(Flux.just(userScore)))
                .collectList()
                .map(userScoreDtos -> {
                    SolutionUserDto<UserScoreDto> solutionUserDto = new SolutionUserDto<>();
                    solutionUserDto.setInfo(0, 1, 1, userScoreDtos.toArray(new UserScoreDto[0]));
                    return solutionUserDto;
                });
    }

    public UUID convertToUUID(String id) {
            return UUID.fromString(id);
    }


}
