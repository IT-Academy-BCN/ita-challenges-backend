package com.itachallenge.challenge.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.dto.challengessection.ChallengesSectionInfoDto;
import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeController.class)
class ChallengeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ChallengeService challengeService;

    @Test
    @DisplayName("GET filters and sorting options test")
    void getChallengesSectionOptionsTest(){
        ChallengesSectionInfoDto expected = mapJsonFileToObject(
                "json/ChallengesSectionInfo.json", ChallengesSectionInfoDto.class);
        when(challengeService.getChallengesSectionInfo()).thenReturn(Mono.just(expected));

        String uri = ChallengeController.CHALLENGE+ChallengeController.SECTION;
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChallengesSectionInfoDto.class)
                .value(Assertions::assertNotNull)
                .value(result -> assertThat(result).usingRecursiveComparison().isEqualTo(expected));
    }

    <T> T mapJsonFileToObject(String jsonPath, Class<T> targetClass){
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            InputStream inputStream = new ClassPathResource(jsonPath).getInputStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            String json = FileCopyUtils.copyToString(reader);
            return mapper.readValue(json, targetClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
