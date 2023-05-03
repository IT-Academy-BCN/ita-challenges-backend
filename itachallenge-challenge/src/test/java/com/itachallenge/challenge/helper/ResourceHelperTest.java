package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.dto.challengessection.FilterInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceHelperTest {

    @Test
    @DisplayName("Map one resource filte to a single object test")
    void mapResourceTest(){
        FilterInfoDto expected = new FilterInfoDto();
        expected.setFilterName("RandomName");
        expected.setOptions(List.of("Option1", "Option2","Option3"));
        expected.setUniqueOption(true);
        expected.setVisibility(List.of("ROLE_X", "ROLE_Y"));

        ResourceHelper resourceHelper = new ResourceHelper();
        String jsonPath = "json/RandomJson.json";
        FilterInfoDto result  = resourceHelper.mapResource(jsonPath, FilterInfoDto.class);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

}
