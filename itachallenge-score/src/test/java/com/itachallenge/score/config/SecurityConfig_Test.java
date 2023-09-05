package com.itachallenge.score.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles(profiles = "prod")
class SecurityConfig_Test {

	@Autowired
	private WebTestClient webtestclient;

	@Test
	void testProfileConfigAnyUser() {
		// any user can access this endpoint
		webtestclient.get().uri("/itachallenge/api/v1/score/test").exchange().expectStatus().isOk();
	}

	@Test
	void testGetMethodProfileConfigWithNOTAuthenticatedUser() {
		// only authenticated users can acces get endpoints, if they arent, then they
		// are redirected to login page
		webtestclient.get().uri("/itachallenge/api/v1/score/scores").exchange().expectStatus().is3xxRedirection();
	}

}
