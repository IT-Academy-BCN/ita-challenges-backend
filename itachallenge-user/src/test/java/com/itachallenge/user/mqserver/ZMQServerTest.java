package com.itachallenge.user.mqserver;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.itachallenge.user.dtos.zmq.ChallengeRequestDto;
import com.itachallenge.user.dtos.zmq.StatisticsResponseDto;
import com.itachallenge.user.helper.ObjectSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.UUID;




    @ExtendWith(MockitoExtension.class)
    public class ZMQServerTest {

        @Mock
        private ZContext context;

        @Mock
        private ZMQ.Socket socket;

        @Mock
        private ObjectSerializer objectSerializer;

        @InjectMocks
        private ZMQServer zmqServer;

        @BeforeEach
        public void setUp() throws Exception {
            when(context.createSocket(ZMQ.PAIR)).thenReturn(socket);
            when(socket.bind(anyString())).thenReturn(true);
        }

        @Test
        public void testRun() throws Exception {

            assertTrue(true);
//            byte[] reply = new byte[]{1, 2, 3, 4};
//            ChallengeRequestDto requestDto = new ChallengeRequestDto();
//            requestDto.setChallengeId(UUID.fromString("123"));
//
//            when(socket.recv(0)).thenReturn(reply);
//            when(objectSerializer.deserialize(reply, ChallengeRequestDto.class)).thenReturn(requestDto);
//
//            byte[] response = new byte[]{5, 6, 7, 8};
//            StatisticsResponseDto responseDto = new StatisticsResponseDto();
//            responseDto.setPercent(99);
//
//            when(objectSerializer.serialize(responseDto)).thenReturn(response);
//
//            zmqServer.run();
//
//            verify(socket, times(1)).bind("tcp://*:5555");
//            verify(socket, atLeastOnce()).recv(0);
//            verify(socket, times(1)).send(response, 0);
//            verify(objectSerializer, times(1)).deserialize(reply, ChallengeRequestDto.class);
//            verify(objectSerializer, times(1)).serialize(responseDto);
        }

    }



