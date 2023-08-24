package com.itachallenge.score.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
public class SecurityConfig_Test {

	@Autowired
	private WebTestClient webtestclient;

	@Test
	public void testProfileConfigAnyUser() {
		// any user can access this endpoint
		webtestclient.get().uri("/itachallenge/api/v1/score/test").exchange().expectStatus().isOk();
	}

	@Test
	public void testGetMethodProfileConfigWithNOTAuthenticatedUser() {
		// only authenticated users can acces get endpoints, if they arent, then they
		// are redirected to login page
		webtestclient.get().uri("/itachallenge/api/v1/score/scores").exchange().expectStatus().is3xxRedirection();
	}

	/*@Test
	@WithMockUser
	public void testGetMethodProfileConfigWithAuthenticatedUser() {
		// mockuser is authenticated so it is allowed to the get endpoint
		webtestclient.get().uri("/itachallenge/api/v1/score/scores").exchange().expectStatus().isOk();
	}*/

}
