package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.services.IChallengeService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ChallengeControllerTest {
    //VARIABLES
    private final static String VALID_ID = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
    private final static String INVALID_ID = "123456789";
    private final static String MESSAGE_INVALID_ID = "Invalid ID format. Please indicate the correct format.";
    private final static String MESSAGE_INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR " + HttpStatus.INTERNAL_SERVER_ERROR.value();
    private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private IChallengeService challengeService;
    @InjectMocks
    private ChallengeController challengeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test EndPoint: test")
    void TestEndPoint_test(){
        final String URI_TEST = "/test";
        webTestClient.get()
                .uri(CHALLENGE_BASE_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(s -> s, equalTo("Hello from ITA Challenge!!!"));
    }

    @Test
    void testGetOneChallenge_ValidUUID() {
        ChallengeDto challenge = new ChallengeDto();

        doReturn(true).when(challengeService).isValidUUID(VALID_ID);
        doReturn(Mono.just(challenge)).when(challengeService).getChallengeId(UUID.fromString(VALID_ID));

        Mono<? extends ResponseEntity<?>> responseMono = challengeController.getOneChallenge(VALID_ID);
        ResponseEntity<?> response = responseMono.block();

        assertNotNull(response); //verifica que el objeto no es nulo.
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody()); //verifica que el cuerpo de la respuesta no sea nulo.
        
        Object responseBody = response.getBody();
        assertTrue(responseBody instanceof ChallengeDto); //instanciamos el objeto de ChallengeDto

        ChallengeDto challengeBody = (ChallengeDto) responseBody;
        assertEquals(challenge, challengeBody);
        assertEquals(challenge, response.getBody());

        verifyService();
    }

    @Test
    void testGetOneChallenge_NotValidUUID() {
        when(challengeService.isValidUUID(INVALID_ID)).thenReturn(false);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            challengeController.getOneChallenge(INVALID_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertEquals(MESSAGE_INVALID_ID, exception.getReason());

        verify(challengeService, times(1)).isValidUUID(INVALID_ID);
        //cuando el id es invalido no se llama al método getChallengeId
        verify(challengeService, times(0)).getChallengeId(UUID.fromString(VALID_ID));
    }

    @Test
    void testGetOneChallenge_Empty() {
        when(challengeService.isValidUUID(VALID_ID)).thenReturn(true);
        when(challengeService.getChallengeId(UUID.fromString(VALID_ID))).thenReturn(Mono.empty());

        Mono<? extends ResponseEntity<?>> responseMono = challengeController.getOneChallenge(VALID_ID);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatusCode().equals(HttpStatus.NOT_FOUND))
                .verifyComplete();

        verifyService();
    }

    @Test
    void testGetOneChallenge_Exception(){
        when(challengeService.isValidUUID(VALID_ID)).thenReturn(true);
        when(challengeService.getChallengeId(any(UUID.class))).thenThrow(new RuntimeException(MESSAGE_INTERNAL_SERVER_ERROR));

        Mono<? extends ResponseEntity<?>> responseMono = challengeController.getOneChallenge(VALID_ID);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();

        verifyService();
    }

    private void verifyService(){
        verify(challengeService, times(1)).isValidUUID(VALID_ID);
        verify(challengeService, times(1)).getChallengeId(UUID.fromString(VALID_ID));
    }

}
