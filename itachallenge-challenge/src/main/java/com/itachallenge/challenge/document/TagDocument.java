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
    @Field(name="id_tag")
    private UUID idTag;

    @Field(name="tag_name")
    private String tagName;

    @Field(name="tag_description")
    private String tagDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagDocument that = (TagDocument) o;

        return idTag != null ? idTag.equals(that.idTag) : that.idTag == null;
    }

    @Override
    public int hashCode() {
        return idTag != null ? idTag.hashCode() : 0;
    }

}
