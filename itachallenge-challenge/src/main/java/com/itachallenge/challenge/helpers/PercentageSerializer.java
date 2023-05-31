package com.itachallenge.challenge.helpers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageSerializer extends JsonSerializer<Float> {

    /*
    TO provide FE de percentage in the required way (used in ChallengeBasicDto)

    value in class: 0.25768786    range [0,1]
    value in json when serialized: "26%"
     */

    @Override
    public void serialize(Float value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        int percentage = new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).intValue();
        gen.writeString(String.format("%s%%",percentage));
    }

}
