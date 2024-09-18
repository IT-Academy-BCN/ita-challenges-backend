package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.sandbox.sandbox_container.JavaSandboxContainer;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class FilterChainSetup {

    private static final Logger log = getLogger(FilterChainSetup.class.getName());

    @Bean
    public Filter createFilterChain(JavaContainerFilter javaContainerFilter, CompileExecuterFilter compileExecuterFilter, JavaSandboxContainer javaSandboxContainer) {
        Filter escapeFilter = new UnescapeFilter();
        Filter asciiFilter = new AsciiFilter();

        escapeFilter.setNext(asciiFilter);
        asciiFilter.setNext(javaContainerFilter);
        javaContainerFilter.setNext(compileExecuterFilter);

        return escapeFilter;
    }

}