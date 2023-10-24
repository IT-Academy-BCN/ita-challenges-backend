package com.itachallenge.user.controller;

import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.service.SolutionUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@WebFluxTest(SolutionUserController.class)
@ExtendWith(SpringExtension.class)
class SolutionUserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SolutionUserServiceImpl solutionUserService;

    @Test
    public void testCreateUserSolution() {
        UUID idSolutionUser = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        UUID idChallenge = UUID.randomUUID();
        UUID idLanguage = UUID.randomUUID();
        String solutionText = "Ejemplo de texto de solución";

        SolutionUserDto expectedDto = new SolutionUserDto();
        expectedDto.setUuid_SolutionUser(idSolutionUser);
        expectedDto.setUuid_user(idUser);
        expectedDto.setUuid_challenge(idChallenge);
        expectedDto.setUuid_language(idLanguage);
        expectedDto.setSolution_Text(solutionText);

        Mockito.when(solutionUserService.createUserSolution(idUser, idChallenge, idLanguage, solutionText))
                .thenReturn(Mono.just(expectedDto));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new SolutionUserDto(idSolutionUser,idUser, idChallenge, idLanguage, solutionText))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.uuidSolutionUser").isNotEmpty()
                .jsonPath("$.uuidUser").isEqualTo(idUser.toString())
                .jsonPath("$.uuidChallenge").isEqualTo(idChallenge.toString())
                .jsonPath("$.uuidLanguage").isEqualTo(idLanguage.toString())
                .jsonPath("$.solutionText").isEqualTo(solutionText);
    }
}

