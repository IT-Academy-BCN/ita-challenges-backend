package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
public class ChallengeBasicInfoDtoTest {

    @Autowired
    ObjectMapper mapper;

    @Test
    @SneakyThrows({JsonProcessingException.class, IOException.class})
    void assertCorrectSerialization(){

        ChallengeBasicInfoDto origin = ChallengeBasicInfoDto.builder()
                .title("Sociis Industries")
                .level("EASY")
                .popularity(105)
                .percentage(23f)
                .languages(Set.of(new LanguageDto(1, "Javascript"), new LanguageDto(2, "Java")))
                .build();

        String jsonResult = mapper.writer(new DefaultPrettyPrinter().withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)).writeValueAsString(origin);
        String jsonExpected = new ResourceHelper("json/OneBasicChallange.json").readResourceAsString();
        Assertions.assertEquals(jsonExpected, jsonResult);

    }

    /* Solventar tema Date del json salida
    @Test
    @SneakyThrows({JsonProcessingException.class, IOException.class})
    void assertCorrectSerialization(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy");

        ChallengeBasicInfoDto origin = ChallengeBasicInfoDto.builder()
                .title("Sociis Industries")
                .level("EASY")
                .creationDate(LocalDateTime.parse("Tue May 30 07:33:43 UTC 2023", formatter))
                .popularity(105)
                .languages(Set.of(new LanguageDto(1, "Javascript"), new LanguageDto(2, "Java")))
                .percentage(23f).build();

        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(origin);
        String jsonExpected = new ResourceHelper("json/OneBasicChallange.json").readResourceAsString();
        Assertions.assertEquals(jsonExpected, jsonResult);

    }*/



}
