package com.itachallenge.user.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.itachallenge.user.dtos.UserChallengeDto;

import java.io.IOException;

public class ChallengeJsonSerializer extends JsonSerializer<UserChallengeDto> {

    @Override
    public void serialize(UserChallengeDto challenge, JsonGenerator gen, SerializerProvider serializers) throws IOException {

            gen.writeStartObject();
            gen.writeStringField("uuid_challenge", challenge.getUuidChallenge());
            gen.writeEndObject();


    }

}