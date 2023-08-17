package com.itachallenge.challenge.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component // @Configuration not used due there isn't any @bean method
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.security.datasource")
@AllArgsConstructor
@Setter
@Getter

public class SecurityPropertiesConfig {

	private String secret;
	private String headerString;
	private String authoritiesClaim;
	private String err;

}
