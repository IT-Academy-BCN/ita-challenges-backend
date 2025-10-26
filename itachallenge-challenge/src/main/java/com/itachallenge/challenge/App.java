package com.itachallenge.challenge;

import com.itachallenge.errorcore.config.ErrorHandlingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(ErrorHandlingConfig.class)
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.itachallenge.challenge",
        "com.itachallenge.jwtcore"
})

public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}