package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;
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
    private final static HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND;
    private final static HttpStatus INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR;
    private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";
    private ChallengeDto challenge1;
    private ChallengeDto challenge2;
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private IChallengeService challengeService;
    @InjectMocks
    private ChallengeController challengeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        Set<LanguageDto> languagesDto = Set.of(new LanguageDto(1, "Javascript"), new LanguageDto(2, "java"));
        String uuidString = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
        UUID uuid = UUID.fromString(uuidString);
        String uuidString2 = "dcacb291-b4aa-4029-8e9b-284c8ca80297";
        UUID uuid2 = UUID.fromString(uuidString2);

        challenge1 = ChallengeDto.builder()
                .challengeId(uuid)
                .title("Sociis Industries")
                .level("EASY")
                .creationDate("2023-06-05T12:30:00+02:00")
                .popularity(105)
                .percentage(23.58f)
                .languages(languagesDto).build();

        challenge2 = ChallengeDto.builder()
                .challengeId(uuid2)
                .title("Sociis Industries 2")
                .level("EASY")
                .creationDate("2023-06-05T12:35:00+02:00")
                .popularity(89)
                .percentage(21.35f)
                .languages(languagesDto).build();
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
                .expectNextMatches(respNotFound -> respNotFound.getStatusCode().equals(NOT_FOUND))
                .verifyComplete();

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

    @Test
    @DisplayName("Test EndPoint: getChallenges")
    void testGetChallenges() {
        final String URI_GET_CHALLENGES = "/challenges";
        List<ChallengeDto> challenges = List.of(challenge1, challenge2);

        when(challengeService.getChallenges()).thenReturn(Flux.fromIterable(challenges));
        webTestClient.method(HttpMethod.GET)
                .uri(CHALLENGE_BASE_URL + URI_GET_CHALLENGES)
                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.fromIterable(challenges), ChallengeDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .equals(Flux.fromIterable(challenges));
    }

    private void verifyService(){
        verify(challengeService, times(1)).isValidUUID(VALID_ID);
        verify(challengeService, times(1)).getChallengeId(UUID.fromString(VALID_ID));
    }

}
