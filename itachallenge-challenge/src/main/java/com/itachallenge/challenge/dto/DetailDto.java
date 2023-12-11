package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DetailDto {

    @JsonProperty(value = "description", index = 0)
    private Map<Locale, String> description;

    @JsonProperty(value = "examples", index = 1)
    private ExampleDto examples;

    @JsonProperty(value = "notes", index = 2)
    private Map<Locale, String> notes;

}
