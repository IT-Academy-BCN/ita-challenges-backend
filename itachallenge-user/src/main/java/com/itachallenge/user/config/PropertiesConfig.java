package com.itachallenge.user.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PropertiesConfig {
    //region PROPERTIES
    public static final int urlMaxLength = 2048;

    //endregion PROPERTIES

}
