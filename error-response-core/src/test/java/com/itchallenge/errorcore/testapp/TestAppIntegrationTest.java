package com.itchallenge.errorcore.testapp;

import com.itchallenge.errorcore.dto.APIErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class TestAppWebClientIntegrationTest {

    @LocalServerPort
    private int port;

    private WebClient client;

    @BeforeEach
    void setup() {
        client = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    // --- Utility for invoking endpoints ---
    private APIErrorResponse getErrorResponse(String uri) {
        try {
            return client.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(APIErrorResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            // Deserialize manually in case of non-2xx responses
            return WebClient.builder().build()
                    .get()
                    .uri("http://localhost:" + port + uri)
                    .retrieve()
                    .bodyToMono(APIErrorResponse.class)
                    .onErrorResume(ex -> Mono.empty())
                    .block();
        }
    }

    // 1️⃣ MethodArgumentTypeMismatchException
    @Test
    void shouldHandleTypeMismatch() {
        APIErrorResponse response = getErrorResponse("/test/mismatch?age=notANumber");
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).contains("Parameter");
        assertThat(response.getErrors().get(0).getField()).isEqualTo("age");
    }

    // 2️⃣ ConstraintViolationException
    @Test
    void shouldHandleConstraintViolation() {
        APIErrorResponse response = getErrorResponse("/test/violation?age=5");
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).contains("validation");
    }

    // 3️⃣ MethodArgumentNotValidException
    @Test
    void shouldHandleInvalidBody() {
        APIErrorResponse response = client.post()
                .uri("/test/invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .retrieve()
                .bodyToMono(APIErrorResponse.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).contains("Validation failed");
        assertThat(response.getErrors()).isNotEmpty();
    }

    // 4️⃣ IllegalArgumentException
    @Test
    void shouldHandleIllegalArgument() {
        APIErrorResponse response = getErrorResponse("/test/illegal");
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).contains("Invalid parameter");
    }

    // 5️⃣ ResponseStatusException
    @Test
    void shouldHandleResponseStatusException() {
        APIErrorResponse response = getErrorResponse("/test/status");
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMessage()).contains("Resource not found");
    }

    // 6️⃣ Generic Exception
    @Test
    void shouldHandleGenericException() {
        APIErrorResponse response = getErrorResponse("/test/any");
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMessage()).contains("terribly wrong");
    }
}
