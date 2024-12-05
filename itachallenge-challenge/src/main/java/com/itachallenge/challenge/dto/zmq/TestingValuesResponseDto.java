package com.itachallenge.challenge.dto.zmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.dto.TestingValueDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class TestingValuesResponseDto {
    @JsonProperty(value="uuid_challenge")
    private UUID challengeId;

    @JsonProperty(value="uuid_language")
    private UUID languageId;

    @JsonProperty(value = "test_params")
    private List<TestingValueDto> testingValues;

}
