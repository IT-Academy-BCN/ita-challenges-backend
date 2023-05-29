package com.itachallenge.challenge.integration;

import com.itachallenge.challenge.controller.ChallengeController;
import com.itachallenge.challenge.helper.ResourceHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ChallengeIT {

    @Autowired
    private WebTestClient webTestClient;

    private ResourceHelper resourceHelper = new ResourceHelper();

    private final String RESULT1 = "$.results[0].";

    /*
    @GetMapping(value = "/getAllChallenges", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getAllChallenges()
    }
    */
    //@Test //TODO: enable once feature#6 included in develop
    @DisplayName("GET all challenges IT")
    void getAllChallengesIT(){
        //TODO: check if path is correct once Alfonso's PR is approved
        String uri = ChallengeController.CHALLENGE+ChallengeController.CHALLENGES;

        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.offset").isEqualTo(0)
                .jsonPath("$.limit").isEqualTo(-1)
                .jsonPath("$.count").isEqualTo(50)
                .jsonPath(RESULT1 + "id_challenge").isEqualTo("dcacb291-b4aa-4029-8e9b-284c8ca80296")
                .jsonPath(RESULT1 + "challenge_title").isEqualTo("Sociis Industries")
                .jsonPath(RESULT1 + "level").isEqualTo("EASY")
                .jsonPath(RESULT1 + "creation_date").isEqualTo("Mar 25, 2023")
                .jsonPath(RESULT1 + "details.description").isEqualTo("mi. Aliquam gravida mauris ut mi. Duis risus odio, auctor vitae, aliquet nec, imperdiet nec, leo. Morbi neque tellus, imperdiet")
                .jsonPath(RESULT1 + "details.examples[0].id_example").isEqualTo("1")
                .jsonPath(RESULT1 + "details.examples[0].example_text").isEqualTo("Sed dictum. Proin eget odio. Aliquam vulputate ullamcorper magna. Sed eu eros. Nam consequat dolor vitae dolor. Donec fringilla. Donec")
                .jsonPath(RESULT1 + "details.examples[1].id_example").isEqualTo("2")
                .jsonPath(RESULT1 + "details.examples[1].example_text").isEqualTo(" Aliquam nec nisi maximus, venenatis")
                .jsonPath(RESULT1 + "details.examples[2].id_example").isEqualTo("3")
                .jsonPath(RESULT1 + "details.examples[2].example_text").isEqualTo("Mauris feugiat massa et suscipit malesuada.")
                .jsonPath(RESULT1 + "details.examples[3].id_example").isEqualTo("4")
                .jsonPath(RESULT1 + "details.examples[3].example_text").isEqualTo("Aenean quis maximus purus. Quisque sit amet")
                .jsonPath(RESULT1 + "details.notes").isEqualTo("Quisque libero lacus, varius et, euismod et, commodo at, libero. Morbi accumsan laoreet ipsum. Curabitur consequat, lectus sit amet luctus vulputate, nisi sem semper erat, in consectetuer ipsum")
                .jsonPath(RESULT1 + "languages[0].id_language").isEqualTo("1")
                .jsonPath(RESULT1 + "languages[0].language_name").isEqualTo("Javascript")
                .jsonPath(RESULT1 + "languages[1].id_language").isEqualTo("2")
                .jsonPath(RESULT1 + "languages[1].language_name").isEqualTo("Java")
                .jsonPath(RESULT1 + "languages[2].id_language").isEqualTo("3")
                .jsonPath(RESULT1 + "languages[2].language_name").isEqualTo("PHP")
                .jsonPath(RESULT1 + "languages[3].id_language").isEqualTo("4")
                .jsonPath(RESULT1 + "languages[3].language_name").isEqualTo("Python")
                .jsonPath(RESULT1 + "solutions[0].id_solution").isEqualTo("b0f0b102-c9d7-4b1b-9a6d-c63c9f33c8f6")
                .jsonPath(RESULT1 + "solutions[0].solution_text").isEqualTo("Sed dictum. Proin eget odio. Aliquam vulputate ullamcorper magna. Sed eu eros. Nam consequat dolor vitae dolor. Donec fringilla. Donec")
                .jsonPath(RESULT1 + "solutions[1].id_solution").isEqualTo("d82385c6-7735-432f-ae9f-e182d0ed3040")
                .jsonPath(RESULT1 + "solutions[1].solution_text").isEqualTo("s a sodales nulla. Aliquam rutrum bibendum pulvinar. ")
                .jsonPath(RESULT1 + "solutions[2].id_solution").isEqualTo("2e3fd4f6-1fd7-4979-97ec-0354ca4acbcc")
                .jsonPath(RESULT1 + "solutions[2].solution_text").isEqualTo("nisl. Maecenas malesuada fringilla est. Mauris eu turpis. Nulla aliquet. Proin velit. Sed malesuada augue ut lacus. Nulla tincidunt, neque vitae semper egestas, urna justo faucibus lectus, a sollicitudin")
                .jsonPath(RESULT1 + "resources[0].id_resource").isEqualTo("e7b6b5a0-5b0a-4b0e-8b0a-9b0a0b0a0b0a")
                .jsonPath(RESULT1 + "resources[0].resource_description").isEqualTo("Sed dictum. Proin eget odio. Aliquam vulputate ullamcorper magna. Sed eu eros. Nam consequat dolor vitae dolor. Donec fringilla. Donec")
                .jsonPath(RESULT1 + "resources[0].author").isEqualTo("Ona Costa")
                .jsonPath(RESULT1 + "resources[0].generation_date").isEqualTo("Mar 25, 2023")
                .jsonPath(RESULT1 + "resources[1].id_resource").isEqualTo("51a0ec36-c500-4615-8910-460c1bed1495")
                .jsonPath(RESULT1 + "resources[1].resource_description").isEqualTo("Pellentesque a aliquet diam. Aliquam efficitur pharetra")
                .jsonPath(RESULT1 + "resources[1].author").isEqualTo("John Doe")
                .jsonPath(RESULT1 + "resources[1].generation_date").isEqualTo("Mar 25, 2023")
                .jsonPath(RESULT1 + "resources[2].id_resource").isEqualTo("959e09ae-9d65-45e2-9f95-f13b5e188492")
                .jsonPath(RESULT1 + "resources[2].resource_description").isEqualTo("Vivamus bibendum eros porttitor orci varius ultricies")
                .jsonPath(RESULT1 + "resources[2].author").isEqualTo("Emeka Okafor")
                .jsonPath(RESULT1 + "resources[2].generation_date").isEqualTo("Mar 25, 2023")
                .jsonPath(RESULT1 + "related[0].id_challenge").isEqualTo("40728c9c-a557-4d12-bf8f-3747d0924197")
                .jsonPath(RESULT1 + "related[1].id_challenge").isEqualTo("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0")
                .jsonPath(RESULT1 + "related[2].id_challenge").isEqualTo("5f71e51d-1e3e-44a2-bc97-158021f1a344")
                .consumeWith(System.out::println);

    }

    @Test
    @DisplayName("GET filters info IT")
    void getChallengesFiltersIT(){
        String uri = ChallengeController.CHALLENGE+ChallengeController.FILTERS;
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.filtersInfo[0].filterName").isEqualTo("Lenguaje")
                .jsonPath("$.filtersInfo[0].options[0]").isEqualTo("Javascript")
                .jsonPath("$.filtersInfo[0].options[1]").isEqualTo("Java")
                .jsonPath("$.filtersInfo[0].options[2]").isEqualTo("PHP")
                .jsonPath("$.filtersInfo[0].options[3]").isEqualTo("Python")
                .jsonPath("$.filtersInfo[0].uniqueOption").isEqualTo(false)
                .jsonPath("$.filtersInfo[0].visibility[0]").isEqualTo("ROLE_GUEST")
                .jsonPath("$.filtersInfo[1].filterName").isEqualTo("Dificultad")
                .jsonPath("$.filtersInfo[1].options[0]").isEqualTo("Fácil")
                .jsonPath("$.filtersInfo[1].options[1]").isEqualTo("Media")
                .jsonPath("$.filtersInfo[1].options[2]").isEqualTo("Difícil")
                .jsonPath("$.filtersInfo[1].uniqueOption").isEqualTo(false)
                .jsonPath("$.filtersInfo[1].visibility[0]").isEqualTo("ROLE_GUEST")
                .jsonPath("$.filtersInfo[2].filterName").isEqualTo("Progreso")
                .jsonPath("$.filtersInfo[2].options[0]").isEqualTo("No empezados")
                .jsonPath("$.filtersInfo[2].options[1]").isEqualTo("Falta completar")
                .jsonPath("$.filtersInfo[2].options[2]").isEqualTo("Completados")
                .jsonPath("$.filtersInfo[2].uniqueOption").isEqualTo(false)
                .jsonPath("$.filtersInfo[2].visibility[0]").isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("GET sorting info IT")
    void getChallengesSortInfoIT(){
        String uri = ChallengeController.CHALLENGE+ChallengeController.SORT;
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.options[0]").isEqualTo("Popularidad")
                .jsonPath("$.options[1]").isEqualTo("Fecha")
                .jsonPath("$.count").isEqualTo(2);
    }
}
