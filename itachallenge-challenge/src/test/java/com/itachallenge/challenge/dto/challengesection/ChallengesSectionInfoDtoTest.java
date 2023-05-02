package com.itachallenge.challenge.dto.challengesection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.dto.challengessection.ChallengesSectionInfoDto;
import com.itachallenge.challenge.dto.challengessection.FiltersDto;
import com.itachallenge.challenge.dto.challengessection.SortInfoDto;
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

class ChallengesSectionInfoDtoTest {

    @Test
    @DisplayName("Init ChallengesSectionInfoDto with all filters + sorting options test.")
    void withAllFiltersTest(){
        FiltersDto expectedFilters = mapJsonFileToObject(
                "json/Filters.json", FiltersDto.class);
        try(MockedStatic<FiltersDto> filterDtoMocked = Mockito.mockStatic(FiltersDto.class)) {
            filterDtoMocked.when(FiltersDto::withAllFilters).thenReturn(expectedFilters);
        }
        SortInfoDto expectedSort = mapJsonFileToObject(
                "json/SortInfo.json", SortInfoDto.class);
        try(MockedStatic<SortInfoDto> sortDtoMocked = Mockito.mockStatic(SortInfoDto.class)) {
            sortDtoMocked.when(SortInfoDto::withAllOptions).thenReturn(expectedSort);
        }

        ChallengesSectionInfoDto expected = mapJsonFileToObject(
                "json/ChallengesSectionInfo.json", ChallengesSectionInfoDto.class);
        ChallengesSectionInfoDto result = ChallengesSectionInfoDto.withAllInfo();
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
