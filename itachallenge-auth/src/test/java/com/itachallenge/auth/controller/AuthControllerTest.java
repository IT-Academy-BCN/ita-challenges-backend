package com.itachallenge.auth.controller;

import com.itachallenge.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void validateTokenOK() {
        String validToken = "validToken";
        when(authService.validateWithSSO(validToken)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(authController.validateToken(validToken))
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode() == HttpStatus.OK &&
                                responseEntity.getBody().equals("Token is valid"))
                .verifyComplete();
    }


    @Test
    void validateTokenNotOK() {
        String invalidToken = "invalidToken";
        when(authService.validateWithSSO(invalidToken)).thenReturn(Mono.just(false));

        StepVerifier.create((authController.validateToken(invalidToken)))
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED &&
                        responseEntity.getBody().equals("Token is not valid"))
                .verifyComplete();
    }
}