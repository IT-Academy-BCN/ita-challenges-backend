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

import com.itachallenge.challenge.dto.RelatedDto;

import static org.hamcrest.Matchers.equalTo;

import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ChallengeControllerTest {
	
	private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";
	private final static String VALID_ID = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
	private final static String NO_VALID_ID = "4029-8e9b-284c8ca80296";
	
	@Autowired
	private WebTestClient webTestClient;
	
    @Test
    @DisplayName("Test EndPoint: test")
    void TestEndPoint_test(){
        final String URI_TEST = "/test";
        webTestClient.get()
                .uri(CHALLENGE_BASE_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(s -> s, equalTo("Hello from ITA Challenge!!!"));
    }


	@Test
	@DisplayName("Test EndPoint: Set related")
	void testRelatedChallenge_VALID_SET() {
		final String URI_RELATED = "/{id}/related";
		RelatedDto rel2 = new RelatedDto("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0");
		
		webTestClient.get().uri(CHALLENGE_BASE_URL + URI_RELATED, VALID_ID)
		.accept(MediaType.ALL)
		.exchange()
		.expectStatus().isOk()
		.expectBody(Set.class)
		.value(set -> set.contains(rel2));
	
	}

	@Test
	@DisplayName("Test EndPoint: not UUID")
	void testRelatedChallenge_NOT_VALID_UUID() {
		final String URI_RELATED = "/{id}/related";

		webTestClient.get().uri(CHALLENGE_BASE_URL + URI_RELATED, NO_VALID_ID)
		.accept(MediaType.ALL)
		.exchange()
		.expectStatus()
		.isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

	}
}