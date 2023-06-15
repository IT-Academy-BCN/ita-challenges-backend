package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ChallengeControllerTest {
    //region ATTRIBUTES
    @Autowired
    private WebTestClient webTestClient;
    private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";

    @MockBean
    ChallengeService challengeService;

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
    void TestDeleteResources_BadRequest(){
        final String URI_TEST = "/resources/{idResource}";
        String uuidString = "not a uuid";

        webTestClient.delete()
                .uri(CHALLENGE_BASE_URL + URI_TEST,uuidString)
                .exchange()
                .expectStatus().isBadRequest();
        verify(challengeService,times(0)).removeResourcesById(any());
    }

    @Test
    void TestDeleteResources_NotFOund(){
        final String URI_TEST = "/resources/{idResource}";
        String uuidString = "db30c7d7-59b1-4338-abfc-348bd5528f3b";
        UUID uuid = UUID.fromString(uuidString);

        //when

        when(challengeService.removeResourcesById(uuid)).thenReturn(false);

        webTestClient.delete()
                .uri(CHALLENGE_BASE_URL + URI_TEST,uuidString)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(challengeService,times(1)).removeResourcesById(uuid);
    }

    @Test
    void TestDeleteResources_OK(){
        final String URI_TEST = "/resources/{idResource}";
        String uuidString = "db30c7d7-59b1-4338-abfc-348bd5528f3b";
        UUID uuid = UUID.fromString(uuidString);

        //when

        when(challengeService.removeResourcesById(uuid)).thenReturn(true);

        webTestClient.delete()
                .uri(CHALLENGE_BASE_URL + URI_TEST,uuidString)
                .exchange()
                .expectStatus()
                .isNoContent();

        verify(challengeService,times(1)).removeResourcesById(uuid);
    }
}

