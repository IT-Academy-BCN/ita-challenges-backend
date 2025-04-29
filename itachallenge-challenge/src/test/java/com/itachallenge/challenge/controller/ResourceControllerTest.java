package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.enums.AssociationType;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import com.itachallenge.challenge.service.IResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


import java.util.List;
import java.util.UUID;


@WebFluxTest(ResourceController.class)
@ActiveProfiles("test")
class ResourceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IResourceService resourceService;

    @Test
    void createNewResource_ValidRequest_ReturnsCreatedResource() {
        ResourceDto resourceDto = ResourceDto.builder()
                .resourceId(UUID.randomUUID())
                .title("Test Resource")
                .description("Test Description")
                .url("https://example.com")
                .topic(Topic.LISTS)
                .contentType(ResourceContentType.VIDEO)
                .challengeIds(List.of(UUID.randomUUID()))
                .associationType(AssociationType.NONE)
                .build();

        when(resourceService.createResource(any(ResourceDto.class)))
                .thenReturn(Mono.just(resourceDto));

        webTestClient.post()
                .uri("/itachallenge/api/v1/resource/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resourceDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResourceDto.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(resourceDto.getTitle(), response.getTitle());
                    assertEquals(resourceDto.getDescription(), response.getDescription());
                });

        verify(resourceService, times(1)).createResource(any(ResourceDto.class));
    }


    @Test
    void createNewResource_InvalidRequest_ReturnsBadRequest() {


        webTestClient.post()
                .uri("/itachallenge/api/v1/resource/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"title\": }")
                .exchange()
                .expectStatus().isBadRequest();

        verify(resourceService, never()).createResource(any(ResourceDto.class));
    }

    @Test
    void createNewResource_MissingRequiredFields_ReturnsBadRequest() {
        ResourceDto invalidResource = ResourceDto.builder()
                .resourceId(UUID.randomUUID())
                .title("")  // Title buit
                .description("Valid Description")
                .url("https://example.com")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of(UUID.randomUUID()))
                .associationType(AssociationType.NONE)
                .build();

        webTestClient.post()
                .uri("/itachallenge/api/v1/resource/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidResource)
                .exchange()
                .expectStatus().isBadRequest();

        verify(resourceService, never()).createResource(any(ResourceDto.class));
    }



    @Test
    void getResourcesByChallengeId_ValidId_ReturnsResources() {
        // 1. Datos de prueba
        UUID challengeId = UUID.randomUUID();
        ResourceDto resource1 = ResourceDto.builder()
                .resourceId(UUID.randomUUID())
                .title("Resource 1")
                .description("Desc 1")
                .url("https://example.com/1")
                .topic(Topic.DEBUGGING)
                .contentType(ResourceContentType.VIDEO)
                .challengeIds(List.of(challengeId))
                .build();

        ResourceDto resource2 = ResourceDto.builder()
                .resourceId(UUID.randomUUID())
                .title("Resource 2")
                .description("Desc 2")
                .url("https://example.com/2")
                .topic(Topic.COMPONENTS)
                .contentType(ResourceContentType.BLOG)
                .challengeIds(List.of(challengeId))
                .build();

        // 2. Mock del servicio
        when(resourceService.getResourcesByChallengeId(challengeId))
                .thenReturn(Flux.just(resource1, resource2));

        // 3. Ejecutar y verificar la petición HTTP
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/itachallenge/api/v1/resource/resources")
                        .queryParam("challengeId", challengeId.toString())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResourceDto.class)
                .hasSize(2)
                .value(resources -> {
                    assertEquals(resource1.getTitle(), resources.get(0).getTitle());
                    assertEquals(resource2.getUrl(), resources.get(1).getUrl());
                });

        // 4. Verificar interacción con el servicio
        verify(resourceService, times(1)).getResourcesByChallengeId(challengeId);
    }




}
