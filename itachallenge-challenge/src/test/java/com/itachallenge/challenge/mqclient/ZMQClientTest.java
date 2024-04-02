package com.itachallenge.challenge.mqclient;

import com.itachallenge.challenge.helper.ObjectSerializer;
import com.itachallenge.challenge.helper.ObjectSerializerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    //private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Test
    public void testSendMessage() throws Exception {
        //create a mock object of ZContext and ZMQ.Socket
        ZContext context = Mockito.mock(ZContext.class);
        ZMQ.Socket socket = Mockito.mock(ZMQ.Socket.class);

        //mock the behavior of context.createSocket() to return the mocked socket
        when(context.createSocket(any())).thenReturn(socket);

        //mock the behavior of socket.recv() to return a specific byte array
        when(socket.recv(any())).thenReturn("response".getBytes());









        /*
        // Arrange
        Object message = new Object();
        byte[] serializedMessage = "Mensaje serializado".getBytes();
        when(objectSerializer.serialize(message)).thenReturn(serializedMessage);

        // Act
        Object deserializedResponse = "Mensaje de respuesta";
        Class<?> clazz = Object.class; // o cualquier otra clase que esperes como respuesta
        CompletableFuture<Object> future = zmqClient.sendMessage(message, clazz);

        // Assert
        assertTrue(future.isDone()); // Verificar que el CompletableFuture esté completo
        assertEquals(deserializedResponse, future.get()); // Verificar que el resultado coincida con el mensaje de respuesta esperado
        verify(socket).send(eq(serializedMessage), eq(0)); // Verificar que se envió el mensaje serializado al socket
    }*/
    }
}

    // add a unit test for the sendMessage method

    /*@Test
    void sendMessage() throws IOException, ExecutionException, InterruptedException {
        // Arrange
        ZMQClient zmqClient = new ZMQClient(context, "tcp://localhost:5555");
        ZContext context = Mockito.mock(ZContext.class);
        when(context.createSocket(ZMQ.REQ)).thenReturn(socket);

        // Act
        CompletableFuture<Object> future = zmqClient.sendMessage(objectSerializer, ObjectSerializerTest.TestObject.class);

        // Assert
        assertNotNull(future);
        // Verifica que se cree el socket y se conecte
        verify(context, times(1)).createSocket(ZMQ.REQ);
        verify(socket, times(1)).connect("tcp://localhost:5555");

    }*/
/*@Test
    public void testSendMessage() throws Exception {
        // Mock ZContext and ZMQ.Socket
        ZContext context = Mockito.mock(ZContext.class);
        ZMQ.Socket socket = Mockito.mock(ZMQ.Socket.class);

        // Mock the behavior of context.createSocket() to return the mocked socket
        when(context.createSocket(any())).thenReturn(socket);

        // Mock the behavior of socket.recv() to return a specific byte array
        when(socket.recv(any())).thenReturn("response".getBytes());

        // Mock the behavior of socket.send() to do nothing (since it's void)
        Mockito.doNothing().when(socket).send(any(byte[].class), any());

        // Mock the behavior of socket.connect() to do nothing (since it's void)
        Mockito.doNothing().when(socket).connect(any());

        // Create an instance of ZMQClient with the mocked context
        ZMQClient client = new ZMQClient(context, "address");

        // Call the method under test
        CompletableFuture<Object> future = client.sendMessage("request", String.class);

        // Assert that the response is as expected
        assertEquals("response", future.get());
    }


}
*/