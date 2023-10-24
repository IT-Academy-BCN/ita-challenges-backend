package com.itachallenge.user.helper;

import com.itachallenge.user.document.SolutionUser;
import com.itachallenge.user.dtos.SolutionUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ConverterTest {

    private Converter converter;

    @Mock
    private SolutionUser solutionUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        converter = new Converter();
    }

    @Test
    public void testFromSolutionUserToSolutionUserDtoDto() {
        UUID uuidSolutionUser = UUID.randomUUID();
        UUID uuidUser = UUID.randomUUID();
        UUID uuidChallenge = UUID.randomUUID();
        UUID uuidLanguage = UUID.randomUUID();
        String solutionText = "Sample solution text";

        when(solutionUser.getUuid_solutionUser()).thenReturn(uuidSolutionUser);
        when(solutionUser.getUuid_user()).thenReturn(uuidUser);
        when(solutionUser.getUuid_challenge()).thenReturn(uuidChallenge);
        when(solutionUser.getUuid_language()).thenReturn(uuidLanguage);
        when(solutionUser.getSolution_Text()).thenReturn(solutionText);

        Mono<SolutionUser> solutionUserFlux = Mono.just(solutionUser);

        StepVerifier.create(converter.fromSolutionUserToSolutionUserDto(solutionUserFlux))
                .expectNextMatches(solutionUserDto -> {
                    assertEquals(uuidSolutionUser, solutionUserDto.getUuid_SolutionUser());
                    assertEquals(uuidUser, solutionUserDto.getUuid_user());
                    assertEquals(uuidChallenge, solutionUserDto.getUuid_challenge());
                    assertEquals(uuidLanguage, solutionUserDto.getUuid_language());
                    assertEquals(solutionText, solutionUserDto.getSolution_Text());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void testToSolutionUserDto() {
        UUID uuidSolutionUser = UUID.randomUUID();
        UUID uuidUser = UUID.randomUUID();
        UUID uuidChallenge = UUID.randomUUID();
        UUID uuidLanguage = UUID.randomUUID();
        String solutionText = "Sample solution text";

        when(solutionUser.getUuid_solutionUser()).thenReturn(uuidSolutionUser);
        when(solutionUser.getUuid_user()).thenReturn(uuidUser);
        when(solutionUser.getUuid_challenge()).thenReturn(uuidChallenge);
        when(solutionUser.getUuid_language()).thenReturn(uuidLanguage);
        when(solutionUser.getSolution_Text()).thenReturn(solutionText);

        SolutionUserDto solutionUserDto = converter.toSolutionUserDto(solutionUser);

        assertEquals(uuidSolutionUser, solutionUserDto.getUuid_SolutionUser());
        assertEquals(uuidUser, solutionUserDto.getUuid_user());
        assertEquals(uuidChallenge, solutionUserDto.getUuid_challenge());
        assertEquals(uuidLanguage, solutionUserDto.getUuid_language());
        assertEquals(solutionText, solutionUserDto.getSolution_Text());
    }

}