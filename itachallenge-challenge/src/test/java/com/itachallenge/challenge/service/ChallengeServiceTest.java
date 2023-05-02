package com.itachallenge.challenge.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.dto.challengessection.ChallengesSectionInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChallengeServiceTest {

    @Autowired
    private ChallengeService challengeService;


    @Test
    @DisplayName("Get Challenges Section Options Test")
    void getChallengesSectionOptionsTest(){
        ChallengesSectionInfoDto expected = mapJsonFileToObject(
                "json/ChallengesSectionInfo.json", ChallengesSectionInfoDto.class);
        try(MockedStatic<ChallengesSectionInfoDto> dtoMocked = Mockito.mockStatic(ChallengesSectionInfoDto.class)) {
            dtoMocked.when(ChallengesSectionInfoDto::withAllInfo).thenReturn(expected);
        }

        Mono<ChallengesSectionInfoDto> result = challengeService.getChallengesSectionInfo();
        StepVerifier.create(result)
                .assertNext(dto -> assertThat(dto).usingRecursiveComparison().isEqualTo(expected))
                .verifyComplete();
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
