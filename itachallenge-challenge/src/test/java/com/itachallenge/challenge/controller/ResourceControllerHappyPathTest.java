package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.enums.AssociationType;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import com.itachallenge.challenge.repository.*;
import com.itachallenge.challenge.service.IResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(ResourceController.class)
@ActiveProfiles("test")
class ResourceControllerHappyPathTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean private IResourceService resourceService;
    @MockBean private DiscoveryClient discoveryClient;
    @MockBean private ChallengeRepository challengeRepository;
    @MockBean private SolutionRepository solutionRepository;
    @MockBean private WebClient.Builder webClientBuilder;
    @MockBean private TagRepository tagRepository;
    @MockBean private ResourceRepository resourceRepository;
    @MockBean private MappingMongoConverter mappingMongoConverter;
    @MockBean private LanguageRepository languageRepository;

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

        verify(resourceService).createResource(any(ResourceDto.class));
    }

    @Test
    void getResourcesByChallengeId_ValidId_ReturnsResources() {
        UUID challengeId = UUID.randomUUID();
        ResourceDto mockResource = ResourceDto.builder()
                .resourceId(UUID.randomUUID())
                .title("Test Resource")
                .description("Test Description")
                .url("http://test.com")
                .topic(Topic.COMPONENTS)
                .contentType(ResourceContentType.VIDEO)
                .challengeIds(List.of(challengeId))
                .associationType(AssociationType.ALLSAMETOPIC)
                .build();

        when(resourceService.getResourcesByChallengeId(challengeId))
                .thenReturn(Flux.just(mockResource));

        webTestClient.get()
                .uri("/itachallenge/api/v1/resource/challenge/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResourceDto.class)
                .hasSize(1)
                .contains(mockResource);

        verify(resourceService).getResourcesByChallengeId(challengeId);
    }
}

