package com.itachallenge.challenge.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tag {

    @Id
    private UUID tagId;

    @Field(name="tag")
    private String tag;
}
