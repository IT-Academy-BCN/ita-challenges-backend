package com.itachallenge.user.integration;


import com.itachallenge.user.dtos.zmq.ScoreRequestDto;
import com.itachallenge.user.dtos.zmq.ScoreResponseDto;
import com.itachallenge.user.mqclient.clientZMQ;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private clientZMQ zmqClient;

    @Test
    @DisplayName("Test ZMQ Client sends request and receives response")
    void testZmqClientCommunication() {
        ScoreRequestDto requestDto = new ScoreRequestDto();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("exampleParameter", 42);
        requestDto.setUserId(UUID.randomUUID());
        requestDto.setChallengeId(UUID.randomUUID());
        requestDto.setParameters(parameters);

        webTestClient
                .get()
                .uri("/user/api/v1/test/zmq")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ScoreResponseDto.class)
                .value(response -> {
                    assert response != null;
                    System.out.println("Response: " + response);
                });
    }
}
