package com.itachallenge.user.controller;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.service.AdminCreateUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ✅ AdminCreateUserControllerHappyPathTest
 * Focus: Only successful flows (201 Created).
 */
@WebFluxTest(controllers = AdminCreateUserController.class)
@ContextConfiguration(classes = { AdminCreateUserController.class })
@ActiveProfiles("test")
class AdminCreateUserControllerHappyPathTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AdminCreateUserService adminCreateUserService;

    @Test
    @DisplayName("POST /admin/users/create with new user → 201 Created")
    void createUser_withNewUser_shouldReturn201Created() {
        // Arrange
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("newUser");

        AdminCreateUserResponseDto serviceResponse = AdminCreateUserResponseDto.builder()
                .userId(UUID.randomUUID().toString())
                .username("newUser")
                .build();

        when(adminCreateUserService.createUser(any(AdminCreateUserRequestDto.class)))
                .thenReturn(Mono.just(serviceResponse));

        // Act & Assert
        webTestClient.post()
                .uri("/itachallenge/api/v1/admin/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AdminCreateUserResponseDto.class)
                .value(response -> {
                    assertThat(response.getUsername()).isEqualTo("newUser");
                    assertThat(response.getUserId()).isNotNull();
                });
    }
}
