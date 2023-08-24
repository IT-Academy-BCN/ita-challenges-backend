package com.itachallenge.score.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.itachallenge.score.security.service.TokenService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

	// private final Builder webClient;

	private TokenService tokenservice;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String jwtToken = authentication.getCredentials().toString();
		return tokenservice.extractAuthenticationFromToken(jwtToken);

	}

}