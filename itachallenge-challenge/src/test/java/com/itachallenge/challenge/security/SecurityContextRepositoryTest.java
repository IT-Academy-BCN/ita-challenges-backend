package com.itachallenge.challenge.security;


import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;

import com.itachallenge.challenge.security.service.TokenService;
//import com.itachallenge.challenge.security.AuthenticationManager;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(SecurityContextRepository.class)

class SecurityContextRepositoryTest {

	final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.DRLWzvViTasSpPflis_LKJIzKLQ1qcehFX5wZd_NThQ";
	final String VALID_AUTH_HEADER = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.DRLWzvViTasSpPflis_LKJIzKLQ1qcehFX5wZd_NThQ";
	final String NOT_VALID_AUTH_HEADER = " eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwiZXhwIjoxNjc3NjQ5NDIzLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.QP7L_5GF8hS3FHdw09oq4sUZyPEtYdNCnOkEIkVS4DE";
	final String NOT_VALID_TOKEN = "QP7L_5GF8hS3FHdw09oq4sUZyPEtYdNCnOkEIkVS4DE";

	//@Autowired
    //private WebTestClient webTestClient;
	
	@MockBean
	private AuthenticationManager authenticationmanager;
	
	@MockBean
	private TokenService tokenservice;

	@InjectMocks
	private SecurityContextRepository	securitycontextrepo;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void load_test() {		
		
		  // Create mock ServerWebExchange
	    MockServerHttpRequest request = MockServerHttpRequest.get("/")
	            .header(HttpHeaders.AUTHORIZATION, VALID_AUTH_HEADER)
	            .build();
	    ServerWebExchange exchange = MockServerWebExchange.from(request);

		 when(authenticationmanager.authenticate(any(Authentication.class)))
		 .thenReturn(Mono.just(new UsernamePasswordAuthenticationToken("John", null, null)));
		 
		 Mono<SecurityContext> response = securitycontextrepo.load(exchange);
		 		 
		 StepVerifier.create(response)
		 .expectNextMatches(rsp -> rsp.getAuthentication().getName().toString().equals("John"))
		 .verifyComplete();
    }
}
