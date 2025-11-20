package com.itachallenge.submission.service;

import com.itachallenge.submission.dto.SubmissionResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;



@Service
public class SubmissionServiceImpl implements ISubmissionService {

    @Override
    public Flux<SubmissionResponseDto> getAllSolutionsByUser(String userId) {
        return null;
    }

}