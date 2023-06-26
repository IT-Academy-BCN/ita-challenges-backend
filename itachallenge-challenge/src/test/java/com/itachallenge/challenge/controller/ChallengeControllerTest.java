package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ChallengeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private ChallengeService challengeService;

    @InjectMocks
    private ChallengeController challengeController;

    private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";

    private ChallengeDto challenge1;
    private ChallengeDto challenge2;

    @BeforeEach
    public void setup() {
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
}

