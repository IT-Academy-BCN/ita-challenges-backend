package com.itachallenge.challenge;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(title = "Ita Backend Challenges", version = "1.0", description = "Description"))
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}