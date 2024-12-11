package com.itachallenge.score.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TestParamsRequest {

    @JsonProperty(value="id_challenge", index = 0) //"uuid_challenge"))
    private UUID uuidChallenge;

    @JsonProperty(value="id_language")//("uuid_language")
    private UUID uuidLanguage;

}
