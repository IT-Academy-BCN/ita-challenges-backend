package com.itachallenge.user.converters;

import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import java.nio.ByteBuffer;
import java.util.UUID;

@Component
@WritingConverter
public class UUIDToBinaryConverter implements Converter<UUID, Binary> {

    @Override
    public Binary convert(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return new Binary(buffer.array());
    }
}