package com.itachallenge.user.service;
import com.itachallenge.user.dtos.SolutionScoreDto;
import com.itachallenge.user.exception.SolutionNotFoundException;
import com.itachallenge.user.repository.IUserSolutionRepository;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserScoreService {

    @Autowired
    private IUserSolutionRepository userSolutionRepository;

    public Mono<SolutionScoreDto> addScore(String idUser, String idChallenge, String idSolution) {
        UUID uuidUser = UUID.fromString(idUser);
        UUID uuidChallenge = UUID.fromString(idChallenge);
        UUID uuidSolution = UUID.fromString(idSolution);

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

    private SolutionScoreDto convertToSolutionScoreDto(UserSolutionDocument solution) {
        SolutionScoreDto dto = new SolutionScoreDto();
        dto.setChallengeId(solution.getChallengeId().toString());
        dto.setLanguageId(solution.getLanguageId().toString());
        dto.setSolutionText(solution.getSolutionDocument().get(0).getSolutionText());
        dto.setScore(solution.getScore());
        return dto;
    }
}