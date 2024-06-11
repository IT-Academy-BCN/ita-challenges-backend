package com.itachallenge.score.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zeromq.ZContext;

@Configuration
public class ZMQConfig {

    @Bean
    public ZContext zContext() {
        return new ZContext();
    }
}
