package com.itachallenge.submission.openapi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class OpenApiContractTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void submissionDto_uuidUser_shouldBeDeprecatedInOpenApi() {
        webTestClient.get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.components.schemas.SubmissionDto.properties.uuid_user.deprecated")
                .isEqualTo(true);
    }
}