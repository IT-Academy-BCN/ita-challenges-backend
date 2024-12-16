package com.itachallenge.user.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.itachallenge.user.dtos.UserChallengeDto;

import java.io.IOException;

public class ChallengeJsonSerializer extends JsonSerializer<UserChallengeDto> {

    @Override
    public void serialize(UserChallengeDto challenge, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        // If score is set, then we set all fields in order to serialize the full object
        if (challenge.getScore() != null) {

            gen.writeStartObject();
            gen.writeStringField("uuid_challenge", challenge.getUuidChallenge());
            gen.writeNumberField("score", challenge.getScore());
            gen.writeEndObject();

        } else
            gen.writeString(challenge.getUuidChallenge()); // If score is not set we just serialize the uuid, no need of a full object

    }

}