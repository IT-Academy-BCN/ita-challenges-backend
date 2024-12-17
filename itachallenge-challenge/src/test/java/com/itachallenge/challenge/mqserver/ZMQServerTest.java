package com.itachallenge.challenge.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.challenge.dto.ChallengeTestingValuesDto;
import com.itachallenge.challenge.dto.TestingValueDto;
import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.helper.ObjectSerializer;
import com.itachallenge.challenge.service.IChallengeService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZMQServerTest {
    private static final Logger log = LoggerFactory.getLogger(ZMQServerTest.class);

    @Mock
    private ZContext mockContext;

    @Mock
    private ZMQ.Socket mockSocket;

    @Mock
    private IChallengeService mockChallengeService;

    @InjectMocks
    private ZMQServer zmqServer;

    @Test
    void testDeserializeRequest_validInput() {
        byte[] validData = new byte[0];
        try {
            validData = ObjectSerializer.serialize(new ChallengeRequestDto(UUID.randomUUID()));
        } catch (JsonProcessingException e) {
            fail("Serialization failed with exception: " + e.getMessage());
        }

        Optional<ChallengeRequestDto> result = zmqServer.deserializeRequest(validData);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getChallengeId());
    }

    @Test
    void testDeserializeRequest_invalidInput() {
        byte[] invalidData = "invalid".getBytes();
        Optional<ChallengeRequestDto> result = zmqServer.deserializeRequest(invalidData);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSerializeResponse_validInput() {
        ChallengeTestingValuesDto challengeData = new ChallengeTestingValuesDto();
        Mono<byte[]> result = zmqServer.serializeResponse(challengeData);

        StepVerifier.create(result)
                .expectNextMatches(bytes -> bytes.length > 0) // Ensure non-empty result
                .verifyComplete();
    }

    @Test
    void testSerializeResponse_invalidInput() {
        ChallengeTestingValuesDto invalidChallenge = null;
        Mono<byte[]> result = zmqServer.serializeResponse(invalidChallenge)
                .doOnNext(bytes -> log.debug("Received bytes: {}", Arrays.toString(bytes)));
        StepVerifier.create(result)
                .expectNextMatches(bytes -> bytes.length == 0) // Expect empty byte array
                .verifyComplete();
    }

    @Test
    void testReceiveAndProcessMessage() throws IOException, InterruptedException {
        when(mockContext.createSocket(SocketType.REP)).thenReturn(mockSocket);

        UUID challengeId = UUID.randomUUID();
        ChallengeRequestDto mockRequest = new ChallengeRequestDto(challengeId);
        byte[] requestBytes = ObjectSerializer.serialize(mockRequest);

        ChallengeTestingValuesDto mockResponse = buildMockResponse(challengeId);
        byte[] responseBytes = ObjectSerializer.serialize(mockResponse);

        when(mockSocket.recv(0)).thenReturn(requestBytes);
        when(mockChallengeService.getTestingParamsByChallengeId(challengeId.toString()))
                .thenReturn(Mono.just(mockResponse));

        zmqServer.init();
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            zmqServer.run();
            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);

        verify(mockSocket, atLeastOnce()).recv(0);
        verify(mockChallengeService, atLeastOnce()).getTestingParamsByChallengeId(challengeId.toString());
        verify(mockSocket, atLeastOnce()).send(responseBytes, 0);

        zmqServer.shutdown();
        executorService.shutdownNow();
    }

    private ChallengeTestingValuesDto buildMockResponse(UUID challengeId) {
        TestingValueDto mockTestingValue = TestingValueDto.builder()
                .inParam(Arrays.asList("a", "b"))
                .outParam(Arrays.asList("1", "2"))
                .build();

        return ChallengeTestingValuesDto.builder()
                .challengeId(challengeId)
                .testingValues(Collections.singletonList(mockTestingValue))
                .build();
    }
}