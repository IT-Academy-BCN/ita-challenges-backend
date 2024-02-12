package com.itachallenge.user.mqserver;

import com.itachallenge.user.dtos.zmq.ChallengeRequestDto;
import com.itachallenge.user.dtos.zmq.StatisticsResponseDto;
import com.itachallenge.user.helper.ObjectSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

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
        byte[] receivedMessage = new byte[0];
        byte[] serializedMessage = new byte[0];

        when(context.createSocket(ZMQ.REP)).thenReturn(socket);
        when(socket.recv(0)).thenReturn(receivedMessage);
        when(objectSerializer.deserialize(receivedMessage, ChallengeRequestDto.class)).thenReturn(new ChallengeRequestDto());
        when(objectSerializer.serialize(any(StatisticsResponseDto.class))).thenReturn(serializedMessage);

        // Act
        zmqServer.run();

        // Assert
        verify(socket, times(1)).bind(anyString());
        verify(socket, times(1)).recv(0);
        verify(objectSerializer, times(1)).deserialize(receivedMessage, ChallengeRequestDto.class);
        verify(objectSerializer, times(1)).serialize(any(StatisticsResponseDto.class));
        verify(socket, times(1)).send(serializedMessage, 0);
    }
}
