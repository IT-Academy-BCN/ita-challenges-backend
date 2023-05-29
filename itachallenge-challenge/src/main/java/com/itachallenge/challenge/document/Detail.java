package com.itachallenge.challenge.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Detail {

    @Id
    private UUID detailId;

    @Field(name="description")
    private String description;

    @Field(name="notes")
    private String notes;

    @Field(name="examples")
    private Example[] examples;
}
