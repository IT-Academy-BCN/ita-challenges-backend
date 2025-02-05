package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ExampleDto {

    @JsonProperty(value = "example_id", index = 0)
    private UUID exampleId;

    @JsonProperty(value = "example_test", index = 1)
    private Map<Locale, String> exampleTest;

}
