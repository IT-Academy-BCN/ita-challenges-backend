package com.itachallenge.challenge.mqclient;

import com.itachallenge.challenge.helper.ObjectSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZMQClientTest {

    @Mock
    private ZContext context;

    @Mock
    private ObjectSerializer objectSerializer;

    @Mock
    private ZMQ.Socket socket;

    @InjectMocks
    private ZMQClient zmqClient = new ZMQClient(context, "socketAddress");

    @Test
    void sendMessage() throws Exception {
        Object message = new Object();
        Class clazz = Object.class;
        byte[] serializedMessage = new byte[0];
        byte[] receivedMessage = new byte[0];
        Object deserializedMessage = new Object();

        when(context.createSocket(ZMQ.REQ)).thenReturn(socket);
        when(objectSerializer.serialize(message)).thenReturn(serializedMessage);
        when(socket.recv(0)).thenReturn(receivedMessage);
        when(objectSerializer.deserialize(receivedMessage, clazz)).thenReturn(deserializedMessage);

        // Act
        CompletableFuture<Object> future = zmqClient.sendMessage(message, clazz);
        Object result = future.get();

        // Assert
        verify(socket, times(1)).connect(anyString());
        verify(socket, times(1)).send(serializedMessage, 0);
        verify(socket, times(1)).recv(0);
        assertEquals(deserializedMessage, result);
    }

}