package com.itachallenge.user.controller;

import static org.mockito.Mockito.*;

import com.itachallenge.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void validateMentor_WhenUserIsMentor_ShouldReturnOkWithUsername() {
        String githubUsername = "mentorUser";
        when(userService.isMentor(any())).thenReturn(Mono.just(githubUsername));

        Mono<ResponseEntity<String>> response = userController.validateMentor(Mono.just(githubUsername));

        StepVerifier.create(response)
                .expectNextMatches(res -> res.getStatusCode() == HttpStatus.OK && Objects.equals(res.getBody(), githubUsername))
                .verifyComplete();
    }

    @Test
    void validateMentor_WhenUserIsNotMentor_ShouldReturnForbiddenWithoutBody() {
        String githubUsername = "nonMentorUser";
        when(userService.isMentor(any())).thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> response = userController.validateMentor(Mono.just(githubUsername));

        StepVerifier.create(response)
                .expectNextMatches(res -> res.getStatusCode() == HttpStatus.FORBIDDEN && res.getBody() == null)
                .verifyComplete();
    }



    @Test
    void validateMentor_WhenUsernameIsEmpty_ShouldReturnBadRequest() {
        Mono<ResponseEntity<String>> response = userController.validateMentor(Mono.just(""));

        StepVerifier.create(response)
                .expectNextMatches(res -> res.getStatusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    @Test
    void validateMentor_WhenUserServiceThrowsError_ShouldReturnBadRequest() {
        String githubUsername = "mentorUser";
        when(userService.isMentor(any())).thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ResponseEntity<String>> response = userController.validateMentor(Mono.just(githubUsername));

        StepVerifier.create(response)
                .expectNextMatches(res -> res.getStatusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    @Test
    void validateMentor_WhenUserServiceThrowsErrorForNonMentor_ShouldReturnBadRequest() {
        String githubUsername = "nonMentorUser";
        when(userService.isMentor(any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        Mono<ResponseEntity<String>> response = userController.validateMentor(Mono.just(githubUsername));

        StepVerifier.create(response)
                .expectNextMatches(res -> res.getStatusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }


}
