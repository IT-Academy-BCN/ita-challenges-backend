package com.itachallenge.challenge.dto.challengesection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.dto.challengessection.FilterInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class FilterInfoDtoTest {

    @Test
    @DisplayName("Init FilterInfoDto with technology filter info test.")
    void forTechnologiesTest(){
        FilterInfoDto result = FilterInfoDto.forTechnologies();
        FilterInfoDto expected = mapJsonFileToObject("json/FilterTechnology.json", FilterInfoDto.class);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Init FilterInfoDto with difficulty filter info test.")
    void forDifficultiesTest(){
        FilterInfoDto result = FilterInfoDto.forDifficulties();
        FilterInfoDto expected = mapJsonFileToObject("json/FilterDifficulty.json", FilterInfoDto.class);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Init FilterInfoDto with progress filter info test.")
    void forProgressTest(){
        FilterInfoDto result = FilterInfoDto.forProgress();
        FilterInfoDto expected = mapJsonFileToObject("json/FilterProgress.json", FilterInfoDto.class);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
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
