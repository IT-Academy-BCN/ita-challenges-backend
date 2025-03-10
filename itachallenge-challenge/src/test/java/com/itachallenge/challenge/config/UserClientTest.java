package com.itachallenge.challenge.config;

import com.itachallenge.challenge.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserClientTest {

    @Mock
    private WebClient userWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private UserClient userClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserByUsername_Success() {
        String username = "testUser";
        UserDto expectedUser = new UserDto();
        expectedUser.setUsername(username);

        when(userWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/users/{username}", username)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(expectedUser));

        Mono<UserDto> result = userClient.getUserByUsername(username);

        StepVerifier.create(result)
                .expectNext(expectedUser)
                .verifyComplete();
    }

    @Test
    public void testGetUserByUsername_NotFound() {
        String username = "nonExistentUser";

        when(userWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/users/{username}", username)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenAnswer(invocation -> {
            ClientResponse response = ClientResponse.create(HttpStatus.NOT_FOUND).build();
            return responseSpec;
        });
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.error(new RuntimeException("User not found")));

        Mono<UserDto> result = userClient.getUserByUsername(username);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testGetUserByUsername_ServerError() {
        String username = "testUser";

        when(userWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/users/{username}", username)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenAnswer(invocation -> {
            ClientResponse response = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build();
            return responseSpec;
        });
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.error(new RuntimeException("Server error in User Service")));

        Mono<UserDto> result = userClient.getUserByUsername(username);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
