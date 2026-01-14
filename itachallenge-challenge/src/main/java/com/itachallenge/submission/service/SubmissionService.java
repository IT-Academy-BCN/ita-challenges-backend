package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.submission.document.SubmissionDocument;
import reactor.core.publisher.Flux;

public interface SubmissionService {

    Flux<SubmissionDto> getAllSubmissionsByUser(String userId);
}
