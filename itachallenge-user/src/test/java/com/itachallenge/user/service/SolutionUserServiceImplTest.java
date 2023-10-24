package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionUser;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.exceptions.SolutionUserCreationException;
import com.itachallenge.user.helper.Converter;
import com.itachallenge.user.repository.SolutionUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolutionUserServiceImplTest {

    @InjectMocks
    private SolutionUserServiceImpl solutionUserService;
    @Mock
    private SolutionUserRepository solutionUserRepository;
    @Mock
    private Converter converter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserSolutionValid() {
        UUID uuidSolutionUser = UUID.randomUUID();
        UUID uuidUser = UUID.fromString("a75fa943-6bc9-4e62-b1db-d43d15b149c7");
        UUID uuidChallenge = UUID.fromString("442b8e6e-5d57-4d12-9be2-3ff4f26e7d79");
        UUID uuidLanguage = UUID.fromString("5c1a97e5-1cca-4144-9981-2de1fb73b178");
        String solution = "Sed dictum. Proin eget odio. Aliquam vulputate ullamcorper magna. Sed eu eros. Nam consequat dolor vitae dolor. Donec fringilla. Donec";

        SolutionUser solutionUser =SolutionUser.builder()
                .uuid_solutionUser(uuidSolutionUser)
                .uuid_user(uuidUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .solution_Text(solution)
                .build();

        SolutionUserDto solutionUserDto = SolutionUserDto.builder()
                .uuid_SolutionUser(solutionUser.getUuid_solutionUser())
                .uuid_user(uuidUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .solution_Text(solution)
                .build();

        when(solutionUserRepository.save(any(SolutionUser.class)))
                .thenReturn(Mono.just(solutionUser));

        when(converter.fromSolutionUserToSolutionUserDto(any()))
                .thenReturn(Mono.just(solutionUserDto));

      SolutionUserDto result = solutionUserService.createUserSolution(uuidUser, uuidChallenge, uuidLanguage, solution).block();

        assertNotNull(result);
        assertEquals(solutionUserDto, result);

    }

    @Test
    public void testCreateUserSolutionError() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        String solutionText = "Sample solution text";

        when(solutionUserRepository.save(any(SolutionUser.class)))
                .thenReturn(Mono.error(new RuntimeException("Simulated error")));

        Mono<SolutionUserDto> result = solutionUserService.createUserSolution(userId, challengeId, languageId, solutionText);

        StepVerifier.create(result)
                .expectError(SolutionUserCreationException.class)
                .verify();
    }

}