package com.itachallenge.challenge.mqclient;

import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.dto.zmq.StatisticsResponseDto;
import com.itachallenge.challenge.helper.ObjectSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class ZMQClientTest {

    @Mock
    private ZContext context;

    @Mock
    private ZMQ.Socket socket;

    @Mock
    private ObjectSerializer objectSerializer;

    @InjectMocks
    private ZMQClient zmqClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(context.createSocket(ZMQ.REQ)).thenReturn(socket);
    }

    @Test
    void testSendMessage() throws ExecutionException, InterruptedException {
        // Initialize requestMessage and response
        ChallengeRequestDto requestMessage = ChallengeRequestDto.builder()
                .challengeId(UUID.randomUUID())
                .build();

        StatisticsResponseDto response = StatisticsResponseDto.builder()
                .percent(50)
                .bookmarks(10)
                .build();

        byte[] message = new byte[0];
        byte[] responseMessage = new byte[0];

        try {
            when(objectSerializer.serialize(any(ChallengeRequestDto.class))).thenReturn(message);
            when(objectSerializer.deserialize(any(byte[].class), eq(StatisticsResponseDto.class))).thenReturn(response);
        } catch (Exception e) {
            fail("Serialization or deserialization failed", e);
        }

        when(socket.send(message, 0)).thenReturn(true);
        when(socket.recv(0)).thenReturn(responseMessage);

        CompletableFuture<Object> future = zmqClient.sendMessage(requestMessage, StatisticsResponseDto.class);
        StatisticsResponseDto responseResult = (StatisticsResponseDto) future.get();

        verify(socket, times(1)).send(message, 0);
        verify(socket, times(1)).recv(0);
        assertThat(responseResult, is(response));
    }
}