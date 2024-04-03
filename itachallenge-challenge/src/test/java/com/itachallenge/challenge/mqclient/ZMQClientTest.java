package com.itachallenge.challenge.mqclient;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private ZMQClient zmqClient = new ZMQClient(context, "tcp://localhost:5555");
    //private ZMQClient zmqClient;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testSendMessage() throws Exception {

        // Arrange: Crear el mensaje y preparar el entorno
        byte[] message = "Message".getBytes(); // Crear el mensaje que deseas probar
        when(objectSerializer.serialize(any())).thenReturn(new byte[0]); // Configurar el comportamiento del mock

        // Act: Ejecutar el método bajo prueba
        CompletableFuture<Object> future = zmqClient.sendMessage(message, Object.class);

        // Assert: Verificar el comportamiento y resultado
        assertEquals("Response", future.join()); // Comprobar el resultado esperado

    }
}