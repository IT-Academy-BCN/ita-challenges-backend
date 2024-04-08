package com.itachallenge.challenge.mqclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.challenge.helper.ObjectSerializer;
import com.itachallenge.challenge.helper.ObjectSerializerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.*;

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
    private ZMQClient zmqClient;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        zmqClient = new ZMQClient(context, "tcp://localhost:5555");
        when(context.createSocket(ZMQ.REQ)).thenReturn(socket);
        objectSerializer = mock(ObjectSerializer.class); // Asegurarse de que objectSerializer no sea null
    }


    @Test
    void testSendMessage() throws IOException, ExecutionException, InterruptedException {
        // Arrange
        Object message = new Object();
        Class<Object> clazz = Object.class;
        byte[] serializedMessage = new byte[]{1, 2, 3};
        Object deserializedMessage = new Object();

        when(objectSerializer.serialize(message)).thenReturn(serializedMessage);
        when(objectSerializer.deserialize(serializedMessage, clazz)).thenReturn(deserializedMessage);
        when(socket.recv(0)).thenReturn(serializedMessage);

        // Act
        CompletableFuture<Object> future = zmqClient.sendMessage(message, clazz);

        // Assert
        assertEquals(deserializedMessage, future.get());
        verify(socket).send(serializedMessage, 0);
        verify(socket).recv(0);
    }
}
