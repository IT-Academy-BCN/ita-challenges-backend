package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeController.class)
class ChallengeControllerTest {

    @MockBean
    private ChallengeService challengeService;

    @Autowired
    private WebTestClient webTestClient;



    @Test
    void test() {
        assertEquals(1, 1);
    }


    @DisplayName("Get All Challenges Controller Test")
    @Test
    void getAllChallengesControllerTest() throws IOException {

        String jsonFile = "C:\\Users\\Alfonso\\ita-challenges-backend\\itachallenge-challenge\\src\\test\\resources\\data-challenge.json";
        String uri = "http://localhost:8762/itachallenge/api/v1/challenge/getAllChallenges";


        byte[] jsonData = Files.readAllBytes(Paths.get(jsonFile));
        String expected = new String(jsonData, "UTF-8");
        when(challengeService.getAllChallenges()).thenReturn(Mono.just(expected));

        webTestClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expected);


    }

}

