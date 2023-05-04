package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.helper.ResourceHelper;
import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeController.class)
public class ChallengeControllerTest {

    @MockBean
    private ChallengeService challengeService;

    @Autowired
    private WebTestClient webTestClient;

    private static ResourceHelper resourceHelper;

    @BeforeAll
    static void setUp(){
        resourceHelper = new ResourceHelper();
    }


    @Test
    public void test() {
        assertEquals(1, 1);
    }


    //TEST FAILED: Expected 200 Ok, Actual 404 Not_Found
    @DisplayName("Get All Challenges Test")
    @Test
    void getAllChallengesTest() throws IOException {

        String resourceJsonPath = "/data-challenge.json";
        String expected =  resourceHelper.readResourceAsString(resourceJsonPath);
        when(challengeService.getAllChallenges()).thenReturn(Mono.just(expected));

        String uri = ChallengeController.CHALLENGES;
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(Assertions::assertNotNull)
                .value(result -> assertThat(result).isEqualTo(expected));


    }

}

