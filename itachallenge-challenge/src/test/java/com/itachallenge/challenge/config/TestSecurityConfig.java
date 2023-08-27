package com.itachallenge.challenge.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@TestConfiguration
@EnableWebFluxSecurity
public class TestSecurityConfig {

	protected SecurityWebFilterChain configureDev(ServerHttpSecurity http) {
		return http
                .authorizeExchange(exchange -> exchange
                        .anyExchange()
                        .permitAll())
                .build();
	}
}