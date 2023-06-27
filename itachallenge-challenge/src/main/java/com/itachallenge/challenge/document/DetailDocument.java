package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@AllArgsConstructor
public class DetailDocument {

    @Field(name="description")
    private String description;

    @Field(name="examples")
    private List<ExampleDocument> examples;

    @Field(name="note")
    private String note;
}
