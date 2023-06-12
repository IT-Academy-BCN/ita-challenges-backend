package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Detail {

    @Field(name="description")
    private String description;

    @Field(name="examples")
    private List<Example> examples;

    @Field(name="note")
    private String note;
}
