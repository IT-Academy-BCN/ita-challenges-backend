package com.itachallenge.challenge.security;


import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;

import com.itachallenge.challenge.security.service.TokenService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(TokenService.class)
@ContextConfiguration // (loader = AnnotationConfigContextLoader.class)

class AuthenticationManagerTest {

	final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.DRLWzvViTasSpPflis_LKJIzKLQ1qcehFX5wZd_NThQ";
	final String VALID_AUTH_HEADER = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.DRLWzvViTasSpPflis_LKJIzKLQ1qcehFX5wZd_NThQ";
	final String NOT_VALID_AUTH_HEADER = " eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwiZXhwIjoxNjc3NjQ5NDIzLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.QP7L_5GF8hS3FHdw09oq4sUZyPEtYdNCnOkEIkVS4DE";
	final String NOT_VALID_TOKEN = "QP7L_5GF8hS3FHdw09oq4sUZyPEtYdNCnOkEIkVS4DE";

	@MockBean
	private TokenService tokenservice;

	@InjectMocks
	private AuthenticationManager authenticationmanager;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void authenticate_test() {
		
		Authentication auth = new UsernamePasswordAuthenticationToken(VALID_TOKEN, VALID_TOKEN);
		
		Mono<Authentication> authMono = Mono.just(auth);
		
		when(tokenservice.extractAuthenticationFromToken(VALID_TOKEN)).thenReturn(authMono);
		
		Mono<Authentication>  response = authenticationmanager.authenticate(auth);
		
		StepVerifier.create(response)
		.expectNextMatches(rsp -> rsp.getName().equalsIgnoreCase(auth.getName()))
		.verifyComplete();
	}


}


