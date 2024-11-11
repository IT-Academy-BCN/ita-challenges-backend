package com.itachallenge.user.mqclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.user.helper.ObjectSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zeromq.ZMQException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    private Object message;
    private byte[] serializedMessage;

    @BeforeEach
    void setUp() {
        lenient().when(zContextMock.createSocket(SocketType.REQ)).thenReturn(socketMock);
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

    @Test
    void testSendMessageThrowsIllegalArgumentException() {
        CompletableFuture<Object> responseFuture = zmqClient.sendMessage(null, String.class);

        ExecutionException executionException = assertThrows(ExecutionException.class, responseFuture::get);
        assertTrue(executionException.getCause() instanceof IllegalArgumentException);
        assertEquals("Message cannot be null", executionException.getCause().getMessage());
    }

    @Test
    void testSendMessageNullReply() throws Exception {
        when(socketMock.recv(0)).thenReturn(null);
        when(objectSerializerMock.serialize(message)).thenReturn(serializedMessage);

        CompletableFuture<Object> responseFuture = zmqClient.sendMessage(message, String.class);

        ExecutionException executionException = assertThrows(ExecutionException.class, responseFuture::get);
        assertTrue(executionException.getCause() instanceof ZMQException);
        assertEquals("Received null reply from ZeroMQ", executionException.getCause().getMessage());
    }

    @Test
    void testSerializeMessageException() throws JsonProcessingException {
        when(objectSerializerMock.serialize(message)).thenThrow(new JsonProcessingException("Serialization error") {
        });
        CompletableFuture<Object> responseFuture = zmqClient.sendMessage(message, String.class);

        ExecutionException executionException = assertThrows(ExecutionException.class, responseFuture::get);
        assertTrue(executionException.getCause() instanceof JsonProcessingException);
        assertEquals("Serialization error", executionException.getCause().getMessage());
    }
}