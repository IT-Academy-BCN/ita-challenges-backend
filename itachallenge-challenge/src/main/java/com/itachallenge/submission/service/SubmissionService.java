package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.PeerSubmissionItemDto;
import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.challenge.dto.submission.SubmissionActionRequestDto;
import com.itachallenge.challenge.dto.submission.SubmissionActionResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SubmissionService {

    Flux<SubmissionDto> getAllSubmissionsByUser(String userId);

    Mono<SubmissionActionResponseDto> processSubmissionAction(String userId, SubmissionActionRequestDto request, String authHeader);

    Flux<PeerSubmissionItemDto> getPeerSubmissions(UUID challengeId, UUID userId);

    Flux<PeerSubmissionItemDto> getPeerSubmissions(UUID challengeId, String userIdStr);
}



