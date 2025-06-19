package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TagDto {

    @JsonProperty(value = "id_tag", index = 0)
    private UUID tagId;

    @JsonProperty(value = "tag_name", index = 1)
    private String tagName;

    @JsonProperty(value = "tag_description", index = 2)
    private String tagDescription;

    @JsonProperty("language_id")
    private UUID languageId;

}
