package com.itachallenge.user.service;

import static org.mockito.Mockito.*;

import com.itachallenge.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void isMentor_ShouldReturnUsername_WhenUserExists() {
        String username = "mentorUser";
        when(userRepository.findByUsernameReturnUsername(username)).thenReturn(Mono.just(username));

        StepVerifier.create(userService.isMentor(Mono.just(username)))
                .expectNext(username)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsernameReturnUsername(username);
    }

    @Test
    void isMentor_ShouldReturnEmpty_WhenUserDoesNotExist() {
        String username = "nonMentorUser";
        when(userRepository.findByUsernameReturnUsername(username)).thenReturn(Mono.empty());

        StepVerifier.create(userService.isMentor(Mono.just(username)))
                .verifyComplete();

        verify(userRepository, times(1)).findByUsernameReturnUsername(username);
    }


    @Test
    void isMentor_ShouldReturnEmpty_WhenInputIsEmptyMono() {
        StepVerifier.create(userService.isMentor(Mono.empty()))
                .verifyComplete();
    }


}

