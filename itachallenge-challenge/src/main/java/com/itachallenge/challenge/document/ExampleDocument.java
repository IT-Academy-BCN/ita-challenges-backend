package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;
import java.util.Locale;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExampleDocument {

    @Id
    @Field(name="id_example")
    private UUID id_example;

    @Field(name="example_text")
    private Map<Locale, String> example_text;
}
