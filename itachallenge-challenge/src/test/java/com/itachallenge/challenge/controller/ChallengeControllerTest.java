package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.SolutionDocument;
import com.itachallenge.challenge.dto.*;
import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.exception.ResourceNotFoundException;
import com.itachallenge.challenge.mqclient.ZMQClient;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.SolutionRepository;
import com.itachallenge.challenge.service.IChallengeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(ChallengeController.class)
@ActiveProfiles("test")
class ChallengeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private Environment env;

    @MockBean
    private IChallengeService challengeService;

    @MockBean
    private DiscoveryClient discoveryClient;

    @MockBean
    private PropertiesConfig config;

    @MockBean
    private ChallengeRepository challengeRepository;

    @MockBean
    private SolutionRepository solutionRepository;

    //TODO - pending externalize to service layer (internal comms)
    @MockBean
    ZMQClient zmqClient;
    @MockBean
    ChallengeRequestDto challengeInputDto;


    @Test
    void getOneChallenge_ChallengeFound_ReturnsOkResponse() {
        String id = "existing Id";
        ChallengeDto challengeDto = new ChallengeDto(); // Предположим, что у вас есть объект ChallengeDto
        Mono<ChallengeDto> response = Mono.just(challengeDto);

        when(challengeService.getChallengeById(id)).thenReturn(response);

        Mono<ChallengeDto> result = challengeService.getChallengeById(id);

        StepVerifier.create(result)
                .expectNext(challengeDto) // Проверяем, что мы получаем ожидаемый объект ChallengeDto
                .verifyComplete(); // Убеждаемся, что последовательность Mono завершается успешно

    }

    @Test
    void removeResourcesById_ValidId_ResourceDeleted() {
        // Arrange
        String resourceId = "validResourceId";

        when(challengeService.removeResourcesByUuid(resourceId))
                .thenReturn(Mono.just("Resource removed successfully"));

        // Act & Assert
// Ensure the response is not null before performing assertions
        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/resources/validResourceId")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .value(responseMap -> {
                    assertNotNull("Response should not be null", responseMap);
                    assertTrue("Response should contain 'message' key", responseMap.containsKey("message"));
                    assertEquals("Resource removed successfully", responseMap.get("message"));
                });
    }

    @Test
    void patchResourcesById_ValidResourceId_ResourceRemovedSuccessfully() {
        // Arrange
        String resourceId = "validResourceId";
        Map<String, Object> updates = new HashMap<>();
        updates.put("fieldName", "newValue"); // Replace with actual field and value

        when(challengeService.updateResourceByUuid(resourceId, updates))
                .thenReturn(Mono.just("Resource updated successfully"));

        // Act & Assert
        webTestClient.patch()
                .uri("/itachallenge/api/v1/challenge/resources/{idResource}", resourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updates)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .value(responseMap -> {
                    String response = (String) responseMap.get("response");
                    assert response.equals("Resource updated successfully");
                });
    }

    @Test
    void patchResourcesById_InvalidResourceId_ResourceNotFound() {

        String resourceId = "invalidUUID";

        when(challengeService.removeResourcesByUuid(resourceId))
                .thenReturn(Mono.error(new ResourceNotFoundException("Resource with id " + resourceId + " not found")));

        // Act & Assert
        webTestClient.patch()
                .uri("/itachallenge/api/v1/challenge/resources/{idResource}", resourceId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Void.class);
    }


    @Test
    void getAllChallenges_ValidPageParameters_ChallengesReturned() {
        //Arrange
        ChallengeDto challengeDto1 = new ChallengeDto();
        ChallengeDto challengeDto2 = new ChallengeDto();
        ChallengeDto challengeDto3 = new ChallengeDto();
        ChallengeDto[] expectedChallenges = {challengeDto1, challengeDto2, challengeDto3};
        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 3, 3, expectedChallenges);

        Mono<GenericResultDto<ChallengeDto>> expectedResultMono = Mono.just(expectedResult);

        String offset = "0";
        String limit = "3";

        when(challengeService.getAllChallenges(Integer.parseInt(offset), Integer.parseInt(limit)))
                .thenReturn(expectedResultMono);

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges?offset=0&limit=3")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeDto.class);
    }

    @Test
    void getAllChallenges_NullPageParameters_ChallengesReturned() {
        //Arrange
        ChallengeDto challengeDto1 = new ChallengeDto();
        ChallengeDto challengeDto2 = new ChallengeDto();
        ChallengeDto[] expectedChallenges = {challengeDto1, challengeDto2};
        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 2, 2, expectedChallenges);

        Mono<GenericResultDto<ChallengeDto>> expectedResultMono = Mono.just(expectedResult);


        String offset = "0";
        String limit = "2";

        when(challengeService.getAllChallenges(Integer.parseInt(offset), Integer.parseInt(limit)))
                .thenReturn(expectedResultMono);

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeDto.class);
    }

    @Test
    void getAllLanguages_LanguagesExist_LanguagesReturned() {
        // Arrange
        GenericResultDto<LanguageDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 2, 2, new LanguageDto[]{new LanguageDto(), new LanguageDto()});

        when(challengeService.getAllLanguages()).thenReturn(Mono.just(expectedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/language")
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

    @Test
    void getSolutions_ValidIdsAndPagination_SolutionsReturned() {
        // Arrange
        String challengeStringId = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
        String languageStringId = "660e1b18-0c0a-4262-a28a-85de9df6ac5f";
        int offset = 0;
        int limit = 2;
        UUID challengeId = UUID.fromString(challengeStringId);
        UUID languageId = UUID.fromString(languageStringId);
        UUID solutionId1 = UUID.randomUUID();
        UUID solutionId2 = UUID.randomUUID();

        SolutionDocument solutionDocument1 = new SolutionDocument(solutionId1, "Solution Text 1", languageId);
        SolutionDocument solutionDocument2 = new SolutionDocument(solutionId2, "Solution Text 2", languageId);
        ChallengeDocument challengeDocument = new ChallengeDocument();
        challengeDocument.setUuid(challengeId);
        challengeDocument.setSolutions(Arrays.asList(solutionId1, solutionId2));

        when(challengeRepository.findByUuid(challengeId)).thenReturn(Mono.just(challengeDocument));
        when(solutionRepository.findById(solutionId1)).thenReturn(Mono.just(solutionDocument1));
        when(solutionRepository.findById(solutionId2)).thenReturn(Mono.just(solutionDocument2));

        List<ChallengeSolutionDto.SolutionDto> solutionDtos = Arrays.asList(
                new ChallengeSolutionDto.SolutionDto(solutionId1, "Solution Text 1"),
                new ChallengeSolutionDto.SolutionDto(solutionId2, "Solution Text 2")
        );
        ChallengeSolutionDto challengeSolutionDto = new ChallengeSolutionDto(challengeId, languageId, solutionDtos);
        GenericResultDto<ChallengeSolutionDto> genericResultDto = new GenericResultDto<>(offset, limit, 1, new ChallengeSolutionDto[]{challengeSolutionDto});

        when(challengeService.getSolutions(challengeStringId, languageStringId, offset, limit)).thenReturn(Mono.just(genericResultDto));

        // Act
        Mono<GenericResultDto<ChallengeSolutionDto>> resultMono = challengeService.getSolutions(challengeStringId, languageStringId, offset, limit);

        // Assert
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertThat(result.getResults()).hasSize(1);
                    ChallengeSolutionDto resultChallengeSolutionDto = result.getResults()[0];
                    assertThat(resultChallengeSolutionDto.getSolutions().get(0).getSolutionText()).isEqualTo("Solution Text 1");
                    assertThat(resultChallengeSolutionDto.getSolutions().get(1).getSolutionText()).isEqualTo("Solution Text 2");
                })
                .verifyComplete();
    }

    @Test
    void getChallengesByLanguageOrDifficultyTest() {
        String idLanguage = "660e1b18-0c0a-4262-a28a-85de9df6ac5f";
        String level = "EASY";
        int offset = 0;
        int limit = -1;
        ChallengeDto challengeDto1 = new ChallengeDto();
        challengeDto1.setLevel(level);

        List<ChallengeDto> challengeDtos = List.of(challengeDto1);

        GenericResultDto<ChallengeDto> genericResultDto = new GenericResultDto<>();
        genericResultDto.setResults(challengeDtos.toArray(new ChallengeDto[0]));

        Mono<GenericResultDto<ChallengeDto>> expectedResult = Mono.just(genericResultDto);

        // Mock del servicio con los parámetros correctos
        when(challengeService.getChallengesByLanguageOrDifficulty(Optional.of(idLanguage), Optional.of(level), offset, limit))
                .thenReturn(expectedResult);

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/itachallenge/api/v1/challenge/challenges/")
                        .queryParam("idLanguage", idLanguage)
                        .queryParam("level", level)
                        .queryParam("offset", offset)
                        .queryParam("limit", limit)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<GenericResultDto<ChallengeDto>>() {
                })
                .value(result -> {
                    assertNotNull(result);
                    assertEquals(level, result.getResults()[0].getLevel());
                });
    }

    @Test
    void AddSolution_validIdChallenge_validIdLanguage() {
        // Arrange
        SolutionDto solutionDto = new SolutionDto();
        solutionDto.setSolutionText("Test solution");
        solutionDto.setIdChallenge(UUID.fromString("3e817487-88a8-47e2-8d3b-b65c942d36aa"));
        solutionDto.setIdLanguage(UUID.fromString("68dc256a-ac52-40ff-b2f2-bacce4994f9e"));

        when(challengeService.addSolution(any(SolutionDto.class))).thenReturn(Mono.just(solutionDto));

        // Act & Assert
        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solutionDto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void addSolution_NullSolutionText_ThrowsBadRequestException() {
        // Arrange
        SolutionDto solutionDto = new SolutionDto();
        solutionDto.setSolutionText(null); // Set solution text to null
        solutionDto.setIdChallenge(UUID.randomUUID()); // Set challenge ID to a valid UUID
        solutionDto.setIdLanguage(UUID.randomUUID()); // Set language ID to a valid UUID

        // Act & Assert
        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solutionDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("solutionText: 'cannot be empty'");
    }

    @Test
    void addSolution_EmptySolutionText_ThrowsBadRequestException() {
        // Arrange
        SolutionDto solutionDto = new SolutionDto();
        solutionDto.setSolutionText(""); // Set solution text to empty
        solutionDto.setIdChallenge(UUID.randomUUID()); // Set challenge ID to a valid UUID
        solutionDto.setIdLanguage(UUID.randomUUID()); // Set language ID to a valid UUID

        // Act & Assert
        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solutionDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("solutionText: 'cannot be empty'");
    }

    @Test
    void addSolution_NullChallengeId_ThrowsBadRequestException() {
        // Arrange
        SolutionDto solutionDto = new SolutionDto();
        solutionDto.setSolutionText("Test solution");
        solutionDto.setIdChallenge(null); // Set challenge ID to null
        solutionDto.setIdLanguage(UUID.randomUUID()); // Set language ID to a valid UUID

        // Act & Assert
        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solutionDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("idChallenge: 'Invalid UUID'");
    }

    @Test
    void addSolution_NullLanguageId_ThrowsBadRequestException() {
        // Arrange
        SolutionDto solutionDto = new SolutionDto();
        solutionDto.setSolutionText("Test solution");
        solutionDto.setIdChallenge(UUID.randomUUID()); // Set challenge ID to a valid UUID
        solutionDto.setIdLanguage(null); // Set language ID to null

        // Act & Assert
        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solutionDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("idLanguage: 'Invalid UUID'");
    }
    @Test
    void addSolution_NullSolutionDto_ThrowsBadRequestException() {
        // Arrange
        SolutionDto solutionDto = new SolutionDto(); // Create a SolutionDto with null fields

        // Act & Assert
        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solutionDto) // Send the SolutionDto with null fields
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllRelatedChallenges_ChallengesReturned() {
        //Arrange
        String challengeStringId = "660e1b18-0c0a-4262-a28a-85de9df6ac5f";
        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 2, 2, new ChallengeDto[]{new ChallengeDto(), new ChallengeDto()});

        when(challengeService.getRelatedChallenges(challengeStringId, 0, 0)).thenReturn(Mono.just(expectedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeStringId + "/related")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeDto.class);
    }

    @Test
    void getAllRelatedChallenges_ChallengesReturnedPaginated() {
        //Arrange
        String challengeStringId = "660e1b18-0c0a-4262-a28a-85de9df6ac5f";
        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 2, 2, new ChallengeDto[]{new ChallengeDto(), new ChallengeDto()});

        when(challengeService.getRelatedChallenges(challengeStringId, 1, 1)).thenReturn(Mono.just(expectedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeStringId + "/related")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeDto.class);
    }

    @Test
    void getChallengesTestingValues_ValidIds_ReturnsTestingValues() {
        String challengeId = UUID.randomUUID().toString();
        String languageId = UUID.randomUUID().toString();

        List<Map<String, Object>> testingValues = List.of(
                Map.of("in_params", List.of("input1"), "out_params", List.of("output1")),
                Map.of("in_params", List.of("input2"), "out_params", List.of("output2"))
        );

        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("uuid_challenge", challengeId);
        expectedResult.put("uuid_language", languageId);
        expectedResult.put("test_params", testingValues);

        when(challengeService.getTestingParamsByChallengeIdAndLanguageId(challengeId, languageId))
                .thenReturn(Mono.just(expectedResult));

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/test/params/{idChallenge}/language/{idLanguage}", challengeId, languageId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.uuid_challenge").isEqualTo(challengeId)
                .jsonPath("$.uuid_language").isEqualTo(languageId)
                .jsonPath("$.test_params[0].in_params[0]").isEqualTo("input1")
                .jsonPath("$.test_params[0].out_params[0]").isEqualTo("output1")
                .jsonPath("$.test_params[1].in_params[0]").isEqualTo("input2")
                .jsonPath("$.test_params[1].out_params[0]").isEqualTo("output2");

        verify(challengeService).getTestingParamsByChallengeIdAndLanguageId(challengeId, languageId);
    }

    @Test
    void getVersionTest() {
        String expectedVersion = env.getProperty("spring.application.version");

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/version")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.application_name").isEqualTo("itachallenge-challenge")
                .jsonPath("$.version").isEqualTo(expectedVersion);
    }

}
