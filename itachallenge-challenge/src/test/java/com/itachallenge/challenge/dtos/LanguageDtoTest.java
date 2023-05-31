package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.helpers.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
public class LanguageDtoTest {

    @Autowired
    ObjectMapper mapper;

    @Test
    @SneakyThrows({JsonProcessingException.class, IOException.class})
    void assertRightSerialization(){
        LanguageDto origin = new LanguageDto(1, "Javascript");
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(origin);
        String jsonExpected = new ResourceHelper("json/OneLanguage.json").readResourceAsString();
        Assertions.assertEquals(jsonExpected,jsonResult);
    }
}
