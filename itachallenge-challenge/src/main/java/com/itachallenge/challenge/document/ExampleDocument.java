package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExampleDocument {

    @Id
    @Field(name="id_example")
    private UUID idExample;

    @Field(name="example_text")
    private String exampleText;

}
