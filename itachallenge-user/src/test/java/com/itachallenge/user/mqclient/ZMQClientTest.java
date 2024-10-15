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

import java.util.concurrent.CompletableFuture;

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
        message = "Test message";
        serializedMessage = "Serialized message".getBytes();
    }

    @Test
    void testSendMessage() throws Exception {

        // Mockear el comportamiento del ZMQ.Socket
        when(zContextMock.createSocket(ZMQ.REQ)).thenReturn(socketMock);

        // Simula respuesta del servidor
        when(socketMock.recv(0)).thenReturn("Server response".getBytes());

        // Simula la serialización y deserialización
        when(objectSerializerMock.serialize(message)).thenReturn(serializedMessage);
        when(objectSerializerMock.deserialize("Server response".getBytes(), String.class)).thenReturn("Server response");

        // Ejecutar el método sendMessage
        CompletableFuture<Object> responseFuture = zmqClient.sendMessage(message, String.class);

        // Verificar que el mensaje fue enviado correctamente
        verify(socketMock).send(serializedMessage, 0);

        // Verificar que la respuesta sea la esperada
        assertEquals("Server response", responseFuture.get());
    }
}