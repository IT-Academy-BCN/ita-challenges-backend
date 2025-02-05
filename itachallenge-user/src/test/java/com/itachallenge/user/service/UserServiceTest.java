package com.itachallenge.user.service;

import com.itachallenge.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final String unknownUsername = "unknownUser";

    @Test
    void isMentor_WhenUserExists_ShouldReturn200OK() {
        String mentorUsername = "mentorUser";
        when(userRepository.findUsername(mentorUsername)).thenReturn(Mono.just(mentorUsername));

        StepVerifier.create(userService.isMentor(Mono.just(mentorUsername)))
                .expectNext(ResponseEntity.status(HttpStatus.OK).body(mentorUsername))
                .verifyComplete();

        verify(userRepository, times(1)).findUsername(mentorUsername);
    }

    @Test
    void isMentor_WhenUserDoesNotExist_ShouldReturn403Forbidden() {
        when(userRepository.findUsername(unknownUsername)).thenReturn(Mono.empty());

        StepVerifier.create(userService.isMentor(Mono.just(unknownUsername)))
                .expectNext(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username is not related to a mentor."))
                .verifyComplete();

        verify(userRepository, times(1)).findUsername(unknownUsername);
    }


}
