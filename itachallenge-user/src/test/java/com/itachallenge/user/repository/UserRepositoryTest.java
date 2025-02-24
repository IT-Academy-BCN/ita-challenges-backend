package com.itachallenge.user.repository;


import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldReturnTrueWhenUsernameExists() {
        String existingUsername = "testUser";
        when(userRepository.existsByUsername(existingUsername)).thenReturn(Mono.just(true));

        StepVerifier.create(userRepository.existsByUsername(existingUsername))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        String nonExistingUsername = "unknownUser";
        when(userRepository.existsByUsername(nonExistingUsername)).thenReturn(Mono.just(false));

        StepVerifier.create(userRepository.existsByUsername(nonExistingUsername))
                .expectNext(false)
                .verifyComplete();
    }
}

