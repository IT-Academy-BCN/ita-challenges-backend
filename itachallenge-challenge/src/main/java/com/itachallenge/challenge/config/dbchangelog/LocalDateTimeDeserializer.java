package com.itachallenge.challenge.config.dbchangelog;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.has("$date")) {
            String dateString = node.get("$date").asText();
            return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        throw new IOException("LocalDateTime not found");
    }
}
