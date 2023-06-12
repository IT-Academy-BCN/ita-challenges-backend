package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class Example {

    @Field(name="id_example")
    private String idExample;

    @Field(name="example_text")
    private String exampleText;

}
