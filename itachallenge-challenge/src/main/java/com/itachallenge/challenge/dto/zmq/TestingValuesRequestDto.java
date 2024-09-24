package com.itachallenge.challenge.dto.zmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TestingValuesRequestDto {

    @JsonProperty(value="id_challenge", index = 0)
    private UUID challengeId;

    @JsonProperty(value="id_language", index = 1)
    private UUID languageId;

}
