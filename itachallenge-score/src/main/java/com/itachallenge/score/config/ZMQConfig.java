package com.itachallenge.score.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zeromq.ZContext;

@Configuration
public class ZMQConfig {

    @Value("${zeromq.socket.address}")
    String socketAddress;

    @Value("${zeromq.context.threads}")
    private int contextThreads;


    @Bean
    public ZContext zContext() {
        return new ZContext(contextThreads);
    }

    @Bean public String socketAddress() {
        return socketAddress;
    }
}

