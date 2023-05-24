package com.itachallenge.challenge.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="examples")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Example {

    @Id
    private UUID ExampleId;

    @Field(name="detailsId")
    private UUID detailsId;

}