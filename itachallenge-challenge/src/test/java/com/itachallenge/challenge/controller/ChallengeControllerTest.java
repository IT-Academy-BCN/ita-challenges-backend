package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.service.IChallengeService;

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
class ChallengeControllerTest {
    //VARIABLES
    private final static String VALID_ID = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
    private final static String INVALID_ID = "123456789";
    private final static String MESSAGE_INVALID_ID = "Invalid ID format. Please indicate the correct format.";
    private final static String MESSAGE_INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR " + HttpStatus.INTERNAL_SERVER_ERROR.value();
    //VARIABLES HTTPSTATUS
    private final static HttpStatus OK = HttpStatus.OK;
    private final static HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    private final static HttpStatus INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR;
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

        Mono<ResponseEntity<ChallengeDto>> responseMono = challengeController.getOneChallenge(VALID_ID);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    assertEquals(OK, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertTrue(response.getBody() instanceof ChallengeDto);
                    assertEquals(challenge, response.getBody());
                    return true;
                })
                .verifyComplete();

        verifyService();
    }

    @Test
    void testGetOneChallenge_NotValidUUID() {
        when(challengeService.isValidUUID(INVALID_ID)).thenReturn(false);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            challengeController.getOneChallenge(INVALID_ID);
        });

        StepVerifier.create(Mono.just(exception))
                        .expectNextMatches(resp -> {
                            assertEquals(BAD_REQUEST.value(), exception.getStatusCode().value());
                            assertEquals(MESSAGE_INVALID_ID, exception.getReason());
                            return true;
                        })
                                .verifyComplete();

        verify(challengeService, times(1)).isValidUUID(INVALID_ID);
        //cuando el id es invalido no se llama al método getChallengeId
        verify(challengeService, times(0)).getChallengeId(UUID.fromString(VALID_ID));
    }

    @Test
    void testGetOneChallenge_Empty() {
        when(challengeService.isValidUUID(VALID_ID)).thenReturn(true);
        when(challengeService.getChallengeId(UUID.fromString(VALID_ID))).thenReturn(Mono.empty());

        Mono<ResponseEntity<ChallengeDto>> responseMono = challengeController.getOneChallenge(VALID_ID);

        StepVerifier.create(responseMono)
                .expectErrorMatches(respThrow -> respThrow instanceof ResponseStatusException
                        && ((ResponseStatusException) respThrow).getStatusCode() == HttpStatus.OK)
                .verify();

        verifyService();
    }
    
    @Test
    void testGetOneChallenge_Exception(){
        when(challengeService.isValidUUID(VALID_ID)).thenReturn(true);
        when(challengeService.getChallengeId(UUID.fromString(VALID_ID))).thenThrow(new RuntimeException(MESSAGE_INTERNAL_SERVER_ERROR));

        Mono<ResponseEntity<ChallengeDto>> responseMono = challengeController.getOneChallenge(VALID_ID);

        StepVerifier.create(responseMono)
                .expectNextMatches(responseInternal -> responseInternal.getStatusCode().equals(INTERNAL_SERVER_ERROR))
                .verifyComplete();

        verifyService();
    }

    private void verifyService(){
        verify(challengeService, times(1)).isValidUUID(VALID_ID);
        verify(challengeService, times(1)).getChallengeId(UUID.fromString(VALID_ID));
    }

    @Test
    void TestDeleteResources_BadRequest(){
        final String URI_TEST = "/resources/{idResource}";
        String uuidString = "not a uuid";

        webTestClient.delete()
                .uri(CHALLENGE_BASE_URL + URI_TEST,uuidString)
                .exchange()
                .expectStatus().isBadRequest();
        verify(challengeService,times(0)).removeResourcesByUuid(any());
    }

    @Test
    void TestDeleteResources_NotFOund(){
        final String URI_TEST = "/resources/{idResource}";
        String uuidString = "db30c7d7-59b1-4338-abfc-348bd5528f3b";
        UUID uuid = UUID.fromString(uuidString);

        //when

        when(challengeService.removeResourcesByUuid(uuid)).thenReturn(false);

        webTestClient.delete()
                .uri(CHALLENGE_BASE_URL + URI_TEST,uuidString)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(challengeService,times(1)).removeResourcesByUuid(uuid);
    }

    @Test
    void TestDeleteResources_OK(){
        final String URI_TEST = "/resources/{idResource}";
        String uuidString = "db30c7d7-59b1-4338-abfc-348bd5528f3b";
        UUID uuid = UUID.fromString(uuidString);

        //when

        when(challengeService.removeResourcesByUuid(uuid)).thenReturn(true);

        webTestClient.delete()
                .uri(CHALLENGE_BASE_URL + URI_TEST,uuidString)
                .exchange()
                .expectStatus()
                .isOk();

        verify(challengeService,times(1)).removeResourcesByUuid(uuid);
    }

}
