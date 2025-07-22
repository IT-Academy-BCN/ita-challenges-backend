package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.ResourceRepository;
import com.itachallenge.challenge.repository.SolutionRepository;
import com.itachallenge.challenge.repository.TagRepository;
import com.itachallenge.challenge.service.LanguageServiceImpl;
import com.itachallenge.jwtcore.service.IJwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = LanguageController.class)
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class LanguageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LanguageServiceImpl languageService;

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

    @Test
    void getAllLanguages_LanguagesExist_LanguagesReturned() {
        // Arrange
        GenericResultDto<LanguageDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 2, 2, new LanguageDto[]{new LanguageDto(), new LanguageDto()});

        when(languageService.getAllLanguages()).thenReturn(Mono.just(expectedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/languages/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenericResultDto.class)
                .value(dto -> {
                    assert dto != null;
                    assert dto.getCount() == 2;
                    assert dto.getResults() != null;
                    assert dto.getResults().length == 2;
                });
    }
}
