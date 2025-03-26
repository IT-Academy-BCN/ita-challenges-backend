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
    @Field(name="id_languages")
    private UUID idTag;

    @Field(name="language_name")
    @Size(max = 20, message = "El nom del tag és molt llarg")
    private String tagName;

    @Field(name="tag_description")
    private String tagDescription;


}
