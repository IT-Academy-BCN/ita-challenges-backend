package com.itachallenge.challenge.config.dbchangelog;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class UUIDDeserializer extends JsonDeserializer<UUID> {


    @Override
    public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.has("uuid")) {
            return UUID.fromString(node.get("uuid").asText());
        }
        throw new IOException("UUID not found");
    }
}


