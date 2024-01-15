package com.itachallenge.auth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class AuthControllerTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private AuthController authController;

    private String validToken;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        validToken = UUID.randomUUID().toString();

        // Configurar el comportamiento del WebClient mock
        WebClient webClientMock = mock(WebClient.class);
        when(webClientBuilder.build()).thenReturn(webClientMock);

        // Configurar el comportamiento del WebClient mock para el caso de éxito
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        doReturn(requestHeadersUriSpec).when(webClientMock).post();
        doReturn(requestBodySpec).when(requestHeadersUriSpec).uri(any(String.class));
        doReturn(requestBodySpec).when(requestBodySpec).bodyValue(validToken);
        doReturn(responseSpec).when(requestBodySpec).retrieve();
        doReturn(Mono.just(true)).when(responseSpec).bodyToMono(Boolean.class);


        // Configurar el comportamiento del WebClient mock para el caso de error
        WebClientResponseException errorResponseException = mock(WebClientResponseException.class);
        when(errorResponseException.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(webClientMock.post().uri(any(String.class)).bodyValue(validToken).retrieve().bodyToMono(Boolean.class))
                .thenThrow(errorResponseException);
    }

    @Test
    void testValidateToken_ValidToken() {

        Mono<ResponseEntity<String>> result = authController.validateToken(validToken);

        StepVerifier.create(result)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().equals(HttpStatus.OK) &&
                                responseEntity.getBody().equals("Token is valid"))
                .verifyComplete();
    }


    boolean isResponseEntityValid(ResponseEntity<String> responseEntity, HttpStatus expectedStatus, String expectedBody) {
        return responseEntity != null &&
                responseEntity.getStatusCode() == expectedStatus &&
                expectedBody.equals(responseEntity.getBody());
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalidToken";

        Mono<ResponseEntity<String>> result = authController.validateToken(invalidToken);

        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> isResponseEntityValid(responseEntity, HttpStatus.UNAUTHORIZED, "Token is not valid"))
                .verifyComplete();
    }

    @Test
    void testValidateToken_SSOError() {

        when(webClientBuilder.build().post().uri(any(String.class)).bodyValue(validToken).retrieve().
                bodyToMono(Boolean.class))
                .thenThrow(WebClientResponseException.create(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "SSO doesn´t respond", null, null, null));

        Mono<ResponseEntity<String>> result = authController.validateToken(validToken);

        StepVerifier.create(result)
                .expectNextMatches(responseEntity ->
                        isResponseEntityValid(responseEntity, HttpStatus.UNAUTHORIZED, "Token is not valid"))
                .verifyComplete();
    }

}


