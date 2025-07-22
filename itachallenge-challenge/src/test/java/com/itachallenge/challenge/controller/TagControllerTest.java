package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import com.itachallenge.challenge.repository.*;
import com.itachallenge.challenge.service.ITagService;
import com.itachallenge.jwtcore.service.IJwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = TagController.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TagControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ITagService tagService;

    @MockBean
    private DiscoveryClient discoveryClient;

    @MockBean
    private ChallengeRepository challengeRepository;

    @MockBean
    private SolutionRepository solutionRepository;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private ChallengeController challengeController;

    @MockBean
    private FavoriteController favoriteController;

    @MockBean
    private ResourceRepository resourceRepository;

    @MockBean
    private IJwtService jwtService;

    @MockBean
    private MappingMongoConverter mappingMongoConverter;

    @MockBean
    private ChallengeSolvedController challengeSolvedController;

    @MockBean
    private LanguageRepository languageRepository;

    @Test
    void testGetTagsByLanguageId_WhenTagsExist_ReturnsOk() {
        UUID languageId = UUID.randomUUID();

        TagDto tag1 = new TagDto(UUID.randomUUID(), "Callbacks", "Description 1", languageId);
        TagDto tag2 = new TagDto(UUID.randomUUID(), "Promises", "Description 2", languageId);

        TagDto[] tagArray = new TagDto[]{tag1, tag2};
        GenericResultDto<TagDto> resultDto = new GenericResultDto<>();
        resultDto.setInfo(0, 2, 2, tagArray);

        when(tagService.getTagsByLanguageId(languageId)).thenReturn(Mono.just(resultDto));

        webTestClient.get()
                .uri("/itachallenge/api/v1/tags/" + languageId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.results.length()").isEqualTo(2)
                .jsonPath("$.results[0].tag_name").isEqualTo("Callbacks")
                .jsonPath("$.results[1].tag_name").isEqualTo("Promises");

        verify(tagService).getTagsByLanguageId(languageId);
    }

    @Test
    void testGetTagsByLanguageId_WhenNoTagsExist_Returns404() {
        UUID languageId = UUID.randomUUID();

        when(tagService.getTagsByLanguageId(languageId)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/itachallenge/api/v1/tags/" + languageId)
                .exchange()
                .expectStatus().isNotFound();

        verify(tagService).getTagsByLanguageId(languageId);
    }

    @Test
    void testGetTagsByLanguageId_WhenErrorOccurs_Returns500() {
        UUID languageId = UUID.randomUUID();

        when(tagService.getTagsByLanguageId(languageId))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/tags/" + languageId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        verify(tagService).getTagsByLanguageId(languageId);
    }
}
