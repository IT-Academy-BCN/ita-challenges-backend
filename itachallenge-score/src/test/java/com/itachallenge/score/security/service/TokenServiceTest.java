package com.itachallenge.score.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import com.itachallenge.score.config.SecurityPropertiesConfig;
import com.itachallenge.score.security.service.exceptions.InvalidTokenException;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(TokenService.class)


class TokenServiceTest {

	final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.DRLWzvViTasSpPflis_LKJIzKLQ1qcehFX5wZd_NThQ";
	final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZXhwIjoxNTE2MjM5MDIyLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.pdCt_570CT7A7xJJafAOPlxsU1279Rdqf4SWzZrVuxk";
	final String NOT_VALID_TOKEN = "QP7L_5GF8hS3FHdw09oq4sUZyPEtYdNCnOkEIkVS4DE";

	@MockBean
	private SecurityPropertiesConfig config;

	@InjectMocks
	private TokenService tokenservice;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName(value = "valid token test")
	void ValidateTokenValid_Token_test() throws UnsupportedEncodingException {

		Claims claims = Jwts.claims();
		claims.setSubject("1234567890");
		claims.put("name", "John Doe");
		claims.setIssuedAt(new Date(1516239022));
		claims.put("roles", List.of("ROLE_USER", "ROLE_ADMIN"));

		String secret = "SgVkYp3s6v9yBEH+MbQeThWmZq4t7p09ventepamadrid";

		when(config.getSecret()).thenReturn(secret);

		Mono<Claims> response = tokenservice.validateToken(VALID_TOKEN);

		StepVerifier.create(response)
				.expectNextMatches(rsp -> rsp.getSubject().equals(claims.getSubject()) && rsp.containsKey("name")
						&& rsp.containsKey("iat") && rsp.containsKey("roles") && rsp.get("name").equals("John Doe"))
				.verifyComplete();

	}
	
	@Test
	@DisplayName(value = "invalid token test")
	void ValidateToken_InValid_Token_test(){

		String secret = "SgVkYp3s6v9yBEH+MbQeThWmZq4t7p09ventepamadrid";

		when(config.getSecret()).thenReturn(secret);

		Mono<Claims> response = tokenservice.validateToken(NOT_VALID_TOKEN);

		StepVerifier.create(response)
		.expectErrorMatches(throwable -> throwable instanceof InvalidTokenException
				&& throwable.getMessage().equals("Invalid token"))
		.verify();
			
	}
	
	@Test
	@DisplayName(value = "EXPIRED token test")
	void ValidateToken_Expired_Token_test(){

		String secret = "SgVkYp3s6v9yBEH+MbQeThWmZq4t7p09ventepamadrid";

		when(config.getSecret()).thenReturn(secret);
		when(config.getTokenExpiration()).thenReturn(System.currentTimeMillis()-1000);

		Mono<Authentication> response = tokenservice.extractAuthenticationFromToken(VALID_TOKEN);

		StepVerifier.create(response)
		.expectError()
		.verify();		
	}

}
