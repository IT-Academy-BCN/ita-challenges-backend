package com.itachallenge.challenge.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.itachallenge.challenge.security.AuthenticationManager;
import com.itachallenge.challenge.security.SecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	@Autowired
    AuthenticationManager authenticationManager;
	@Autowired
    SecurityContextRepository securityContextRepository;

	@Profile("dev")
	@Bean
	protected SecurityWebFilterChain configureDev(ServerHttpSecurity http) {
		return http
                .authorizeExchange(exchange -> exchange
                        .anyExchange()
                        .permitAll())
                .build();
	}

	@Profile("prod")
	@Bean
	protected SecurityWebFilterChain configureProd(ServerHttpSecurity http) {
		

		return http
				.csrf().disable()
				.authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
				.authorizeExchange()
				.pathMatchers("/itachallenge/api/v1/challenge/test")
				.permitAll()
				.pathMatchers(HttpMethod.POST).hasRole("ADMIN")
				.pathMatchers(HttpMethod.DELETE).hasRole("ADMIN")
				.pathMatchers(HttpMethod.PUT).hasRole("ADMIN")
				.anyExchange()
				.authenticated()
				.and()
				.formLogin()
				.and()
				.build();
	}
}
