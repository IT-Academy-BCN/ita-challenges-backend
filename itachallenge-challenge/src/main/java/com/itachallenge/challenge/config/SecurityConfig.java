package com.itachallenge.challenge.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.context.annotation.Profile;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.http.HttpMethod;


import com.itachallenge.challenge.security.AuthenticationManager;
import com.itachallenge.challenge.security.SecurityContextRepository;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	String admin = "ADMIN";
	
	@Autowired
	AuthenticationManager authenticationmanager;
	
	@Autowired
	SecurityContextRepository securitycontextrepository;

	@Profile("dev")
	@Bean
	protected SecurityWebFilterChain configureDev(ServerHttpSecurity http) {
		return http
                .csrf().disable()
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
                .authenticationManager(authenticationmanager)
                .securityContextRepository(securitycontextrepository)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/itachallenge/api/v1/challenge/test")
                        .permitAll()
                        .pathMatchers(HttpMethod.POST).hasRole(admin)
                        .pathMatchers(HttpMethod.DELETE).hasRole(admin)
                        .pathMatchers(HttpMethod.PUT).hasRole(admin)
                        .anyExchange()
                        .authenticated())
                .formLogin(withDefaults())
                .build();
	}
}


