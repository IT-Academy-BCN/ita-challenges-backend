package com.itachallenge.challenge.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DummiesConfig {

    @Value("${dummy.filters-info.path}")
    private String filterPath;

    @Value("${dummy.sort-info.path}")
    private String sortPath;
}
