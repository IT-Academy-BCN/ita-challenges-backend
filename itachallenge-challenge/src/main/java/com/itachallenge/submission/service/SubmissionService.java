package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.PeerSolutionItemDto;
import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.challenge.dto.submission.SubmissionActionRequestDto;
import com.itachallenge.challenge.dto.submission.SubmissionActionResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SubmissionService {

    Flux<SubmissionDto> getAllSubmissionsByUser(String userId);

    Mono<SubmissionActionResponseDto> processSubmissionAction(String userId, SubmissionActionRequestDto request, String authHeader);

    /**
     * Returns up to 10 most recent peer solutions for the given challenge (Story #191, card #253).
     * Requires the requesting user to have already submitted the challenge (SUBMITTED_COMPLETE or SUBMITTED_INCOMPLETE).
     * Otherwise, returns 403 Forbidden.
     */
    Flux<PeerSolutionItemDto> getPeerSolutions(UUID challengeId, UUID userId);
}



