package com.itachallenge.score.service;
import com.itachallenge.score.mqclient.ZMQClient;
import com.itachallenge.score.dto.zmq.TestingValuesResponseDto;
import com.itachallenge.score.dto.zmq.ChallengeRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceImplTest {

    @Mock
    private ZMQClient zmqClient;

    @InjectMocks
    private ScoreServiceImpl scoreService;

    @Test
    void getTestParamsTest_getExpectedResults() {
        String challengeId = UUID.randomUUID().toString();
        TestingValuesResponseDto expectedResponse = new TestingValuesResponseDto();
        when(zmqClient.sendMessage(any(ChallengeRequestDto.class), eq(TestingValuesResponseDto.class)))
                .thenReturn(CompletableFuture.completedFuture(expectedResponse));

        scoreService.getTestParams(challengeId)
                .subscribe(result -> {
                    verify(zmqClient, times(1)).sendMessage(any(ChallengeRequestDto.class), eq(TestingValuesResponseDto.class));
                    assertEquals(expectedResponse, result);
                });
    }
}
