package com.itachallenge.challenge.dto.challengesection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.dto.challengessection.FilterInfoDto;
import com.itachallenge.challenge.dto.challengessection.FiltersDto;
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

import static org.assertj.core.api.Assertions.assertThat;


class FilterDtoTest {

    @Test
    @DisplayName("Init FiltersDto with all filters test.")
    void withAllFiltersTest(){
        FilterInfoDto expectedTechnologies = mapJsonFileToObject(
                "json/FilterTechnology.json", FilterInfoDto.class);
        FilterInfoDto expectedDifficulties = mapJsonFileToObject(
                "json/FilterDifficulty.json", FilterInfoDto.class);
        FilterInfoDto expectedProgress = mapJsonFileToObject(
                "json/FilterProgress.json", FilterInfoDto.class);
        try(MockedStatic<FilterInfoDto> filterDtoMocked = Mockito.mockStatic(FilterInfoDto.class)) {
            filterDtoMocked.when(FilterInfoDto::forTechnologies).thenReturn(expectedTechnologies);
            filterDtoMocked.when(FilterInfoDto::forDifficulties).thenReturn(expectedDifficulties);
            filterDtoMocked.when(FilterInfoDto::forProgress).thenReturn(expectedProgress);
        }

        FiltersDto result = FiltersDto.withAllFilters();
        FiltersDto expected = mapJsonFileToObject("json/Filters.json", FiltersDto.class);
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
