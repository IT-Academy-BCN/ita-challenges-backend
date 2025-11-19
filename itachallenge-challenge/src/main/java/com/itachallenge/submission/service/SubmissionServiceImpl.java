package com.itachallenge.submission.service;

import com.itachallenge.user.document.SolutionAttemptDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.document.enums.ChallengeStatus;
import com.itachallenge.user.dto.SubmitSolutionResponseDto;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.dto.UserSolutionResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;


@Service
public class SubmissionServiceImpl implements SubmissionService {
    @Override
    public Mono<SubmitSolutionResponseDto> addSolution(UserSolutionRequestDto userSolutionDto) {
        return null;
    }

    @Override
    public Flux<UserSolutionResponseDto> getAllSolutionsByUser(String userId) {
        return null;
    }

    private Mono<UserSolutionDocument> saveValidSolution(UUID userUuid, UUID challengeUuid, UUID languageUuid, ChallengeStatus challengeStatus, SolutionAttemptDocument solutionAttempt) {
        return null;
    }
    private Mono<SubmitSolutionResponseDto> buildSubmitSolutionResponse(UserSolutionDocument savedDocument) {
        return null;

    }
    private Mono<UUID> validateAndParseUuid(String userId) {
        return null;
    }
}