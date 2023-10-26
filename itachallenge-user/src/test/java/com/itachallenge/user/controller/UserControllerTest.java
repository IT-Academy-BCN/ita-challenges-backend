package com.itachallenge.user.controller;

import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.service.IUserSolutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class UserControllerTest {
    //region VARIABLES
    @Autowired()
    private WebTestClient webTestClient;

    private static final String CONTROLLER_URL = "/itachallenge/api/v1/user";

    @MockBean
    IUserSolutionService userScoreService;

    //endregion VARIABLES


    //region CONSTRUCTOR
    @BeforeEach
    public void setUp() {
    }

    //endregion CONSTRUCTOR


    //region TEST METHODS
    @Test
    void testHello() {
        //region VARIABLES
        String URI_TEST = "/test";

        //endregion VARIABLES


        //region TESTS
        webTestClient.get()
                .uri(CONTROLLER_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(String.class)
                .value(String::toString, equalTo("Hello from ITA User!!!"));

        //endregion TESTS

    }

    //endregion TEST METHODS


    //region TEST METHODS: ChallengeStatistics
    @Test
    void getChallengeStatistics() {
        //region VARIABLES
        String URI_TEST = "/statistics?";

        //endregion VARIABLES


        //region INITIALIZATION TEST
        // Set up the URL_TEST
        URI_TEST += queryCreation(10);

        //endregion INITIALIZATION TEST


        //region TESTS
        List<ChallengeStatisticsDto> response = webTestClient.get()
                .uri(CONTROLLER_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ChallengeStatisticsDto.class)
                .returnResult().getResponseBody();

        assertEquals(10, response.size());
        assertNotEquals(0, response.get(0).getPopularity());
        assertNotEquals(0, response.get(9).getPercentage());

        //endregion TESTS

    }

    @Test
    void getChallengeStatistics_URLToLong() {
        //region VARIABLES
        String URI_TEST = "/statistics?";

        //endregion VARIABLES


        //region INITIALIZATION TEST
        // Set up the URL_TEST
        URI_TEST += queryCreation(100);

        //endregion INITIALIZATION TEST


        //region TESTS
        webTestClient.get()
                .uri(CONTROLLER_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.URI_TOO_LONG);

        //endregion TESTS

    }

    @Test
   void getSolutionsByUserIdChallengeIdLanguageId (){

       String URI_TEST = "/solution/user/{idUser}/challenge/{idChallenge}/language/{idLanguage}";

       final String VALID_MONGO_UUID = "c3a92f9d-5d10-4f76-8c0b-6d884c549b1c";
       String userId = VALID_MONGO_UUID;
       String idLanguage = VALID_MONGO_UUID;
       String idChallenge = VALID_MONGO_UUID;

       UserScoreDto userScoreDto = new UserScoreDto();
       SolutionUserDto<UserScoreDto> expectedSolutionUserDto = new SolutionUserDto<>();
       expectedSolutionUserDto.setInfo(0,1,1, new UserScoreDto[]{userScoreDto});

       when(userScoreService.getChallengeById(any(),any(),any())).thenReturn(Mono.just(expectedSolutionUserDto));

        webTestClient.get()
              .uri(CONTROLLER_URL + URI_TEST, userId,idLanguage,idChallenge)
              .exchange()
              .expectStatus().isOk()
              .expectBody(SolutionUserDto.class)
              .value(dto -> {
                   assert dto != null;
                   assert dto.getCount() == 1;
                   assert dto.getResults() != null;
                   assert dto.getResults().length == 1;
              });
   }



    @Test
    void getChallengeStatistics_EmptyUUIDList() {
        //region VARIABLES
        String URI_TEST = "/statistics";

        //endregion VARIABLES


        //region TESTS
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CONTROLLER_URL + URI_TEST)
                        .queryParam("", Collections.EMPTY_LIST)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(ChallengeStatisticsDto.class);

        //endregion TESTS

    }

    //endregion TEST METHODS: ChallengeStatics


    //region PRIVATE METHODS

    /**
     * Method to create a query string link "challenge=UUID&", repeat 'numberUUID' times.
     *
     * @param numberUUID Number of tiems that repeat.
     * @return String with query
     */
    private String queryCreation(int numberUUID) {
        String URI_TEST = String.format("challenge=%s", UUID.randomUUID());

        for (int i = 1; i < numberUUID; i++) URI_TEST += String.format("&challenge=%s", UUID.randomUUID());

        return URI_TEST;
    }

    //endregion PRIVATE METHODS

}
