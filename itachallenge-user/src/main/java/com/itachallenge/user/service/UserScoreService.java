package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.SolutionScoreDto;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.exception.SolutionNotFoundException;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class UserScoreService implements IUserScoreService{
    @Autowired
    private IUserSolutionRepository userSolutionRepository;
    @Autowired
    private ConverterDocumentToDto converter;


    public Mono<SolutionScoreDto> addScore(UUID idUser, UUID idChallenge, UUID idSolution) {
        UUID uuidUser = UUID.fromString(String.valueOf(idUser));
        UUID uuidChallenge = UUID.fromString(String.valueOf(idChallenge));
        UUID uuidSolution = UUID.fromString(String.valueOf(idSolution));

        return userSolutionRepository.findByUserIdAndChallengeIdAndSolutionId(uuidUser, uuidChallenge, uuidSolution)
                .flatMap(solution -> {
                    // Lógica para obtener el puntaje del microservicio ita-score a través de ZMQ
                    return Mono.just(convertToSolutionScoreDto(solution)); // Reemplaza esto con la lógica real
                })
                .switchIfEmpty(Mono.error(new SolutionNotFoundException("Solution not found for given IDs")))
                .onErrorMap(e -> {
                    if (e instanceof IllegalArgumentException) {
                        return new IllegalArgumentException("Invalid argument provided", e);
                    } else {
                        return new RuntimeException("Internal server error", e);
                    }
                });
    }

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
        // Lógica para obtener la puntuación del usuario por su ID de usuario, desafío y lenguaje
        // Esto podría involucrar llamadas al repositorio y la conversión de documentos a DTOs

        // Ejemplo simplificado:
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
