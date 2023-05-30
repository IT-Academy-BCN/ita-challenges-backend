package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Detail {

    private String description;

    private List<Example> examples;

    @Field(name="note")
    private String note;
}
