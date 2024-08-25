package com.itachallenge.score.filter;

import com.itachallenge.score.docker.DockerContainerHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterChainSetup {

    @Bean
    public Filter createFilterChain(DockerContainerHelper dockerContainerHelper) {
        Filter unescapeFilter = new UnescapeFilter();
        Filter asciiFilter = new AsciiFilter();
        DockerJavaFilter dockerJavaFilter = new DockerJavaFilter(dockerContainerHelper);
        Filter compilationFilter = new CompilationFilter();
        Filter executionFilter = new ExecutionFilter(dockerJavaFilter);
        Filter tearDownDockerJavaFilter = new TearDownDockerJavaFilter();

        unescapeFilter.setNext(asciiFilter);
        asciiFilter.setNext(dockerJavaFilter);
        dockerJavaFilter.setNext(compilationFilter);
        compilationFilter.setNext(executionFilter);
        executionFilter.setNext(tearDownDockerJavaFilter);

        return unescapeFilter;
    }
}