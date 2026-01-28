package com.itachallenge.auth;

import com.itachallenge.githubcore.config.GithubServiceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(GithubServiceConfig.class)
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.itachallenge.auth",
        "com.itachallenge.jwtcore"
})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}