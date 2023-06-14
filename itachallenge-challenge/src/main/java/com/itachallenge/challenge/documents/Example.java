package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Example {

    @MongoId
    @Field(name="id_example")
    private UUID idExample;

    @Field(name="example_text")
    private String exampleText;

}
