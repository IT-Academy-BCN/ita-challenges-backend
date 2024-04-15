package com.itachallenge.user.mqserver;

import com.itachallenge.user.dtos.zmq.ChallengeRequestDto;
import com.itachallenge.user.dtos.zmq.StatisticsResponseDto;
import com.itachallenge.user.helper.ObjectSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZMQServerTest {

    private ZContext context;
    private ObjectSerializer objectSerializer;
    private ZMQServer zmqServer;

    @BeforeEach
    void setUp() throws Exception {
        context = new ZContext();
        objectSerializer = new ObjectSerializer();
        zmqServer = new ZMQServer(context, "tcp://*:5555");

        // Use reflection to set objectSerializer field
        Field field = ZMQServer.class.getDeclaredField("objectSerializer");
        field.setAccessible(true);
        field.set(zmqServer, objectSerializer);
    }

    @Test
    void run() throws Exception {
        ChallengeRequestDto challengeRequestDto = new ChallengeRequestDto();
        UUID testID = UUID.randomUUID();
        challengeRequestDto.setChallengeId(testID);

        new Thread(() -> zmqServer.run()).start();

        ZMQ.Socket client = context.createSocket(SocketType.REQ);
        client.connect("tcp://localhost:5555");

        client.send(objectSerializer.serialize(challengeRequestDto));

        byte[] reply = client.recv(0);
        StatisticsResponseDto response = objectSerializer.deserialize(reply, StatisticsResponseDto.class);

        assertEquals(99, response.getPercent());

        zmqServer.stop();
    }
}