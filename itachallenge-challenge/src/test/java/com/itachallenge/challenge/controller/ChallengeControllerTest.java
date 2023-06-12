package com.itachallenge.challenge.controller;

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
import org.springframework.test.web.reactive.server.WebTestClient;

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
    private ChallengeService challengeService;

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
    void TestRemoveResource_NotFound(){
        final String URI_TEST="/resources/{idResource}";
        final String idResource = "6236d499-56fb-4a1d-82a4-7210d92625e0";

        when(challengeService.removeResourcesById(idResource)).thenReturn(false);

        webTestClient.delete()
                .uri(CHALLENGE_BASE_URL + URI_TEST,idResource)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(challengeService,times(1)).removeResourcesById(idResource);
    }

    @Test
    void TestRemoveResource_BadRequest(){
        final String URI_TEST="/resources/{idResource}";
        final String idResource = "Obviamente no es un UUID";


        webTestClient.delete()
                .uri(CHALLENGE_BASE_URL + URI_TEST,idResource)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(challengeService,times(0)).removeResourcesById(idResource);
    }

    @Test
    void TestRemoveResource_NoContent(){
        final String URI_TEST="/resources/{idResource}";
        final String idResource = "2bb1da87-f060-484e-9098-a38cc29dcfad";

        when(challengeService.removeResourcesById(idResource)).thenReturn(true);


        webTestClient.delete()
                .uri(CHALLENGE_BASE_URL + URI_TEST,idResource)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        verify(challengeService,times(1)).removeResourcesById(idResource);
    }


}
