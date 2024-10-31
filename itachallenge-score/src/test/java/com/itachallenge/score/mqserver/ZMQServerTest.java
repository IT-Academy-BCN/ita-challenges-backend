package com.itachallenge.score.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import com.itachallenge.score.helper.ObjectSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
public class ZMQServerTest {

    @Mock
    private ObjectSerializer objectSerializer;

    @InjectMocks
    private ZMQServer zmqServer;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        zmqServer.init(); // Inicia el servidor
    }

    @AfterEach
    public void tearDown() throws Exception {
        zmqServer.cleanup(); // Limpia el servidor
        closeable.close();
    }

    @Test
    public void testServerStarts() {
        // Verifica que el servidor se haya iniciado correctamente
        assertTrue(zmqServer.isRunning()); // Asegúrate de tener un método isRunning() en ZMQServer
    }

    @Test
    public void testHandleRequest() throws Exception {
        // Simula un objeto ScoreRequestDto con UUIDs válidos
        ScoreRequestDto requestDto = ScoreRequestDto.builder()
                .uuidChallenge(UUID.randomUUID())
                .uuidLanguage(UUID.randomUUID())
                .solutionText("test-solution")
                .build();

        // Simula la serialización
        byte[] serializedRequest = objectSerializer.serialize(requestDto);
        when(objectSerializer.deserialize(any(byte[].class), eq(ScoreRequestDto.class))).thenReturn(requestDto);

        // Crea un socket para enviar el mensaje
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://localhost:5555"); // Conéctate al servidor

            // Envía el mensaje
            socket.send(serializedRequest);

            // Espera la respuesta
            byte[] reply = socket.recv(0);

            // Simula la respuesta esperada
            ScoreResponseDto expectedResponse = ScoreResponseDto.builder()
                    .uuidChallenge(requestDto.getUuidChallenge())
                    .uuidLanguage(requestDto.getUuidLanguage())
                    .solutionText(requestDto.getSolutionText())
                    .score(99) // Aquí puedes calcular el puntaje real
                    .errors("xxx") // Aquí puedes calcular los errores reales
                    .build();

            // Simula la serialización de la respuesta
            byte[] serializedResponse = objectSerializer.serialize(expectedResponse);
            when(objectSerializer.serialize(any(ScoreResponseDto.class))).thenReturn(serializedResponse);

            // Verifica que la respuesta sea la esperada
            assertEquals(serializedResponse, reply);
        }
    }

    @Test
    public void testServerStops() {
        zmqServer.cleanup(); // Detiene el servidor
        assertFalse(zmqServer.isRunning()); // Asegúrate de tener un método isRunning() en ZMQServer
    }
}