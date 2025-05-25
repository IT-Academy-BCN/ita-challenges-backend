package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.document.DetailDocument;
import com.itachallenge.challenge.dto.*;
import com.itachallenge.challenge.enums.DifficultyLevel;
import com.itachallenge.challenge.enums.Topic;
import com.itachallenge.challenge.exception.*;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.challenge.service.ITagService;
import com.itachallenge.challenge.service.JwtServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = ChallengeController.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ChallengeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private Environment env;

    @MockBean
    private IChallengeService challengeService;

    @MockBean
    private ITagService tagService;

    @MockBean
    private DiscoveryClient discoveryClient;

    @MockBean
    private PropertiesConfig config;

    @MockBean
    private JwtServiceImpl jwtService;

    private List<UUID> tags;
    private String challengeId;
    private ChallengeCreateDto formData;
    private ChallengeDto createdChallenge;

    @BeforeEach
    void setup(){
        tags = List.of(UUID.randomUUID());
        challengeId = String.valueOf(UUID.randomUUID());
        formData = new ChallengeCreateDto("títol", "descripció",
                DifficultyLevel.valueOf("EASY"), "Java", "solució", Topic.LISTS, tags);
        createdChallenge = new ChallengeDto();
    }

    @Test
    void getOneChallenge_ChallengeFound_ReturnsOkResponse() {
        String id = "existing Id";
        ChallengeDto challengeDto = new ChallengeDto();
        Mono<ChallengeDto> response = Mono.just(challengeDto);

        when(challengeService.getChallengeById(id)).thenReturn(response);

        Mono<ChallengeDto> result = challengeService.getChallengeById(id);

        StepVerifier.create(result)
                .expectNext(challengeDto)
                .verifyComplete();

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
    void getSolutions_ValidIds_SolutionsReturned() {
        // Arrange
        String idChallenge = "valid-challenge-id";
        String idLanguage = "valid-language-id";

        GenericResultDto<SolutionDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, 2, 2, new SolutionDto[]{new SolutionDto(), new SolutionDto()});

        when(challengeService.getSolutions(idChallenge, idLanguage)).thenReturn(Mono.just(expectedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/solution/challenge/{idChallenge}/language/{idLanguage}", idChallenge, idLanguage)
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
    void getChallengesByFilter_ValidParams_ChallengesReturned() {
        String idLanguage = "valid-language-id";
        String level = "EASY";
        int offset = 0;
        int limit = 10;

        ChallengeDto challenge1 = new ChallengeDto();
        challenge1.setTitle("Challenge 1");
        challenge1.setLevel(level);

        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(offset, limit, 1, new ChallengeDto[]{challenge1});

        when(challengeService.getChallengesByFilter(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Flux.just(expectedResult));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/itachallenge/api/v1/challenge/challenges/byFilter")
                        .queryParam("idLanguage", idLanguage)
                        .queryParam("level", level)
                        .queryParam("offset", String.valueOf(offset))
                        .queryParam("limit", String.valueOf(limit))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);

    }

    @Test
    void AddSolution_validIdChallenge_validIdLanguage() {
        // Mock del servicio
        SolutionDto inputDto = new SolutionDto();
        inputDto.setSolutionText("Test solution");
        inputDto.setIdChallenge(UUID.randomUUID());
        inputDto.setIdLanguage(UUID.randomUUID());

        SolutionDto outputDto = new SolutionDto();
        outputDto.setSolutionText("Test solution");
        outputDto.setIdChallenge(inputDto.getIdChallenge());
        outputDto.setIdLanguage(inputDto.getIdLanguage());

        when(challengeService.addSolution(any())).thenReturn(Mono.just(outputDto));

        // Ejecutar la solicitud y verificar la respuesta
        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    // Verify that the response has the expected keys
                    assert response.getResponseBody().containsKey("uuid_challenge");
                    assert response.getResponseBody().containsKey("uuid_language");
                    assert response.getResponseBody().containsKey("solution_text");

                    // Verify that the values are correct
                    assert response.getResponseBody().get("uuid_challenge").equals(inputDto.getIdChallenge().toString());
                    assert response.getResponseBody().get("uuid_language").equals(inputDto.getIdLanguage().toString());
                    assert response.getResponseBody().get("solution_text").equals(inputDto.getSolutionText());
                });
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
        solutionDto.setIdLanguage(null); // Set challenge ID to null

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
    void addChallenge_test_validRequest() {
        List<UUID> tags = List.of(UUID.randomUUID());
        ChallengeCreateDto formData = new ChallengeCreateDto("títol", "descripció",
                DifficultyLevel.valueOf("EASY"), "Java", "solució", Topic.LISTS,tags );

        ChallengeDto createdChallenge = new ChallengeDto();

        when(challengeService.addChallenge(any())).thenReturn(Mono.just(createdChallenge));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(formData)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChallengeDto.class);

        verify(challengeService).addChallenge(any(ChallengeCreateDto.class));
    }

    @Test
    void addChallenge_test_emptyField_statusBadRequest() {
        List<UUID> tags = List.of(UUID.randomUUID());
        ChallengeCreateDto formData = new ChallengeCreateDto("",
                "descripció",
                DifficultyLevel.valueOf("EASY"),
                "Java",
                "solució",
                Topic.COMPONENTS,
                tags);

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(formData)
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Test
    void addChallenge_test_invalidLanguage_statusBadRequest() {
        List<UUID> tags = List.of(UUID.randomUUID());
        ChallengeCreateDto formData = new ChallengeCreateDto("títol",
                "descripció",
                DifficultyLevel.valueOf("EASY"),
                "Invalid language",
                "solució",
                Topic.COMPONENTS,
                tags);

        when(challengeService.addChallenge(any()))
                .thenThrow(new LanguageNotFoundException("Language not found: Invalid language"));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(formData)
                .exchange()
                .expectStatus().isBadRequest();
    }
    @Test
    void addChallenge_test_invalidLevel_statusBadRequest() {
        String invalidFormData = """
                {
                    "challengeTitle": "títol",
                    "description": "descripció",
                    "level": "TOUGH",
                    "language": "Java",
                    "solution": "solució"
                }
                """;

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidFormData)
                .exchange()
                .expectStatus().isBadRequest();
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
    @Test
    void deleteOneChallenge_success() {
        String id = "existing_id";
        DeleteResponseDto deleteResponseDto = new DeleteResponseDto(id, "Challenge deleted successfully.");
        Mono<DeleteResponseDto> response = Mono.just(deleteResponseDto);

        when(challengeService.deleteChallengeById(id))
                .thenReturn(response);

        Mono<DeleteResponseDto> result = challengeService.deleteChallengeById(id);

        StepVerifier.create(result)
                .expectNext(deleteResponseDto)
                .verifyComplete();
    }

    @Test
    void deleteOneChallenge_notFound() {
        String id = "non_existing_id";

        when(challengeService.deleteChallengeById(id))
                .thenReturn(Mono.error(new ChallengeNotFoundException(String.format("Challenge with id: %s not found", id))));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Challenge with id: non_existing_id not found");
    }

    @Test
    void addChallengeToFavorites_Success_Returns200() {
        String challengeId = "existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        FavoriteDto expectedResponse = new FavoriteDto(true, 20);

        when(challengeService.addChallengeToFavorites(challengeId, userId)).thenReturn(Mono.just(expectedResponse));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FavoriteDto.class)
                .isEqualTo(expectedResponse);

        verify(challengeService, times(1)).addChallengeToFavorites(challengeId, userId);
    }

    @Test
    void addChallengeToBookmarks_Success_Returns200() {
        String challengeId = "existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        BookmarkDto expectedResponse = new BookmarkDto(true, 20);

        when(challengeService.addChallengeToBookmarks(challengeId, userId)).thenReturn(Mono.just(expectedResponse));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookmarkDto.class)
                .isEqualTo(expectedResponse);

        verify(challengeService, times(1)).addChallengeToBookmarks(challengeId, userId);
    }

    @Test
    void addChallengeToFavorites_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.addChallengeToFavorites(challengeId, userId)).thenReturn(Mono.error(new ChallengeNotFoundException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).addChallengeToFavorites(challengeId, userId);
    }

    @Test
    void addChallengeToBookmarks_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.addChallengeToBookmarks(challengeId, userId)).thenReturn(Mono.error(new ChallengeNotFoundException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).addChallengeToBookmarks(challengeId, userId);
    }

    @Test
    void addChallengeToFavorites_InternalServerError_Returns500() {
        String challengeId = "Existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.addChallengeToFavorites(challengeId, userId)).thenReturn(Mono.error(new InternalServerErrorException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).addChallengeToFavorites(challengeId, userId);
    }

    @Test
    void addChallengeToBookmarks_InternalServerError_Returns500() {
        String challengeId = "Existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.addChallengeToBookmarks(challengeId, userId)).thenReturn(Mono.error(new InternalServerErrorException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).addChallengeToBookmarks(challengeId, userId);
    }

    @Test
    void addChallengeToFavorites_InvalidHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String authHeader = "badHeader";
        String errorMessage = "ErrorMessage";

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException(errorMessage));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).addChallengeToFavorites(anyString(), anyString());
    }

    @Test
    void addChallengeToBookmarks_InvalidHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String authHeader = "badHeader";
        String errorMessage = "ErrorMessage";

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException(errorMessage));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).addChallengeToBookmarks(anyString(), anyString());
    }

    @Test
    void addChallengeToFavorites_MissingHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String errorMessage = "ErrorMessage";

        when(jwtService.getUserUuIdFromAuthenticationHeader(null)).thenThrow(new JwtException(errorMessage));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).addChallengeToFavorites(anyString(), anyString());
    }

    @Test
    void addChallengeToBookmarks_MissingHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String errorMessage = "ErrorMessage";

        when(jwtService.getUserUuIdFromAuthenticationHeader(null)).thenThrow(new JwtException(errorMessage));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).addChallengeToBookmarks(anyString(), anyString());
    }

    @Test

    @DisplayName("GET recibir respuesta 200 a getTags")
    void getTags_test_validRequest() {

        TagDto tag1 = new TagDto(UUID.randomUUID(), "POO", "Programación orientada a objetos");
        TagDto tag2 = new TagDto(UUID.randomUUID(), "Algoritmos", "Retos de lógica y eficiencia");

        GenericResultDto<TagDto> resultDto = new GenericResultDto<>();
        resultDto.setInfo(0, 2, 2, new TagDto[]{tag1, tag2});

        when(tagService.getAllTags()).thenReturn(Mono.just(resultDto));

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/tags")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.results.length()").isEqualTo(2)
                .jsonPath("$.results[0].tag_name").isEqualTo("POO")
                .jsonPath("$.results[1].tag_name").isEqualTo("Algoritmos")
                .jsonPath("$.results[0].tag_description").value(desc ->
                        assertTrue(desc.toString().contains("Programación orientada")))
                .jsonPath("$.offset").isEqualTo(0)
                .jsonPath("$.limit").isEqualTo(2);

        verify(tagService).getAllTags();
    }

    @Test
    void removeChallengeFromFavorite_Success_Returns200() {
        String challengeId = "existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        FavoriteDto expectedResponse = new FavoriteDto(false, 20);

        when(challengeService.removeChallengeFromFavorites(challengeId, userId)).thenReturn(Mono.just(expectedResponse));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FavoriteDto.class)
                .isEqualTo(expectedResponse);

        verify(challengeService, times(1)).removeChallengeFromFavorites(challengeId, userId);
    }

    @Test
    void removeChallengeFromFavorite_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.removeChallengeFromFavorites(challengeId, userId)).thenReturn(Mono.error(new ChallengeNotFoundException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).removeChallengeFromFavorites(challengeId, userId);
    }

    @Test
    void removeChallengeFromFavorite_InternalServerError_Returns500() {
        String challengeId = "Existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.removeChallengeFromFavorites(challengeId, userId)).thenReturn(Mono.error(new InternalServerErrorException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).removeChallengeFromFavorites(challengeId, userId);
    }

    @Test
    void removeChallengeFromFavorite_InvalidHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String authHeader = "BadHeader";
        String errorMessage = "Error message";

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException(errorMessage));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).removeChallengeFromFavorites(anyString(), anyString());
    }

    @Test
    void removeChallengeFromFavorite_MissingHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String errorMessage = "ErrorMessage";

        when(jwtService.getUserUuIdFromAuthenticationHeader(null)).thenThrow(new JwtException(errorMessage));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/favorites")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).removeChallengeFromFavorites(anyString(), anyString());

    }

    @Test
    @DisplayName("GET /challenges must return the timesFavorite field in the JSON")
    void getChallenges_IncludesTimesFavorite() {
        ChallengeDto challenge = ChallengeDto.builder()
                .challengeId(UUID.randomUUID())
                .title("Repte amb cor")
                .level("Hard")
                .creationDate("2025-03-26")
                .detail(new DetailDocument("detall"))
                .languages(Set.of())
                .solutions(List.of())
                .topic(Topic.DEBUGGING)
                .timesFavorite(5)
                .build();

        ChallengeDto[] challengeArray = new ChallengeDto[] { challenge };
        GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>(0, 10, 1, challengeArray);


        when(challengeService.getAllChallenges(anyInt(), anyInt()))
                .thenReturn(Mono.just(resultDto));

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.results[0].timesFavorite").isEqualTo(5);
    }

    @Test
    void getChallengesByFilter_shouldReturnOkResponse() {
        // Mock de respuesta vacía
        GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
        resultDto.setInfo(0, 10, 0, new ChallengeDto[0]);

        UUID mockTag = UUID.randomUUID();
        UUID languageMok = UUID.randomUUID();

        when(challengeService.getChallengesByFilter(
                Optional.of(languageMok.toString()),
                Optional.of("EASY"),
                Optional.of(List.of(mockTag)),
                0,
                2
        )).thenReturn(Flux.just(resultDto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/itachallenge/api/v1/challenge/challenges/byFilter")
                        .queryParam("idLanguage", languageMok.toString())
                        .queryParam("level", "EASY")
                        .queryParam("offset", 0)
                        .queryParam("limit", 2)
                        .queryParam("tags", mockTag.toString())
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateChallengeValidRequest_AuthorizedUser_Returns200() {
        String authHeader = "Bearer valid-token";
        String userId = "user123";

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);
        when(challengeService.updateChallenge(anyString(), any(ChallengeCreateDto.class)))
                .thenReturn(Mono.just(new ChallengeDto()));

        webTestClient.put()
                .uri("/itachallenge/api/v1/challenge/challenge/" + challengeId + "/update")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(formData)
                .exchange()
                .expectStatus().isOk();

        verify(challengeService, times(1)).updateChallenge(anyString(), any(ChallengeCreateDto.class));
    }

   @Test
    void updateChallenge_MissingAuthHeader_Returns400() {
        when(jwtService.getUserUuIdFromAuthenticationHeader(null)).thenThrow(new JwtException("Missing auth header"));

        webTestClient.put()
                .uri("/itachallenge/api/v1/challenge/challenge/" + challengeId + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(formData)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(challengeService);
    }

    @ParameterizedTest
    @MethodSource("provideEmptyFields")
    void updateChallengeEmptyField_returnsBadRequest_test(Consumer<ChallengeCreateDto> fieldSetter) {
        fieldSetter.accept(formData);

        webTestClient.put()
                .uri("/itachallenge/api/v1/challenge/challenge/" + challengeId + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(formData)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(challengeService);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEnumValues")
    void updateChallengeInvalidEnumValue_returnsBadRequest_test(String jsonBody){

        webTestClient.put()
                .uri("/itachallenge/api/v1/challenge/challenge/" + challengeId + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonBody)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(challengeService);
    }

    private static Stream<Arguments> provideEmptyFields() {
        return Stream.of(
                Arguments.of((Consumer<ChallengeCreateDto>) dto -> dto.setChallengeTitle("")),
                Arguments.of((Consumer<ChallengeCreateDto>) dto -> dto.setDescription("")),
                Arguments.of((Consumer<ChallengeCreateDto>) dto -> dto.setLanguage("")),
                Arguments.of((Consumer<ChallengeCreateDto>) dto -> dto.setSolution(null)),
                Arguments.of((Consumer<ChallengeCreateDto>) dto -> dto.setTopic(null))
        );
    }

    private static Stream<Arguments> provideInvalidEnumValues() {

        String baseJson = """
        {
          "challengeTitle": "Title",
          "description": "Description",
          "level": "%s",
          "language": "Java",
          "solution": "valid solution",
          "topic": "%s",
          "tags": {}
        }
        """;

        return Stream.of(
                Arguments.of(String.format(baseJson, "invalidLevel", "ALL")),
                Arguments.of(String.format(baseJson, "EASY", "invalidTopic"))
        );
    }

    @Test
    void removeChallengeFromBookmarks_Success_Returns200() {
        String challengeId = "existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        BookmarkDto expectedResponse = new BookmarkDto(false, 20);

        when(challengeService.removeChallengeFromBookmarks(challengeId, userId)).thenReturn(Mono.just(expectedResponse));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookmarkDto.class)
                .isEqualTo(expectedResponse);

        verify(challengeService, times(1)).removeChallengeFromBookmarks(challengeId, userId);
    }

    @Test
    void removeChallengeFromBookmarks_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.removeChallengeFromBookmarks(challengeId, userId)).thenReturn(Mono.error(new ChallengeNotFoundException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).removeChallengeFromBookmarks(challengeId, userId);
    }

    @Test
    void removeChallengeFromBookmarks_InternalServerError_Returns500() {
        String challengeId = "Existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.removeChallengeFromBookmarks(challengeId, userId)).thenReturn(Mono.error(new InternalServerErrorException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).removeChallengeFromBookmarks(challengeId, userId);
    }

    @Test
    void removeChallengeFromBookmarks_InvalidHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String authHeader = "BadHeader";
        String errorMessage = "Error message";

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException(errorMessage));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).removeChallengeFromBookmarks(anyString(), anyString());
    }

    @Test
    void removeChallengeFromBookmarks_MissingHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String errorMessage = "ErrorMessage";

        when(jwtService.getUserUuIdFromAuthenticationHeader(null)).thenThrow(new JwtException(errorMessage));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).removeChallengeFromBookmarks(anyString(), anyString());

    }
}
