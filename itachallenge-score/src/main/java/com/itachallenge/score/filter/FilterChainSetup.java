package com.itachallenge.score.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FilterChainSetup {

    private FilterChainSetup() {
    }

    @Bean
    public static Filter createFilterChain() {

        DockerJavaFilter dockerJavaFilter = new DockerJavaFilter();
        TearDownDockerJavaFilter tearDownDockerJavaFilter = new TearDownDockerJavaFilter();


        Filter escapeFilter = new UnescapeFilter();
        Filter asciiFilter = new AsciiFilter();
        Filter compilationFilter = new CompilationFilter();
        Filter executionFilter = new ExecutionFilter();

        escapeFilter.setNext(asciiFilter);
        asciiFilter.setNext(dockerJavaFilter);
        dockerJavaFilter.setNext(compilationFilter);
        compilationFilter.setNext(executionFilter);
        executionFilter.setNext(tearDownDockerJavaFilter);
        tearDownDockerJavaFilter.setSandboxContainer(dockerJavaFilter.getSandboxContainer());

        return escapeFilter;
    }
}
