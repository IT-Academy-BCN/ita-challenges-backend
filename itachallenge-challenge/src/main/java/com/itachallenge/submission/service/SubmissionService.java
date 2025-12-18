package com.itachallenge.submission.service;

import com.itachallenge.submission.document.SubmissionDocument;
import reactor.core.publisher.Flux;

public interface SubmissionService {

    Flux<SubmissionDocument> getAllSubmissionsByUser(String userId);
}
