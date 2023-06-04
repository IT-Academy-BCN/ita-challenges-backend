package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.utils.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
class FilterDtoTest {

    @Autowired
    private ObjectMapper mapper;

    private final String filterJsonPath = "json/Filter.json";

    private FilterDto filterDto;

    @BeforeEach
    void setUp(){
        List<String> options = List.of("option 1","option 2","option 3");
        Set<String> visibilities = Set.of("ROLE_RANDOM");
        filterDto = FilterDtoTest.buildFilterDto("Filter name", options, true, visibilities);
    }

    @Test
    @DisplayName("Serialization LanguageDto test")
    @SneakyThrows({JsonProcessingException.class, IOException.class})
    void rightSerializationTest(){
        FilterDto dtoSerializable = filterDto;
        String jsonResult = mapper
                .writer(new DefaultPrettyPrinter().withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE))
                .writeValueAsString(dtoSerializable);

        String jsonExpected = new ResourceHelper(filterJsonPath).readResourceAsString();
        assertEquals(jsonExpected,jsonResult);
    }

    static FilterDto buildFilterDto(String title, List<String> options,
                                    boolean multipleOption, Set<String> visibilities){
        return new FilterDto(title, options, multipleOption, visibilities);
    }
}
