package com.itachallenge.score.config;

import com.itachallenge.score.docker.DockerContainerHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerContainerHelperConfig {

    @Bean
    public DockerContainerHelper dockerContainerHelper() {
        return new DockerContainerHelper();
    }
}