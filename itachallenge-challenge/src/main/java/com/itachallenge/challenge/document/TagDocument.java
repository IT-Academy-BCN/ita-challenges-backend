package com.itachallenge.challenge.document;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.UUID;

@Document(collection="tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagDocument {

    @Id
    private UUID idTag;

    @Field(name="tag_name")
    private String tagName;

    @Field(name="tag_description")
    private String tagDescription;


}
