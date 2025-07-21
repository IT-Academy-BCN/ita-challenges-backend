package com.itachallenge.user.controller;
import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.exception.UsernameAlreadyExistsException;
import com.itachallenge.user.service.AdminCreateUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AdminCreateUserController.class)
class AdminCreateUserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AdminCreateUserService adminCreateUserService;

    @Test
    @DisplayName("Test: POST /admin/users/create with new user should return 201 Created")
    void createUser_withNewUser_shouldReturn201Created() {

        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("newUser");

        AdminCreateUserResponseDto serviceResponse = AdminCreateUserResponseDto.builder()
                .uuid(UUID.randomUUID().toString())
                .username("newUser")
                .role("USER")
                .build();

        when(adminCreateUserService.createUser(any(AdminCreateUserRequestDto.class))).thenReturn(Mono.just(serviceResponse));

        webTestClient.post()
                .uri("/itachallenge/api/v1/admin/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AdminCreateUserResponseDto.class)
                .value(response -> {
                    assertThat(response.getUsername()).isEqualTo("newUser");
                    assertThat(response.getUuid()).isNotNull();
                });
    }

    @Test
    @DisplayName("Test: POST /admin/users/create with existing user should return 409 Conflict")
    void createUser_withExistingUser_shouldReturn409Conflict() {

        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("existingUser");

        when(adminCreateUserService.createUser(any(AdminCreateUserRequestDto.class)))
                .thenReturn(Mono.error(new UsernameAlreadyExistsException("Username existingUser already exists.")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/admin/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409);
    }
}
