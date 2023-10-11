package com.itachallenge.user.config;

import com.itachallenge.user.converters.BinaryToUUIDConverter;
import com.itachallenge.user.converters.UUIDToBinaryConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class ConvertersConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {

        return new MongoCustomConversions(
                Arrays.asList(
                        new UUIDToBinaryConverter(),
                        new BinaryToUUIDConverter()));
    }
}
