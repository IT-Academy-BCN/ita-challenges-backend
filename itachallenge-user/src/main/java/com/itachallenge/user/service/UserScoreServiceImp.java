package com.itachallenge.user.service;

import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.exception.UserScoreNotFoundException;
import com.itachallenge.user.helper.Converter;
import com.itachallenge.user.repository.IUserScoreRepository;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserScoreServiceImp implements IUserScoreService {
    @Autowired
    private IUserScoreRepository userScoreRepository;

    @Autowired
    private Converter converter;

    private static final Logger log = LoggerFactory.getLogger(UserScoreServiceImp.class);


    public Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String idUser, String idChallenge, String idLanguage) {
        UUID userUuid = convertToUUID(idUser);
        UUID challengeUuid = convertToUUID(idChallenge);
        UUID languageUuid = convertToUUID(idLanguage);

        return this.userScoreRepository.findByUserId(userUuid)
                .switchIfEmpty(Mono.error(new UserScoreNotFoundException("No challenges for user with id " + idUser)))
                .filter(userScore -> userScore.getChallengeId().equals(challengeUuid) && userScore.getLanguajeId().equals(languageUuid))
                .flatMap(userScore -> converter.fromUserScoreDocumentToUserScoreDto(Flux.just(userScore)))
                .collectList()
                    .map(userScoreDtos -> {
                        SolutionUserDto<UserScoreDto> solutionUserDto = new SolutionUserDto<>();
                        solutionUserDto.setInfo(0, 1, userScoreDtos.size(), userScoreDtos.toArray(new UserScoreDto[0]));
                        return solutionUserDto;
                })
                //.doOnSuccess(solutionUserDto -> log.info("Data found with given id"))
                //.doOnError(error -> log.error("Error occurred while retrieving challenge: {}", error.getMessage()))
                ;
    }

    public UUID convertToUUID(String id) {
            return UUID.fromString(id);
    }


}
