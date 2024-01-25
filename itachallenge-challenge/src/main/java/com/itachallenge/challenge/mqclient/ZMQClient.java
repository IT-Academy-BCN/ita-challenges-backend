package com.itachallenge.challenge.mqclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

@Component
public class ZMQClient {

    private final ZContext context;
    private final String SOCKET_ADDRESS;

    public ZMQClient(ZContext context, @Value("${zeromq.socket.address}") String socketAddress){
        this.context = context;
        this.SOCKET_ADDRESS = socketAddress;
    }

    public void sendMessage(String message){
        new Thread(() -> {
            try (ZContext context = new ZContext()) {
                // Socket to talk to server
                ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
                socket.connect(SOCKET_ADDRESS);

                // Send a message
                socket.send(message.getBytes(ZMQ.CHARSET), 0);

                // Receive a response
                byte[] reply = socket.recv(0);
                // Print the message
                System.out.println("Received: [" + new String(reply, ZMQ.CHARSET) + "]");
            }
        }).start();
    }
}