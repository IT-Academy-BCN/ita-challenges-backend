package com.itachallenge.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.user.dto.SolvedDto;
import com.itachallenge.user.exception.BadRequestException;
import com.itachallenge.user.exception.InternalServerErrorException;
import com.itachallenge.user.exception.NotFoundException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ChallengeServiceImplTest {

    private MockWebServer mockWebServer;
    private ChallengeServiceImpl challengeService;

    private static final String SOLVED_URL = "/itachallenge/api/v1/challenge/solved/submitted_completed/%s";
    public static final String X_SOLVED_MESSAGE = "X-Solved-Message";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        challengeService = new ChallengeServiceImpl(
                WebClient.builder(),
                mockWebServer.url("").toString()
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.close();
    }

    @Test
    void addChallengeToSolved_AddedToUser_ReturnsTrue() throws Exception {
        String challengeId = "someId";

        SolvedDto solvedDto = new SolvedDto(true, 5);

        String responseBody = new ObjectMapper().writeValueAsString(solvedDto);

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .setResponseCode(201)
                .addHeader("Content-Type", "application/json"));

        Mono<SolvedDto> result = challengeService.addChallengeToSolved(challengeId);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertTrue(dto.isSolved(), "Expected isSolved to be true");
                    assertEquals(5, dto.getTimesSolved(), "Expected timesSolved to be 5");
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(SOLVED_URL, challengeId),
                request.getRequestUrl().encodedPath());
    }


    @Test
    void addChallengeToSolved_NotAddedToUser_ReturnsFalse() throws InterruptedException {
        String challengeId = "someId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"isSolved\":false,\"timesSolved\":0}")
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        Mono<SolvedDto> result = challengeService.addChallengeToSolved(challengeId);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertFalse(dto.isSolved());
                    assertEquals(0, dto.getTimesSolved());
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(String.format(SOLVED_URL, challengeId), request.getRequestUrl().encodedPath());
    }

    @Test
    void addChallengeToSolved_BadRequest_ReturnsError() throws InterruptedException {
        String challengeId = "someId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(400)
                .addHeader(X_SOLVED_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<SolvedDto> result = challengeService.addChallengeToSolved(challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadRequestException.class, throwable);
                    assertTrue(throwable.getMessage().contains(someErrorMessage));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(String.format(SOLVED_URL, challengeId), request.getRequestUrl().encodedPath());
    }

    @Test
    void addChallengeToSolved_ChallengeNotFound_ReturnsError() throws InterruptedException {
        String challengeId = "someId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json"));

        Mono<SolvedDto> result = challengeService.addChallengeToSolved(challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(NotFoundException.class, throwable);
                    assertTrue(throwable.getMessage().contains("Challenge not found"));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(String.format(SOLVED_URL, challengeId), request.getRequestUrl().encodedPath());
    }

    @Test
    void addChallengeToSolved_InternalServerError_ReturnsError() throws InterruptedException {
        String challengeId = "someId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(500)
                .addHeader(X_SOLVED_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<SolvedDto> result = challengeService.addChallengeToSolved(challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(InternalServerErrorException.class, throwable);
                    assertTrue(throwable.getMessage().contains(someErrorMessage));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(String.format(SOLVED_URL, challengeId), request.getRequestUrl().encodedPath());
    }
}
