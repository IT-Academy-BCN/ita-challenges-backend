package com.itachallenge.challenge.controller;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ChallengeControllerTest {
	@Autowired
	private WebTestClient webTestClient;
	private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";
	private final static String VALID_ID = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
	private final static String NO_VALID_ID = "4029-8e9b-284c8ca80296";

	@Test
	@DisplayName("Test EndPoint: related")
	void testRelatedChallenge_VALID_CLASS() {
		final String URI_RELATED = "/getOne/{id}/related";

		webTestClient.get().uri(CHALLENGE_BASE_URL + URI_RELATED, VALID_ID)
		.accept(MediaType.ALL)
		.exchange()
		.expectStatus().isOk()
		.expectBody(Set.class);
	}

	@Test
	@DisplayName("Test EndPoint: not UUID")
	void testRelatedChallenge_NOT_UUID() {
		final String URI_RELATED = "/getOne/{id}/related";

		webTestClient.get().uri(CHALLENGE_BASE_URL + URI_RELATED, NO_VALID_ID)
		.accept(MediaType.ALL)
		.exchange()
		.expectStatus()
		.isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

	}
}