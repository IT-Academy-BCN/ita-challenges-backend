package com.itachallenge.user.mqclient;

import com.itachallenge.user.helper.ObjectSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zeromq.ZMQException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZMQClientTest {

    @Mock
    private ZContext zContextMock;
    @Mock
    private ZMQ.Socket socketMock;
    @Mock
    private ObjectSerializer objectSerializerMock;
    @InjectMocks
    private ZMQClient zmqClient;

    private final String socketAddress = "tcp://localhost:5555";
    private Object message;
    private byte[] serializedMessage;

    @BeforeEach
    void setUp() {
        when(zContextMock.createSocket(ZMQ.REQ)).thenReturn(socketMock);
        message = "Test message";
        serializedMessage = "Serialized message".getBytes();
    }

    @Test
    void testSendMessage() throws Exception {

        when(socketMock.recv(0)).thenReturn("Server response".getBytes());

        when(objectSerializerMock.serialize(message)).thenReturn(serializedMessage);
        when(objectSerializerMock.deserialize("Server response".getBytes(), String.class)).thenReturn("Server response");
        System.out.println("Serialized message: " + Arrays.toString(serializedMessage));

        CompletableFuture<Object> responseFuture = zmqClient.sendMessage(message, String.class);
        responseFuture.get();

        verify(socketMock).send(serializedMessage, 0);
        responseFuture.thenAccept(result -> System.out.println("Response from future: " + result));

        verify(socketMock, times(1)).send(serializedMessage, 0);

        assertEquals("Server response", responseFuture.get());
    }
}