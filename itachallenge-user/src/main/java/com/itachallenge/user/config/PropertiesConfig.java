package com.itachallenge.user.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Getter
@Component
public class PropertiesConfig {
    //region PROPERTIES
    public static final int URLMAXLENGTH = 2048;

    //endregion PROPERTIES

}
