package com.itachallenge.user.controller;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.BookmarkRequestDto;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.service.IServiceChallengeStatistics;
import com.itachallenge.user.service.IUserSolutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class UserControllerTest {

    @MockBean
    private IServiceChallengeStatistics serviceChallengeStatistics;
    @MockBean
    private IUserSolutionService userSolutionService;

    // here spring inject automatically mocked version of userController
    @Autowired
    private UserController userController;
    @Autowired
    private WebTestClient webTestClient;

    private static final String CONTROLLER_URL = "/itachallenge/api/v1/user";
    @BeforeEach
    public void setUp() {
    }
    @Test
    void testHello() {
        //region VARIABLES
        String URI_TEST = "/test";
        webTestClient.get()
                .uri(CONTROLLER_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(String.class)
                .value(String::toString, equalTo("Hello from ITA User!!!"));
    }

    @Test
    void testGetChallengeStatistics() {
        UUID challengeId = UUID.randomUUID();

        // Mock HttpHeaders and set content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Mock ChallengeStatisticsDto
        ChallengeStatisticsDto mockChallengeStatisticsDto = new ChallengeStatisticsDto(challengeId, 10, 75.0f);

        // Mock the service behavior
        when(serviceChallengeStatistics.getChallengeStatistics(challengeId))
                .thenReturn(Mono.just(mockChallengeStatisticsDto));

        // Perform the controller method
        Mono<ResponseEntity<?>> resultMono = userController.getChallengeStatistics(challengeId, headers);

        // Block to retrieve the result
        ResponseEntity<?> result = resultMono.block();

        // Assert the result
        assertNotNull(result);
        assertInstanceOf(ChallengeStatisticsDto.class, result.getBody());

        // Optionally, you can perform more specific assertions on the ChallengeStatisticsDto

        // Verify that the service method was called
        verify(serviceChallengeStatistics, times(1)).getChallengeStatistics(challengeId);
    }

    @Test
    void getChallengeStatistics_URLToLong() {

        String URI_TEST = "/statistics?";
        URI_TEST += queryCreation();
        webTestClient.get()
                .uri(CONTROLLER_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.URI_TOO_LONG);
    }

    @Test
    void getSolutionsByUserIdChallengeIdLanguageId() {

        String URI_TEST = "/solution/user/{idUser}/challenge/{idChallenge}/language/{idLanguage}";

        //final String VALID_MONGO_UUID = "c3a92f9d-5d10-4f76-8c0b-6d884c549b1c";

        String userId = "442b8e6e-5d57-4d12-9be2-3ff4f26e7d79";
        String idLanguage = "5c1a97e5-1cca-4144-9981-2de1fb73b178";
        String idChallenge = "09fabe32-7362-4bfb-ac05-b7bf854c6e0f";

        UserScoreDto userScoreDto = new UserScoreDto();
        SolutionUserDto<UserScoreDto> expectedSolutionUserDto = new SolutionUserDto<>();
        expectedSolutionUserDto.setInfo(0, 1, 1, new UserScoreDto[]{userScoreDto});

        when(userSolutionService.getChallengeById(any(), any(), any())).thenReturn(Mono.just(expectedSolutionUserDto));

        webTestClient.get()
                .uri(CONTROLLER_URL + URI_TEST, userId, idLanguage, idChallenge)

                .exchange()
                .expectStatus().isOk()
                .expectBody(SolutionUserDto.class)
                .value(dto -> {
                    assert dto != null;
                    assert dto.getCount() == 1;
                    assert dto.getResults() != null;
                    assert dto.getResults().length == 1;
                    System.out.println("Content of dto: " + dto); // Ajoutez cette ligne
                });
    }
    @Test
    void getChallengeStatistics_NullChallengeId() {

        String URI_TEST = "/statistics/";

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CONTROLLER_URL + URI_TEST)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ChallengeStatisticsDto.class);

        //endregion TESTS
    }


    //endregion TEST METHODS: ChallengeStatics


    //region PRIVATE METHODS

    /**
     * Method to create a query string link "challenge=UUID&", repeat 'numberUUID' times.
     *
     * @return String with query
     */
    private String queryCreation() {
        String URI_TEST = String.format("challenge=%s", UUID.randomUUID());

        for (int i = 1; i < 100; i++) URI_TEST += String.format("&challenge=%s", UUID.randomUUID());

        return URI_TEST;
    }
    private String prepareQuery(List<UUID> challengeIds) {
        return challengeIds.stream()
                .map(UUID::toString)
                .collect(Collectors.joining("&", "challenge=", ""));
    }

    @Test
    void getChallengeUserPercentageTest() {

        String URI_TEST = "/statistics/percent/{idChallenge}";
        UUID idLanguage = UUID.fromString("866853b8-ae7d-4daf-8c82-5e6f653e0fc1");

        when(serviceChallengeStatistics.getChallengeUsersPercentage(any(UUID.class)))
                .thenReturn(Mono.just(10.5f));

        webTestClient.get()
                .uri(CONTROLLER_URL + URI_TEST, idLanguage)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Float.class).isEqualTo(10.5f);
    }

    @Test
    void markOrAddBookmark() {

        BookmarkRequestDto bookmarkRequestDto = new BookmarkRequestDto();
        bookmarkRequestDto.setUuid_challenge("b860f3eb-ef9f-43bf-8c3c-9a5318d26a90");
        bookmarkRequestDto.setUuid_user("26cbe8eb-be68-4eb4-96a6-796168e80ec9");
        bookmarkRequestDto.setUuid_language("df99bae8-4f7f-4054-a957-37a12aa16364");
        bookmarkRequestDto.setBookmarked(true);
        when(userSolutionService.markAsBookmarked(
                bookmarkRequestDto.getUuid_challenge(),
                bookmarkRequestDto.getUuid_language(),
                bookmarkRequestDto.getUuid_user(),
                bookmarkRequestDto.isBookmarked()))
                .thenAnswer(invocation -> {

                            UserSolutionDocument userSolutionDocument = new UserSolutionDocument();

                            return Mono.just(userSolutionDocument);
                        }

                );

        Mono<ResponseEntity<BookmarkRequestDto>> responseMono = userController.markOrAddBookmark(bookmarkRequestDto);

        ResponseEntity<BookmarkRequestDto> responseEntity = responseMono.block();

        assertNotNull(responseEntity);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertEquals(bookmarkRequestDto, responseEntity.getBody());
    }
}



