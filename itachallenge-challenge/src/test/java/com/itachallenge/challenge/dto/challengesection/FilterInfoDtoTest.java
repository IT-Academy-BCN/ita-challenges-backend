package com.itachallenge.challenge.dto.challengesection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.dto.challengessection.FilterInfoDto;
import com.itachallenge.challenge.model.Difficulties;
import com.itachallenge.challenge.model.Progress;
import com.itachallenge.challenge.model.Technologies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FilterInfoDtoTest {

    @Test
    @DisplayName("Init FilterInfoDto with technology filter info test.")
    void forTechnologiesTest(){
        try(MockedStatic<Technologies> TechnologiesEnumMocked = Mockito.mockStatic(Technologies.class)) {
            List<String> values = List.of("Javascript", "Java", "PHP", "Python");
            TechnologiesEnumMocked.when(Technologies::getAllValues).thenReturn(values);
        }
        FilterInfoDto result = FilterInfoDto.forTechnologies();
        FilterInfoDto expected = mapJsonFileToObject("json/FilterTechnology.json", FilterInfoDto.class);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Init FilterInfoDto with difficulty filter info test.")
    void forDifficultiesTest(){
        try(MockedStatic<Difficulties> DifficultiesEnumMocked = Mockito.mockStatic(Difficulties.class)) {
            List<String> values = List.of("Fácil", "Media", "Difícil");
            DifficultiesEnumMocked.when(Difficulties::getAllValues).thenReturn(values);
        }
        FilterInfoDto result = FilterInfoDto.forDifficulties();
        FilterInfoDto expected = mapJsonFileToObject("json/FilterDifficulty.json", FilterInfoDto.class);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Init FilterInfoDto with progress filter info test.")
    void forProgressTest(){
        try(MockedStatic<Progress> ProgressEnumMocked = Mockito.mockStatic(Progress.class)) {
            List<String> values = List.of("No empezados", "Falta completar", "Completados");
            ProgressEnumMocked.when(Progress::getAllValues).thenReturn(values);
        }
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
