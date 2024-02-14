package com.itachallenge.user.mqserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.user.dtos.zmq.ChallengeRequestDto;
import com.itachallenge.user.dtos.zmq.StatisticsResponseDto;
import com.itachallenge.user.helper.ObjectSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZMQServerTest {

    @Mock
    private ZContext context;

    @Mock
    private ObjectSerializer objectSerializer;

    @Mock
    private ZMQ.Socket socketMock;

    @InjectMocks
    private ZMQServer zmqServer = new ZMQServer(context, "socketAddress");

    @Test
    void run() throws Exception {

        ChallengeRequestDto challengeRequestDto = new ChallengeRequestDto();
        UUID testID = UUID.randomUUID();
        challengeRequestDto.setChallengeId(testID);

        String validJson = new ObjectMapper().writeValueAsString(challengeRequestDto);
        byte[] receivedMessage = validJson.getBytes(StandardCharsets.UTF_8);
        byte[] serializedMessage = new byte[]{1, 2, 3};

        when(context.createSocket(anyInt())).thenReturn(socketMock);
        when(socketMock.recv(0)).thenReturn(receivedMessage, (byte[]) null);

        when(objectSerializer.deserialize(receivedMessage, ChallengeRequestDto.class)).thenReturn(challengeRequestDto);
        when(objectSerializer.serialize(any(StatisticsResponseDto.class))).thenReturn(serializedMessage);


        zmqServer.run();

        verify(socketMock, times(1)).bind(anyString());
        verify(socketMock, times(1)).recv(0);
        verify(socketMock, times(1)).send(serializedMessage, 0);

        zmqServer.stop();
    }
}