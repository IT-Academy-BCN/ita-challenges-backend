package com.itachallenge.user.mqserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.user.dtos.zmq.ChallengeRequestDto;
import com.itachallenge.user.dtos.zmq.StatisticsResponseDto;

import com.itachallenge.user.helper.ObjectSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZMQServerTest {

    @Mock
    private ZContext context;

    @Mock
    private ObjectSerializer objectSerializer;

    @Mock
    private ZMQ.Socket socket;

    @InjectMocks
    private ZMQServer zmqServer = new ZMQServer(context, "socketAddress");

    @Test
    void run() throws Exception {

// Arrange
ChallengeRequestDto challengeRequestDto = new ChallengeRequestDto();
UUID testID = UUID.randomUUID();
challengeRequestDto.setChallengeId(testID);

String validJson = new ObjectMapper().writeValueAsString(challengeRequestDto);
byte[] receivedMessage = validJson.getBytes(StandardCharsets.UTF_8);
byte[] serializedMessage = new byte[]{1,2,3};

ZMQ.Socket socketMock = mock(ZMQ.Socket.class);


when(context.createSocket(anyInt())).thenReturn(socketMock);
when(socketMock.recv(0)).thenReturn(receivedMessage);
when(objectSerializer.deserialize(receivedMessage, ChallengeRequestDto.class)).thenReturn(challengeRequestDto);
when(objectSerializer.serialize(any(StatisticsResponseDto.class))).thenReturn(serializedMessage);

// Act
zmqServer.run();

// Assert
verify(socketMock, times(1)).bind(anyString());
verify(socketMock, times(1)).recv(0);
verify(objectSerializer, times(1)).deserialize(receivedMessage, ChallengeRequestDto.class);
verify(objectSerializer, times(1)).serialize(any(StatisticsResponseDto.class));
verify(socketMock, times(1)).send(serializedMessage, 0);
zmqServer.stop();

  }
}

