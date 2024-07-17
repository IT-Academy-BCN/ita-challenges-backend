package com.itachallenge.challenge.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailDocument {

    @Field(name="description")
    private Map<Locale, String> description;

    @Field(name="examples")
    private List<ExampleDocument> examples;

    @Field(name="notes")
    private Map<Locale, String> notes;

}
