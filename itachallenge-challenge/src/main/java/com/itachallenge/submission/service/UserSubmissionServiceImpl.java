package com.itachallenge.submission.service;

import com.itachallenge.submission.dto.UserSubmissionResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;



@Service
public class UserSubmissionServiceImpl implements IUserSubmissionService {

    @Override
    public Flux<UserSubmissionResponseDto> getAllSolutionsByUser(String userId) {
        return null;
    }
}