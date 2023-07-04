package com.itachallenge.challenge.helper;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateTimeConverter {

    //TODO - externalizar a application.yml
    final String FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    final String ZONE = "Europe/Paris";
    //
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMAT);

    public String getFormattedCreationDateTime(LocalDateTime creationDateDocument) {
        return ZonedDateTime.of(creationDateDocument, ZoneId.of(ZONE)).format(FORMATTER);
    }

    public LocalDateTime getFormattedStringToCreationDate(String date) {
        return LocalDateTime.parse(date);
    }

}
