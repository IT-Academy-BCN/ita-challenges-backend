package com.itachallenge.user.mqserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

@Component
public class ZMQServer {

    private final ZContext context;
    private final String SOCKET_ADDRESS;

    public ZMQServer(ZContext context, @Value("${zeromq.socket.address}") String socketAddress){
        this.context = context;
        this.SOCKET_ADDRESS = socketAddress;
    }

    public void run(){
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] reply = socket.recv(0);
                // Print the message
                System.out.println("Received: [" + new String(reply, ZMQ.CHARSET) + "]");
                // Send a response
                String response = "Hello, world!";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        }
    }
}

