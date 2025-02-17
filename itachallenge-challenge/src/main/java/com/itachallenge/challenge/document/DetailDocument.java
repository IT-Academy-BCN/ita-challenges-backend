package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class DetailDocument {

    @Field(name="description")
    private String description;

    @Field(name="examples")
    private List<ExampleDocument> examples;

    @Field(name="notes")
    private Map<Locale, String> notes;

}
