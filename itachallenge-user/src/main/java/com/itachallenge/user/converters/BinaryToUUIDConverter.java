package com.itachallenge.user.converters;

import org.bson.types.Binary;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

@Component
@ReadingConverter
public class BinaryToUUIDConverter implements Converter<Binary, UUID> {

    @Override
    public UUID convert(Binary binary) {
        byte[] bytes = binary.getData();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long mostSigBits = buffer.getLong();
        long leastSigBits = buffer.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }
}
