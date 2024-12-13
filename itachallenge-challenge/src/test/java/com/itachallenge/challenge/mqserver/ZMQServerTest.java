package com.itachallenge.challenge.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.challenge.dto.ChallengeTestingValuesDto;
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

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZMQServerTest {
    private static final Logger log = LoggerFactory.getLogger(ZMQServerTest.class);

    @InjectMocks
    private ZMQServer zmqServer;

    @Mock
    private IChallengeService challengeService;

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
}