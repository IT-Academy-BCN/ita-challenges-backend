package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Example {

    @Id
    @Field(name="id_example")
    private UUID idExample;

    @Field(name="example_text")
    private String exampleText;

}
