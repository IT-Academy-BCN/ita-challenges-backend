package com.itachallenge.auth.service;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient.RequestBodyUriSpec RequestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AuthService authService;

    @Before
    public void setUp() {
        /*
            TODO: Mockear la url de Service para que invoque mock y preparar la respuesta
         */
        // Configura el WebClient para devolver el mock de RequestHeadersUriSpec cuando se llama a post()
        when(webClient.post()).thenReturn(RequestBodyUriSpec);

        // Configura el RequestHeadersUriSpec para devolver el mock de RequestBodySpec cuando se llama a uri()
        when(RequestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);

        // Configura el RequestBodySpec para devolver el mock de ResponseSpec cuando se llama a retrieve()
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        // Configura el ResponseSpec para devolver un Mono cuando se llama a bodyToMono()
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
    }

    @Test
    public void testWebClientMock() {

        // Ahora, cuando llames a authService.validateWithSSO(), debería devolver "response"
        Mono<Boolean> response = authService.validateWithSSO("token");
        assertEquals(true, response.block());
    }

}
