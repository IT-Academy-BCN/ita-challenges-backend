package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.SolutionScoreDto;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.dtos.UserSolScoreDto;
import com.itachallenge.user.exception.SolutionNotFoundException;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class UserScoreService implements IUserScoreService{
    @Autowired
    private IUserSolutionRepository userSolutionRepository;
    @Autowired
    private ConverterDocumentToDto converter;




    public SolutionScoreDto convertToSolutionScoreDto(UserSolutionDocument solution) {
        SolutionScoreDto dto = new SolutionScoreDto();
        dto.setChallengeId(UUID.fromString(solution.getChallengeId().toString()));
        dto.setLanguageId(UUID.fromString(solution.getLanguageId().toString()));
        dto.setSolutionText(solution.getSolutionDocument().get(0).getSolutionText());
        dto.setScore(solution.getScore());
        return dto;
    }

    @Override
    public Mono<SolutionUserDto<UserScoreDto>> getChallengeById(UUID userId, UUID challengeId, UUID languageId) {

        return userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userId, challengeId, languageId)
                .flatMap(userSolutionDocument -> {
                    UserScoreDto userScoreDto = converter.fromUserScoreDocumentToUserScoreDto(userSolutionDocument);
                    SolutionUserDto<UserScoreDto> solutionUserDto = new SolutionUserDto<>();
                    solutionUserDto.setInfo(0, 1, 0, new UserScoreDto[]{userScoreDto});
                    return Mono.just(solutionUserDto);
                });
    }

    @Override
    public Mono<SolutionUserDto<UserScoreDto>> getUserScoreByUserId(String userId, String challengeId, String languageId) {
        UUID uuidUser = UUID.fromString(userId);
        UUID uuidChallenge = UUID.fromString(challengeId);
        UUID uuidLanguage = UUID.fromString(languageId);

        return userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(uuidUser, uuidChallenge, uuidLanguage)
                .flatMap(userSolutionDocument -> {
                    UserScoreDto userScoreDto = converter.fromUserScoreDocumentToUserScoreDto(userSolutionDocument);
                    SolutionUserDto<UserScoreDto> solutionUserDto = new SolutionUserDto<>();
                    solutionUserDto.setInfo(0, 1, 0, new UserScoreDto[]{userScoreDto});
                    return Mono.just(solutionUserDto);
                });
    }
}
